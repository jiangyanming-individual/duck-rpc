package com.jiang.duck.rpc.core.loadbalancer.impl;

import com.jiang.duck.rpc.core.loadbalancer.LoadBalancer;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

/**
 * 一致性hash 轮询
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    // 使用TreeMap的ceilingEntry 和FirstEntry
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    // 虚拟节点的数据量
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {

        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        //构建虚拟环节点：
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            //每个节点对应服务节点是100
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }
        // 请求最小的hash
        int hash = getHash(requestParams);
        //选择接近且大于等于调用请求的hash值的虚拟节点：
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        // 如果不存在，返回第一个
        if (entry == null){
            entry=virtualNodes.firstEntry();
        }
        //返回服务：
        return entry.getValue();
    }

    /**
     * 获取hashCode
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
