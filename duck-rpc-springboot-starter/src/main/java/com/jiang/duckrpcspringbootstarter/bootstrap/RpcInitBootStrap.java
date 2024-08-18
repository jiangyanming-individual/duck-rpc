package com.jiang.duckrpcspringbootstarter.bootstrap;

import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.server.tcp.VertxTcpServer;
import com.jiang.duckrpcspringbootstarter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * rpc框架启动：
 */

@Slf4j
public class RpcInitBootStrap implements ImportBeanDefinitionRegistrar {


    public void registryBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {

        /**
         * 是否需要服务
         */
        boolean needServer = (boolean) importingClassMetadata
                .getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");
        //初始化rpc：
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        //启动服务器：
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        } else {
            log.info("不启动服务");
        }
    }
}
