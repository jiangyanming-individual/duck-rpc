package com.jiang.duck.rpc.core.serializer;
import com.jiang.duck.rpc.core.serializer.impl.JdkSerializer;
import com.jiang.duck.rpc.core.spi.SpiLoader;

/**
 * 根据SPI生成序列化器：
 */
public class SerializerFactory {

    /**
     * 静态代码块，会随着类的加载而加载：
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器：
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例序列化器：
     * @param key
     * @return
     */
    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }

}
