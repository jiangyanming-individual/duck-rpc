package com.jiang.duck.rpc.core.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jiang.duck.rpc.core.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> KYRO_THREAD_LOCAL = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        //不提前注册所有类防止有安全问题
        kryo.setRegistrationRequired(false);
        return kryo;
    });


    /**
     * 序列化
     * @param obj
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        KYRO_THREAD_LOCAL.get().writeObject(output, obj);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 反序列化
     * @param bytes
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> T deserializer(byte[] bytes, Class<T> type) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T t = KYRO_THREAD_LOCAL.get().readObject(input, type);
        //关闭流
        input.close();
        return t;
    }
}
