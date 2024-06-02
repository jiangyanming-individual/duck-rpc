package com.jiang.duck.rpc.core.server;


import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.register.LocalRegister;
import com.jiang.duck.rpc.core.serializer.JdkSerializer;
import com.jiang.duck.rpc.core.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * http 请求处理器
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {

        //指定序列化器
        final Serializer jdkSerializer=new JdkSerializer();

        System.out.println("duck-rpc-easy Received request:" + request.method() + " " + request.uri());
        //1. 反序列化请求为对象，并从请求对象中获取参数。
        //异步处理http 请求 Reading Data from the Request Body
        request.bodyHandler(body->{
            //读取数据：
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest =null;
            try {
                //反序列化：
                 rpcRequest = jdkSerializer.deserializer(bytes, RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //构造响应对象：
            RpcResponse rpcResponse = new RpcResponse();
            //如果请求为null 直接返回
            if (rpcRequest == null){
                rpcResponse.setMessage("response is null");
                doResponse(request,rpcResponse,jdkSerializer);
                return;
            }

            try {
                //2. 根据服务名称从本地注册器中获取到对应的服务实现类。
                Class<?> implClass = LocalRegister.get(rpcRequest.getServiceName());
                //3. 通过反射机制调用方法，得到返回结果。
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                //实例化服务类，然后传递参数，调用方法：==> 反射机制的使用
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                //4. 对返回结果进行封装和序列化，并写入到响应中。
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
            } catch (Exception e) {
                //抛出异常：
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
            doResponse(request,rpcResponse,jdkSerializer);
        });


    }

    /**
     * 响应
     * @param request
     * @param rpcResponse
     * @param jdkSerializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse,Serializer jdkSerializer) {
        HttpServerResponse httpServerResponse =
                request.
                response().
                putHeader("content-type", "application/json"); //设置请求头，为json格式
        try {
            //序列化
            byte[] serializedData = jdkSerializer.serialize(rpcResponse);
            //传输数据
            httpServerResponse.end(Buffer.buffer(serializedData));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
