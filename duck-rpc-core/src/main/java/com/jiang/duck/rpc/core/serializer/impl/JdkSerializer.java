package com.jiang.duck.rpc.core.serializer.impl;

import com.jiang.duck.rpc.core.serializer.Serializer;

import java.io.*;

public class JdkSerializer implements Serializer {

    /**
     * 序列化 对象=> 字节流
     * @param obj
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //传入字节输入流：
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close(); //关闭流
        return byteArrayOutputStream.toByteArray(); //转为字节数组；
    }

    /**
     * 反序列化 字节流=> 对象
     * @param bytes
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {

        //输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            //返回数据类型 readObject方法：转为对象
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            objectInputStream.close();
        }
    }
}
