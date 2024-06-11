package com.jiang.duck.rpc.core.config;

import com.jiang.duck.rpc.core.constant.SerializerKeys;
import lombok.Data;

/**
 * Rpc 框架的全局配置
 */
@Data
public class RpcConfig {

    /**
     * 是否开启mock服务
     */
    private Boolean mock=false;
    private String name = "duck-rpc";
    private String version = "1.0";
    private String serverHost = "localhost";
    private Integer serverPort = 8020;
    private String serializer= SerializerKeys.JSON; //默认序列化器

    /**
     * 注册中心的配置
     */
    private RegisterConfig registerConfig=new RegisterConfig();
}
