package com.jiang.duckrpcspringbootstarter.annotation;


import com.jiang.duckrpcspringbootstarter.bootstrap.RpcConsumerBootStrap;
import com.jiang.duckrpcspringbootstarter.bootstrap.RpcInitBootStrap;
import com.jiang.duckrpcspringbootstarter.bootstrap.RpcProviderBootStrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启Rpc调用的注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) // 运行时起作用
@Import({RpcInitBootStrap.class, RpcProviderBootStrap.class, RpcConsumerBootStrap.class})//导入启动类
public @interface EnableRpc {

    /**
     * 需要开启服务：
     *
     * @return
     */
    boolean needServer() default true;
}
