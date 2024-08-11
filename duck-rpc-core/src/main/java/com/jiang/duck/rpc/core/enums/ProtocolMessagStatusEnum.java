package com.jiang.duck.rpc.core.enums;

import lombok.Getter;

/**
 * 协议请求状态枚举类：
 */

@Getter
public enum ProtocolMessagStatusEnum {


    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private String text;

    private int value;

    ProtocolMessagStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static ProtocolMessagStatusEnum getEnumByValue(int value) {

        for (ProtocolMessagStatusEnum anEnum : ProtocolMessagStatusEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        //如果没有返回null;
        return null;
    }

}
