package com.jiang.duck.rpc.core.bootstrap;

import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.model.RpcServiceRegistryInfo;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.register.LocalRegister;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.RegisterFactory;
import com.jiang.duck.rpc.core.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供注册服务
 */
public class ProviderBootStrap {

    public static void init(List<RpcServiceRegistryInfo<?>> rpcServiceRegistryInfoList) {

        //初始化
        RpcApplication.init();
        //获取全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //遍历要注册的服务：
        for (RpcServiceRegistryInfo<?> rpcServiceRegistryInfo : rpcServiceRegistryInfoList) {
            //注册服务到本地
            String serviceName = rpcServiceRegistryInfo.getServiceName();
            Class<?> implClass = rpcServiceRegistryInfo.getImplClass();
            LocalRegister.register(serviceName, implClass);
            //获取注册中心配置
            RegisterConfig registerConfig = rpcConfig.getRegisterConfig();
            //注册中心实例化
            Register registerInstance = RegisterFactory.getInstance(registerConfig.getRegisterType());
            //获取rpc配置信息：
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            //注册服务
            try {
                registerInstance.registerService(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册未成功！");
            }
        }
        //使用自定义的Tcp协议：启动服务进行监听：
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
