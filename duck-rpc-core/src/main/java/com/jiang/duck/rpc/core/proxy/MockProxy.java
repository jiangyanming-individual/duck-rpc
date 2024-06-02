package com.jiang.duck.rpc.core.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}",method.getName()); //调用方法
        return getDefaultObject(returnType); //初始化
    }

    public Object getDefaultObject(Class<?> type){
        //基本数据类型
        if (type.isPrimitive()){ //判断是不是基本数据类型
            if (type == boolean.class){
                return false;
            }else if (type == int.class){
                return 0;
            }else if (type == long.class){
                return 0L;
            }else if (type == float.class){
                return 0.0;
            }else if (type == double.class){
                return 0.0;
            }else if (type == byte.class){
                return (byte) 0;
            }else if (type == short.class){
                return (short) 0;
            }else if (type == char.class){
                return (char) 0;
            }
        }
        //对象类型，返回null
        return null;
    }
}
