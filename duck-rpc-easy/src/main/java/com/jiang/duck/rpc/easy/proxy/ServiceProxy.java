package com.jiang.duck.rpc.easy.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jiang.duck.rpc.easy.model.RpcRequest;
import com.jiang.duck.rpc.easy.model.RpcResponse;
import com.jiang.duck.rpc.easy.serializer.JdkSerializer;
import com.jiang.duck.rpc.easy.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理类
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Serializer serializer = new JdkSerializer();

        //封装请求体
        RpcRequest rpcRequest = RpcRequest.builder().
                serviceName(method.getDeclaringClass().getName()). //获取Class对象
                methodName(method.getName()). //方法名
                parameterTypes(method.getParameterTypes()). //方法参数类型
                args(args). //方法中需要传入的参数
                build();
        try {
            //序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            //发送请求
            HttpResponse httpResponse = HttpRequest.
                    post("http://localhost:8020").
                    body(bodyBytes).
                    execute();

            byte[] result = httpResponse.bodyBytes();//返回对象；
            //反序列化,反序列化后的对象
            RpcResponse rpcResponse = serializer.deserializer(result, RpcResponse.class);
            //返回结果
            return rpcResponse.getData();
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }
}
