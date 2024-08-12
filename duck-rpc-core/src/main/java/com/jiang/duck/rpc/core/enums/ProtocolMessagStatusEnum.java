package com.jiang.duck.rpc.core.enums;

import lombok.Getter;

/**
 * 协议请求状态枚举类：
 */

@Getter
public enum ProtocolMessagStatusEnum {


    OK(20, "ok"),
    BAD_REQUEST(40, "badRequest"),
    BAD_RESPONSE(50, "badResponse");

    private int key;

    private String value;

    ProtocolMessagStatusEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 根据key 获得信息
     * @param key
     * @return
     */
    public static ProtocolMessagStatusEnum getEnumByValue(int key) {
        for (ProtocolMessagStatusEnum anEnum : ProtocolMessagStatusEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        //如果没有返回null;
        return null;
    }

}
