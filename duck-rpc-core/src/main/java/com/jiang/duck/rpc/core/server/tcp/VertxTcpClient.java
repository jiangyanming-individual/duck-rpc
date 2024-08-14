package com.jiang.duck.rpc.core.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.constant.ProtocolConstant;
import com.jiang.duck.rpc.core.enums.ProtocolMessageSerializerEnum;
import com.jiang.duck.rpc.core.enums.ProtocolMessageTypeEnum;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.model.ServiceMetaInfo;
import com.jiang.duck.rpc.core.protocol.ProtocolMessage;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageDecoder;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageEncoder;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Tcp的请求客户端： 发送请求：
 */
public class VertxTcpClient {

    //发送Tcp请求：
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo selectedServiceMetaInfo ) throws ExecutionException, InterruptedException {

        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        //异步调用：
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(), netSocketAsyncResult -> {
            if (netSocketAsyncResult.succeeded()) {
                System.out.println("Connected to Tcp serve");
                NetSocket netSocket = netSocketAsyncResult.result();
                //1. 发送消息 先编码： 请求对象转Buffer
                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                // 得到序列化器的key：
                header.setSerializer((byte) ProtocolMessageSerializerEnum.getSerializerByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setRequestId(IdUtil.getSnowflakeNextId());

                // 封装ProtocolMessage:
                protocolMessage.setHeader(header);
                protocolMessage.setBody(rpcRequest);
                //请求编码：
                try {
                    Buffer encode = ProtocolMessageEncoder.encode(protocolMessage);
                    netSocket.write(encode);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误");
                }
                //2 接受数据：解码器： Buffer 转返回对象：防止粘包问题：
                TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                    try {
                        // 数据解码：
                        ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decoder(buffer);
                        //手动设置异步完成任务的结果：
                        responseFuture.complete(responseProtocolMessage.getBody());
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息解码错误");
                    }
                });
                netSocket.handler(tcpBufferHandlerWrapper);
            } else {
                System.out.println("Failed to connect to Tcp serve");
            }
        });
        RpcResponse response = responseFuture.get();
        //关闭连接
        netClient.close();
        //返回结果：
        return response;
    }

}
