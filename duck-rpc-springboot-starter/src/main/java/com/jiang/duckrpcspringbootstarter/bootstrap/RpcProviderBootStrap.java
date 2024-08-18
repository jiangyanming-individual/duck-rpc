package com.jiang.duckrpcspringbootstarter.bootstrap;

import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.register.LocalRegister;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.RegisterFactory;
import com.jiang.duckrpcspringbootstarter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class RpcProviderBootStrap implements BeanPostProcessor {

    /**
     * Bean 初始化后执行，注册服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        // 使用反射
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            Class<?> interfaceClass = rpcService.interfaceClass();
            //默认类
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 注册服务，本地注册：
            LocalRegister.register(serviceName, beanClass);

            // 全局配置：
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册到注册中心：
            RegisterConfig registerConfig = rpcConfig.getRegisterConfig();
            Register register = RegisterFactory.getInstance(registerConfig.getRegisterType());
            // 服务信息：
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                // 注册服务：
                register.registerService(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + "服务注册失败");
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

    }

}
