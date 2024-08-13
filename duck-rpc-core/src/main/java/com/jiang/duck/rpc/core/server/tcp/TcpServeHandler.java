package com.jiang.duck.rpc.core.server.tcp;

import com.jiang.duck.rpc.core.enums.ProtocolMessageTypeEnum;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.protocol.ProtocolMessage;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageDecoder;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageEncoder;
import com.jiang.duck.rpc.core.register.LocalRegister;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理器(服务提供者), 使用Tcp协议
 */

public class TcpServeHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        //处理连接：
        netSocket.handler(buffer -> {
            //1. 先解码：Buffer 转对象：
            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decoder(buffer);
            } catch (IOException e) {
                throw new RuntimeException("解码失败");
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            //处理请求：
            RpcResponse rpcResponse = new RpcResponse();
            try {
                //通过反射调用服务：
                Class<?> implClass = LocalRegister.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 返回封装结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            //发送响应：2. 编码： 返回对象转Buffer
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
                //发送响应：
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码失败");
            }
        });
    }
}
