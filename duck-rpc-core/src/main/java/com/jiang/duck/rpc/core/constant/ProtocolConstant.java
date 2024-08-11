package com.jiang.duck.rpc.core.constant;

/**
 * 协议常量：
 */
public interface ProtocolConstant {

    /**
     * 请求头长度：
     */
    int PROTOCOL_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
