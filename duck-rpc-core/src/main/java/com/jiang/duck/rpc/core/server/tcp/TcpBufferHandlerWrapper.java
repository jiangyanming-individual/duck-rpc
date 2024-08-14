package com.jiang.duck.rpc.core.server.tcp;

import com.jiang.duck.rpc.core.constant.ProtocolConstant;
import com.jiang.duck.rpc.core.protocol.ProtocolMessage;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * 防止占粘包的问题： 固定长度；
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }


    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        //设置大小
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.PROTOCOL_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            //初始化；
            int size = -1;
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
                    //读取消息体的长度
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    //写入信息头：
                    resultBuffer.appendBuffer(buffer);
                } else {
                    //写入信息体：
                    resultBuffer.appendBuffer(buffer);
                    //拼接完整的buffer:
                    bufferHandler.handle(resultBuffer);
                    //重置一轮：
                    parser.fixedSizeMode(ProtocolConstant.PROTOCOL_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });
        return parser;
    }
}
