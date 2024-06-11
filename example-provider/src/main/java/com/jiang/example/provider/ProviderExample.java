package com.jiang.example.provider;

import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.register.LocalRegister;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.RegisterFactory;
import com.jiang.duck.rpc.core.server.VertxHttpServer;
import com.jiang.example.common.service.UserService;

public class ProviderExample {
    public static void main(String[] args) {
        //初始化
        RpcApplication.init();

        //注册服务到本地
        String serviceName = UserService.class.getName();
        LocalRegister.register(serviceName,UserServiceImpl.class);

        //获取全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //获取注册中心配置
        RegisterConfig registerConfig = rpcConfig.getRegisterConfig();
        Register registerInstance = RegisterFactory.getInstance(registerConfig.getRegisterType());
        //注册服务到注册中心
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registerInstance.registerService(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException("服务注册未成功！");
        }
        //启动web服务器:
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        //读取配置类中的端口号
        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
