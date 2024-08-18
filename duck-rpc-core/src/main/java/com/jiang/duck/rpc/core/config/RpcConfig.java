package com.jiang.duck.rpc.core.config;

import com.jiang.duck.rpc.core.constant.LoadBalancerKeys;
import com.jiang.duck.rpc.core.constant.RetryStrategyKeys;
import com.jiang.duck.rpc.core.constant.SerializerKeys;
import com.jiang.duck.rpc.core.constant.TolerantStrategyKeys;
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

    /**
     * rpc名称
     */
    private String name = "duck-rpc";
    /**
     * rpc 版本号
     */
    private String version = "1.0";

    /**
     * rpc 域名
     */
    private String serverHost = "localhost";

    /**
     * rpc 端口号
     */
    private Integer serverPort = 8020;

    /**
     * 默认序列化器：
     */
    private String serializer= SerializerKeys.JSON; //默认序列化器

    /**
     * 默认负载均衡算法：
     */
    private String LoadBalancer= LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */

    private String retry= RetryStrategyKeys.NO;

    /**
     * 容错机制
     */
    private String tolerant= TolerantStrategyKeys.FAIL_FAST;

    /**
     * 注册中心的配置
     */
    private RegisterConfig registerConfig=new RegisterConfig();

}
