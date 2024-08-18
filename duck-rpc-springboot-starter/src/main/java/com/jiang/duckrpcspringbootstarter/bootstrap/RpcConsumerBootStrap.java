package com.jiang.duckrpcspringbootstarter.bootstrap;

import com.jiang.duck.rpc.core.proxy.ServiceProxyFactory;
import com.jiang.duck.rpc.core.proxy.ServiceTcpProxy;
import com.jiang.duckrpcspringbootstarter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class RpcConsumerBootStrap implements BeanPostProcessor {


    /**
     * Bean 初始化后执行，注入服务
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        // 获取所有的字段：
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            // 获取字段注解的属性：
            RpcReference reference = field.getAnnotation(RpcReference.class);
            if (reference != null) {
                // 为属性生成代理对象：
                Class<?> interfaceClass = reference.interfaceClass();
                if (interfaceClass == void.class) {
                    //得到字段返回类型：
                    interfaceClass = field.getType();
                }
                // 可以得到私有的属性；
                field.setAccessible(true);
                //得到服务的实例化：
                Object serviceProxy = ServiceProxyFactory.getTcpProxy(interfaceClass);
                try {
                    // 将代理对象赋值给字段。
                    field.set(bean, serviceProxy);
                    //将字段的访问恢复成默认的状态：
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败！", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}


