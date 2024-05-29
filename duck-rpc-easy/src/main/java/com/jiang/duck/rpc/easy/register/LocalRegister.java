package com.jiang.duck.rpc.easy.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册器:
 */
public class LocalRegister {
    /**
     * 存储注册服务
     */
    public static final Map<String,Class<?>> map=new ConcurrentHashMap<String,Class<?>>();

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
