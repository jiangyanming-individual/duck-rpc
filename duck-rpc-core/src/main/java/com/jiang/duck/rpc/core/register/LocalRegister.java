package com.jiang.duck.rpc.core.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册器:使用Map进行存储
 */
public class LocalRegister {
    /**
     * 存储注册服务
     */
    public static final Map<String,Class<?>> map=new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param implClass  Class类
     */
    public static void register(String serviceName,Class<?> implClass){
        map.put(serviceName,implClass);
    }

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName){
        return map.get(serviceName);
    }

    /**
     * 移除服务
     * @param serviceName
     */
    public static void  remove(String serviceName){
        map.remove(serviceName);
    }

}
