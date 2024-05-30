package com.jiang.duck.rpc.easy.model;

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
    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object [] args;
}
