package com.jiang.duck.rpc.core.register;

import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心实现的功能：
 */
public interface Register {


    /**
     * 初始化服务：
     * @param registerConfig :注册配置类
     */
    void init(RegisterConfig registerConfig);

    /**
     * 注册服务
     * @param serviceMetaInfo
     * @throws Exception
     */
    void registerService(ServiceMetaInfo serviceMetaInfo) throws Exception;


    /**
     * 服务注销
     * @param serviceMetaInfo
     * @throws Exception
     */
    void unRegisterService(ServiceMetaInfo serviceMetaInfo) throws Exception;


    /**
     *服务发现
     * @param serviceKey
     * @throws Exception
     */
    List<ServiceMetaInfo> discoveryRegister(String serviceKey) throws Exception;

    /**
     * 销毁服务
     */
    void destroy();


    /**
     * 心脏检测：服务端
     */
    void heartBeat();


    /**
     * 消费者端监听
     */
    void watch(String serviceNodeKey);

}
