package com.jiang.duck.rpc.easy.proxy;

import java.lang.reflect.Proxy;

/**
 * 代理工厂类
 */
public class ServiceProxyFactory {

    /**
     * 生成代理类
     * @param serviceClass
     * @return
     * @param <T>
     *
     *
     * ClassLoader: 用于定义代理类的Classloader。
     * Class[] interfaces: 表示需要代理的接口，即serviceClass。
     * InvocationHandler: 表示要执行代理方法时的回调处理器对象，
     * 这里使用的是new ServiceProxy()来处理代理方法的调用。
     */
    public static  <T> T getProxy(Class<T> serviceClass) {
        //实例化代理类 (classLoader,interface,class)
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
