package com.jiang.duck.rpc.core.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息结构封装类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 请求头
     */
    private Header header;

    /**
     * 消息体(请求或者响应)
     */
    private T body;

    /**
     * 请求头,静态内部类：
     */
    @Data
    public static class Header {
        /**
         * 魔数
         */
        private byte magic;

        /**
         * 版本
         */
        private byte version;

        /**
         * 序列化方式
         */
        private byte serializer;


        /**
         * 消息类型（请求、响应）
         */
        private byte type;


        /**
         * 状态
         */
        private byte status;


        /**
         * 请求id
         */
        private long requestId;

        /**
         * 消息体的长度
         */
        private int bodyLength;

    }

}
