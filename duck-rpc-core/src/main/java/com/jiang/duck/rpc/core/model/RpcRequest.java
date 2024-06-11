package com.jiang.duck.rpc.core.model;

import com.jiang.duck.rpc.core.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * PRC请求
 */
@Data
@Builder //链式调用
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
//    请求方法名、请求方法、请求参数类型、请求参数列表

    /**
     * 提供服务名
     */
    private String serviceName;

    /**
     *
     * 服务提供的方法
     */
    private String methodName;

    /**
     * 服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVER_VERSION;

    /**
     * 请求参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 请求参数列表：
     */
    private Object [] args;
}
