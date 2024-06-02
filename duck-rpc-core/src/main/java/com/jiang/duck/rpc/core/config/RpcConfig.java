package com.jiang.duck.rpc.core.config;

import lombok.Data;

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
}
