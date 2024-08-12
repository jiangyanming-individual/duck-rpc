package protocol;


import cn.hutool.core.util.IdUtil;
import com.jiang.duck.rpc.core.constant.ProtocolConstant;
import com.jiang.duck.rpc.core.constant.RpcConstant;
import com.jiang.duck.rpc.core.enums.ProtocolMessagStatusEnum;
import com.jiang.duck.rpc.core.enums.ProtocolMessageSerializerEnum;
import com.jiang.duck.rpc.core.enums.ProtocolMessageTypeEnum;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.protocol.ProtocolMessage;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageDecoder;
import com.jiang.duck.rpc.core.protocol.ProtocolMessageEncoder;
import io.vertx.core.buffer.Buffer;
import org.checkerframework.checker.units.qual.C;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ProtocolTest {

    @Test
    public void testEncoderAndDecoder() throws IOException {
        ProtocolMessage<Object> protocolMessage = new ProtocolMessage<>();
        //创建一个Header类型：
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        //设置请求头：
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        //获得消息的请求类型
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        //获取消息的状态
        header.setStatus((byte) ProtocolMessagStatusEnum.OK.getKey());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("myService");
        rpcRequest.setMethodName("myMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVER_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"aaa","bbb"});

        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        //编码
        Buffer encoderBuffer = ProtocolMessageEncoder.encode(protocolMessage);
        System.out.println("encoderBuffer"+ encoderBuffer);
        //解码：
        ProtocolMessage<?> message = ProtocolMessageDecoder.decoder(encoderBuffer);
        System.out.println(message);
        Assert.assertNotNull(message);
    }
}
