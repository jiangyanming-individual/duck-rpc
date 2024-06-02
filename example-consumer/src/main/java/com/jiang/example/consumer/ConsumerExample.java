package com.jiang.example.consumer;

import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.utils.ConfigUtils;

/**
 * 测试配置类工具类是否生效
 */
public class ConsumerExample {
    public static void main(String[] args) {

        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
        System.out.println(rpc.getName());
        System.out.println(rpc.getVersion());
        System.out.println(rpc.getServerPort());
    }
}
