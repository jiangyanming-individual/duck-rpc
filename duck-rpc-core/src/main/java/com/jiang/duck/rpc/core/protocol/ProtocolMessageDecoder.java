package com.jiang.duck.rpc.core.protocol;


import com.jiang.duck.rpc.core.constant.ProtocolConstant;
import com.jiang.duck.rpc.core.enums.ProtocolMessageSerializerEnum;
import com.jiang.duck.rpc.core.enums.ProtocolMessageTypeEnum;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;
import com.jiang.duck.rpc.core.serializer.Serializer;
import com.jiang.duck.rpc.core.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 消息解码器： Buffer转对象：
 */
public class ProtocolMessageDecoder {

    public static ProtocolMessage<?> decoder(Buffer buffer) throws IOException {
        //分别读取指定位置的数据
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        //校验魔术
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("魔数不符合要求");
        }

        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        //requestId是Long
        header.setRequestId(buffer.getLong(5));
        //长度是int
        header.setBodyLength(buffer.getInt(13));

        //解决粘包的问题,获取数据
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //得到序列化器： byte 转int 自动转
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getSerializerByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("协议序列化方式不存在");
        }
        //实例化序列化器：
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("请求类型枚举类不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂时不支持该消息的类型");
        }
    }
}
