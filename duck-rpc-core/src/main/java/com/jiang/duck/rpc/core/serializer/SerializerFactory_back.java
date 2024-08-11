package com.jiang.duck.rpc.core.serializer;
import com.jiang.duck.rpc.core.serializer.impl.HessianSerializer;
import com.jiang.duck.rpc.core.serializer.impl.JdkSerializer;
import com.jiang.duck.rpc.core.serializer.impl.JsonSerializer;
import com.jiang.duck.rpc.core.serializer.impl.KryoSerializer;

import java.util.HashMap;

/**
 * 单例模式 + 工厂模式 动态生成序列化
 */
public class SerializerFactory_back {

    /**
     * 直接使用map 容器进行加载：
     */
    private static final HashMap<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){{
        put("jdk",new JdkSerializer());
        put("json", new JsonSerializer());
        put("kryo",new KryoSerializer());
        put("hessian",new HessianSerializer());
    }};

    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");

    /**
     *根据key 返回序列化器
     * @param key
     * @return
     */
    public static Serializer getInstance(String key){
        return KEY_SERIALIZER_MAP.getOrDefault(key,DEFAULT_SERIALIZER);
    }

}
