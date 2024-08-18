package com.jiang.duckrpcspringbootstarter.annotation;

import com.jiang.duck.rpc.core.constant.LoadBalancerKeys;
import com.jiang.duck.rpc.core.constant.RetryStrategyKeys;
import com.jiang.duck.rpc.core.constant.RpcConstant;
import com.jiang.duck.rpc.core.constant.TolerantStrategyKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/***
 * 服务消费者引用注解
 */
@Target({ElementType.FIELD}) //作用于类的字段之前
@Retention(RetentionPolicy.RUNTIME) // runtime的运行周期是最长的；
public @interface RpcReference {

    /**
     * 服务接口
     *
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务的版本：
     *
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVER_VERSION;

    /**
     * 负载均衡器
     *
     * @return
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;


    /**
     * 重试策略
     *
     * @return
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * 容错策略
     *
     * @return
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 模拟调用
     *
     * @return
     */
    boolean mock() default false;
}
