package com.jiang.duck.rpc.core.loadbalancer.impl;

import com.jiang.duck.rpc.core.loadbalancer.LoadBalancer;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 轮询处理器
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    // 使用原子类，防止并发冲突的问题
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        //服务为空直接返回null
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        //仅有一个服务不需要轮询
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // 除余数轮询：
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
