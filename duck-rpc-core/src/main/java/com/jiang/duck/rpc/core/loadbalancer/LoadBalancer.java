package com.jiang.duck.rpc.core.loadbalancer;


import com.jiang.duck.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器 (消费者端使用)
 */
public interface LoadBalancer {

    /**
     * @param requestParams       请求参数
     * @param serviceMetaInfoList 可选服务列表
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
