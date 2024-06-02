package com.jiang.duck.rpc.core.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

//    响应数据、响应数据类型、响应信息、异常
    //响应数据
    private Object data;

   //响应数据类型
    private Class<?> dataType;

    //响应信息
    private String message;

    //异常
    private Exception exception;
}
