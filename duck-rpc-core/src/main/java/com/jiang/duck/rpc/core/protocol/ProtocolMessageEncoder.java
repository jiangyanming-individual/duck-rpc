package com.jiang.duck.rpc.core.protocol;


import com.jiang.duck.rpc.core.enums.ProtocolMessageSerializerEnum;
import com.jiang.duck.rpc.core.serializer.Serializer;
import com.jiang.duck.rpc.core.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 编码器： 对象转 Buffer:
 */
public class ProtocolMessageEncoder {

    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        //向缓冲区写入数据
        Buffer buffer = Buffer.buffer();
        //写入头部的数据：
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        //得到序列化方式
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getSerializerByKey(header.getSerializer());
        //序列化数据
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        //序列化数据：body:
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        //添加消息体的长度
        buffer.appendInt(bodyBytes.length);
        //添加请求体数据：body
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
