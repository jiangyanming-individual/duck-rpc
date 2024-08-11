package com.jiang.duck.rpc.core.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 协议序列化器类型
 */
@Getter
public enum ProtocolMessageSerializerEnum {

    JDK(0, "jdk"),

    JSON(1, "json"),

    KRYO(2, "kryo"),

    HESSIAN(3, "hessian");

    private final int key;

    private final String value;


    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }


    /**
     * 获取值的列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static ProtocolMessageSerializerEnum getSerializerByValue(String value) {

        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            //判断枚举的内容是否相等：
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
