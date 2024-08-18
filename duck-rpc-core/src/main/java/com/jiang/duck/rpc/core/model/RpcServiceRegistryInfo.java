package com.jiang.duck.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务提供者注册服务的信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcServiceRegistryInfo<T>{

    /**
     * 服务名：
     */
    private String serviceName;

    /**
     * 实现类：
     */
    private Class<? extends T> implClass;

}
