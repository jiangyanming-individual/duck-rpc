package com.jiang.duck.rpc.core.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiang.duck.rpc.core.model.RpcRequest;
import com.jiang.duck.rpc.core.model.RpcResponse;

import java.io.IOException;

/**
 * Json
 * 使用jackSon序列化和反序列化
 */
public class JsonSerializer implements Serializer{

    //JackSon 序列化Mapper
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {

        //序列化： 对象=>字节数组
      return OBJECT_MAPPER.writeValueAsBytes(obj);

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

        T obj = OBJECT_MAPPER.readValue(bytes, type);
        //由于Object对象 会在序列化时将Object原始对象擦除，编程LinkedHashMap。
        // 在Java中，泛型是通过类型除来实现的，这意味着在编译期间，泛型类型信息会被擦除。
        // 当使用Object进行操作时，编译器将泛型类型擦除为其原始类型。
        // 这是为了保持与旧代码的向后兼容性并且减少运行时开销。

        //这一部分是进行特殊的处理：
        if (obj instanceof RpcRequest){
           return handleRpcRequest((RpcRequest) obj,type);
        }
        if (obj instanceof RpcResponse){
            return handleRpcResponse((RpcResponse) obj,type);
        }
        //返回数据
        return obj;
    }

    /**
     * 处理RpcRequest
     * @param request
     * @param type
     * @return
     * @param <T>
     * @throws IOException
     */
    public <T> T handleRpcRequest(RpcRequest request, Class<T> type) throws IOException {

        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] args = request.getArgs();

        for (int i = 0; i < parameterTypes.length; i++) {

            Class<?> aClass = parameterTypes[i];
            // 确定一个类(B)是不是继承来自于另一个父类(A)，一个接口(A)是不是实现了另外一个接口(B)，或者两个类相同。
            if (!aClass.isAssignableFrom(args[i].getClass())) {
                //如果arg[i]的类型和aClass不一致，需要转换类型
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i]= OBJECT_MAPPER.readValue(bytes, aClass);
            }
        }
        //强制转换
        return type.cast(request);
    }

    public <T> T handleRpcResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {

        //处理相应数据：
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(bytes,rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }


}
