package com.jiang.duck.rpc.core.register.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.lang.Console;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.RegistryServiceCache;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegister implements Register {


    /**
     * 服务根路径：
     */
    public static final String ETCD_ROOT_URL = "/rpc/";

    /**
     * 本机注册节点的hashset，用于实现服务续期
     */
    private final Set<String> localRegisterNodeKeysSet = new HashSet();

    /**
     * 消费者缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();


    /**
     * 监听hashset：
     * 防止并发冲突
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * etcd 客户端
     */
    private Client client;

    /**
     * etcd KV 客户端
     */
    private KV kvClient;

    /**
     * 服务初始化
     *
     * @param registerConfig :注册配置类
     */
    @Override
    public void init(RegisterConfig registerConfig) {
        // create client using endpoints
        client = Client.builder().
                endpoints(registerConfig.getAddress()).
                connectTimeout(Duration.ofMillis(registerConfig.getTimeout())). //计算时间间隔
                        build();
        System.out.println("client connect success");
        kvClient = client.getKVClient();
        //心脏检测
        heartBeat();
    }


    /**
     * 服务注册：创建key,并设置过期时间，value:为服务注册信息的序列化
     *
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void registerService(ServiceMetaInfo serviceMetaInfo) throws Exception {

        //创建租赁客户端
        Lease leaseClient = client.getLeaseClient();
        //设置一个租赁时间，30秒过期
        long leaseId = leaseClient.grant(30).get().getID();
        //设置key和value:
        String registerKey = ETCD_ROOT_URL + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        //value
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        //将key value 与租约关联起来
        PutOption option = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, option).get();
        //将注册的服务存储到本地
        localRegisterNodeKeysSet.add(registerKey);

    }

    /**
     * 服务注销
     *
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void unRegisterService(ServiceMetaInfo serviceMetaInfo) throws Exception {

        String registerKey = ETCD_ROOT_URL + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
        localRegisterNodeKeysSet.remove(registerKey);//删除
    }

    /**
     * 消费者进行服务发现
     * @param serviceKey
     * @return
     * @throws Exception
     */
    @Override
    public List<ServiceMetaInfo> discoveryRegister(String serviceKey) throws Exception {

        //优先读取缓存：
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }
        //服务公共节点前缀：
        String registerPrefix = ETCD_ROOT_URL + serviceKey + "/"; //结尾以/结束
        try {
            //根据服务名查询所有的服务节点：
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.
                    get(ByteSequence.from(registerPrefix, StandardCharsets.UTF_8), getOption).
                    get().
                    getKvs();

            //重新映射：服务解析；
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(keyValue -> {
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                //监听key：
                watch(key);
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class); //转为Object对象；
            }).collect(Collectors.toList());

            //如果没有缓存：写入；
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    /**
     * 服务销毁
     */
    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        //遍历当前节点：
        for (String key : localRegisterNodeKeysSet) {
            try {
                //删除节点：
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException("当前节点下线失败", e);
            }
        }

        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }


    /**
     * 心脏检测，每10秒进行续约，如果挂了就续约不了
     */
    @Override
    public void heartBeat() {
        //使用hutool的定时任务：每10秒执行一次续约
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // key是根服务的名字：
                for (String key : localRegisterNodeKeysSet) {
                    //遍历每一个服务节点：
                    try {
                        //一个list 仅有一个节点：
                        List<KeyValue> keyValueList = kvClient.
                                get(ByteSequence.from(key, StandardCharsets.UTF_8)).
                                get().
                                getKvs();
                        //服务需要重启；
                        if (CollUtil.isEmpty(keyValueList)) {
                            continue;
                        }
                        //得到键值对
                        KeyValue keyValue = keyValueList.get(0);
                        //得到值：
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        //重新注册服务：
                        registerService(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "服务续约失败," + e);
                    }
                }

            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }


    /**
     * 消费者端实现key 是否变换的监听机制：
     *
     * @param serviceNodeKey
     */
    @Override
    public void watch(String serviceNodeKey) {
        //获取监听客户端
        Watch watchClient = client.getWatchClient();
        //添加监听的节点：
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            //清理缓存
                            registryServiceCache.clearCache();
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // create client using endpoints
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        System.out.println("client connect success");
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());
        // put key和value：
        kvClient.put(key, value).get();

        // get the CompletableFuture
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
        System.out.println(getFuture.get());

        // get the value from CompletableFuture
        GetResponse response = getFuture.get();
        System.out.println(response.getKvs());
        // 删除key
        kvClient.delete(key).get();
    }
}
