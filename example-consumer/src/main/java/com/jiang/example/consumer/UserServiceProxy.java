package com.jiang.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.serializer.JdkSerializer;
import com.jiang.duck.rpc.core.serializer.Serializer;
import com.jiang.example.common.model.User;
import com.jiang.example.common.service.UserService;

import java.io.IOException;

/**
 * 静态代理，实际的实现类
 */
public class UserServiceProxy implements UserService {

    //直接实现UserService接口
    public User getUser(User user) {
        //执行序列化器
        Serializer serializer = new JdkSerializer();
        //请求封装
        RpcRequest rpcRequest = RpcRequest.builder().
                serviceName(UserService.class.getName()). //服务名
                methodName("getUser"). //方法名
                parameterTypes(new Class[]{User.class}). //参数类型
                args(new Object[]{user}). // 参数
                build();

        //序列化请求
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            //发送请求，得到响应
            HttpResponse httpResponse = HttpRequest.
                    post("http://localhost:8020").
                    body(bodyBytes).
                    execute();
            result =httpResponse.bodyBytes();
            //反序列化：响应
            RpcResponse rpcResponse = serializer.deserializer(result, RpcResponse.class);
            //返回结果
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
