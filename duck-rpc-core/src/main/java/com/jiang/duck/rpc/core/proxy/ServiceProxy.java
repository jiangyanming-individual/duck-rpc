package com.jiang.duck.rpc.core.proxy;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.constant.RpcConstant;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.RegisterFactory;
import com.jiang.duck.rpc.core.serializer.Serializer;
import com.jiang.duck.rpc.core.serializer.SerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


/**
 * 动态代理类
 */
public class ServiceProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceProxy.class);

    /**
     * 消费者端调用代理
     *
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //Serializer serializer = new JdkSerializer();

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
            //发送请求
            //注册中心配置类
            RegisterConfig registerConfig = RpcApplication.getRpcConfig().getRegisterConfig();
            //实例化注册中心
            Register registerInstance = RegisterFactory.getInstance(registerConfig.getRegisterType());
            //服务节点信息：
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVER_VERSION);
            //消费者发现服务节点列表 (serviceName:serviceVersion)
            List<ServiceMetaInfo> serviceMetaInfoList= registerInstance.discoveryRegister(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂时还未有服务节点");
            }
            //先获取第一个服务节点
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            //得到服务的地址取请求：
            try (HttpResponse httpResponse = HttpRequest.
                           post(selectedServiceMetaInfo.getServiceAddress()). //向注册中心发送请求
                           body(bodyData).
                           execute()){
                byte[] result = httpResponse.bodyBytes();//返回对象；
                //反序列化,反序列化后的对象
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                //返回结果
                return rpcResponse.getData();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }
}

