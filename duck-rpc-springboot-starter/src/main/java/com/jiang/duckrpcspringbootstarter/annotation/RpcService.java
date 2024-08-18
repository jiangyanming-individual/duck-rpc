package com.jiang.duckrpcspringbootstarter.annotation;

import com.jiang.duck.rpc.core.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者提供服务的注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * 服务接口
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务的版本：
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVER_VERSION;

}
