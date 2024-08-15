package com.jiang.duck.rpc.core.proxy;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.constant.ProtocolConstant;
import com.jiang.duck.rpc.core.constant.RpcConstant;
import com.jiang.duck.rpc.core.enums.ProtocolMessagStatusEnum;
import com.jiang.duck.rpc.core.enums.ProtocolMessageSerializerEnum;
import com.jiang.duck.rpc.core.enums.ProtocolMessageTypeEnum;
import com.jiang.duck.rpc.core.loadbalancer.LoadBalancer;
import com.jiang.duck.rpc.core.loadbalancer.LoadBalancerFactory;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.protocol.ProtocolMessage;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageDecoder;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageEncoder;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.RegisterFactory;
import com.jiang.duck.rpc.core.retry.RetryStrategy;
import com.jiang.duck.rpc.core.retry.RetryStrategyFactory;
import com.jiang.duck.rpc.core.serializer.Serializer;
import com.jiang.duck.rpc.core.serializer.SerializerFactory;
import com.jiang.duck.rpc.core.server.tcp.VertxTcpClient;
import io.netty.util.concurrent.CompleteFuture;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 请求发送(服务消费者) Tcp协议
 */
public class ServiceTcpProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceTcpProxy.class);

    /**
     * 消费者端调用代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //实例化序列化器：
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        String serviceName = method.getDeclaringClass().getName();
        //封装请求体
        RpcRequest rpcRequest = RpcRequest.builder().
                serviceName(serviceName). //获取Class对象
                        methodName(method.getName()). //方法名
                        parameterTypes(method.getParameterTypes()). //方法参数类型
                        args(args). //方法中需要传入的参数
                        build();
        try {
            //序列化
            byte[] bodyData = serializer.serialize(rpcRequest);
            //获取rpc配置类
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            //注册中心配置类
            RegisterConfig registerConfig = rpcConfig.getRegisterConfig();
            //实例化注册中心
            Register registerInstance = RegisterFactory.getInstance(registerConfig.getRegisterType());
            //服务节点信息：
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVER_VERSION);
            //消费者发现服务节点列表 (serviceName:serviceVersion)
            List<ServiceMetaInfo> serviceMetaInfoList = registerInstance.discoveryRegister(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂时还未有服务节点");
            }
            //先获取第一个服务节点
//            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            // 选用负载均衡算法：
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            //传入调用参数
            HashMap<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            //发送Tcp请求：使用重试策略
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetry());
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败");
        }
    }
}

