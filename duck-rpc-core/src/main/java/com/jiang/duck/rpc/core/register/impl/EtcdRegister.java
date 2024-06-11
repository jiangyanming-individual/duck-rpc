package com.jiang.duck.rpc.core.register.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.register.Register;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegister implements Register {


    public static final String ROOT_URL="/rpc/";

    public final HashSet<String> localRegisterNodeKeysSet=new HashSet();

    private Client client;

    private KV kvClient;


    /**
     * 服务初始化
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
        heartBeat(); //心脏检测
    }


    /**
     * 服务注册：创建key,并设置过期时间，value:为服务注册信息的序列化
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void registerService(ServiceMetaInfo serviceMetaInfo) throws Exception {

        //创建租赁客户端
        Lease leaseClient = client.getLeaseClient();
        //设置一个租赁时间，30秒过期
        long leaseId= leaseClient.grant(30).get().getID();

        //设置key和value:
        String registerKey=ROOT_URL + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        //value
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将key value 与租约关联起来
        PutOption option = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, option).get();
        localRegisterNodeKeysSet.add(registerKey); //将注册的服务存储到本地

    }

    /**
     * 服务注销
     * @param serviceMetaInfo
     * @throws Exception
     */
    @Override
    public void unRegisterService(ServiceMetaInfo serviceMetaInfo) throws Exception {

        String registerKey=ROOT_URL + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey,StandardCharsets.UTF_8)).get();
        localRegisterNodeKeysSet.remove(registerKey);//删除
    }

    /**
     * 服务发现
     * @param serviceKey
     * @return
     * @throws Exception
     */
    @Override
    public List<ServiceMetaInfo> discoveryRegister(String serviceKey) throws Exception {
        //服务公共节点前缀：
        String registerPrefix=ROOT_URL + serviceKey + "/"; //结尾以/结束
        try {
            //根据服务名查询所有的服务节点：
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.
                    get(ByteSequence.from(registerPrefix, StandardCharsets.UTF_8), getOption).
                    get().
                    getKvs();

            //重新映射：服务解析；
            return keyValues.stream().map(keyValue -> {
                String value= keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class); //转为Object对象；
            }).collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败",e);
        }

    }

    /**
     * 服务销毁
     */
    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        if (kvClient != null){
            kvClient.close();
        }
        if (client != null){
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
                for (String key : localRegisterNodeKeysSet) {
                    //遍历每一个服务节点：
                    try {
                        //一个list 仅有一个节点：
                        List<KeyValue> keyValueList = kvClient.
                                get(ByteSequence.from(key, StandardCharsets.UTF_8)).
                                get().
                                getKvs();
                        if (CollUtil.isEmpty(keyValueList)){
                            continue; //服务需要重启；
                        }
                        //得到键值对
                        KeyValue keyValue = keyValueList.get(0);
                        //得到值：
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        //重新注册服务：
                        registerService(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key+ "服务续约失败," + e);
                    }
                }

            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
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
