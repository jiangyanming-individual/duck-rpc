package com.jiang.duck.rpc.core.serializer;

import java.io.*;

public class JdkSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //传入字节输入流：
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close(); //关闭流
        return byteArrayOutputStream.toByteArray(); //转为字节数组；
    }

    @Override
    public <T> T deserializer(byte[] bytes, Class<T> type) throws IOException {

        //输入流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
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
