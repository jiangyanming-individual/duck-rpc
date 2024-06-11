package com.jiang.duck.rpc.core.spi;


import cn.hutool.core.io.resource.ResourceUtil;
import com.jiang.duck.rpc.core.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spi映射器
 */
@Slf4j
public class SpiLoader {


    /***
     * 存储已经加载的类
     * (接口名, (key, 对象实例))
     */
    private static final Map<String,Map<String,Class<?>>> loaderMap=new ConcurrentHashMap();

    /**
     * 序列化器缓存
     *
     * (类路径,对象实例)
     */
    private static final Map<String,Object> instanceCache=new ConcurrentHashMap();

    /**
     * 系统SPI
     */

    public static final String RPC_SYSTEM_SPI_DIR= "META-INF/rpc/system/";


    /**
     * 用户SPI
     */

    public static final String RPC_CONSUMER_SPI_DIR= "META-INF/rpc/custom/";


    /**
     * 动态加载类的扫描路径
     */

    private static final String[] SCANS_DIRS=new String[]{RPC_SYSTEM_SPI_DIR,RPC_CONSUMER_SPI_DIR};

    /**
     *加载类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST= Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll(){
        log.info("加载所有的SPI类");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass); //加载某一类
        }
    }

    /**
     * 加载某一类型：
     * @param loadClass
     * @return
     */
    public static Map<String,Class<?>> load(Class<?> loadClass){
        log.info("SPI加载的类为：{}",loadClass.getName());
        //用户自定义SPI，要高于系统定义的SPI
        HashMap<String, Class<?>> keyCLassMap = new HashMap<>();
        //扫描路径：
        for (String scan_dir : SCANS_DIRS) {
            //读取Resource下面的资源：
            List<URL> resources = ResourceUtil.getResources(scan_dir + loadClass.getName());
            //读取每一个资源
            for (URL resource : resources) {
                try {
                    //输入字节流
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    //缓存输入字节流
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line=bufferedReader.readLine())!=null) {
                        String[] splitLength = line.split("=");
                        if (splitLength.length>1){
                            String key = splitLength[0]; //key
                            String className = splitLength[1]; // 类
                            System.out.println("className:"+className);
                            keyCLassMap.put(key,Class.forName(className));
                        }
                    }
                }catch (Exception e){
                    log.error("SPI resource load error",e);
                }
            }

        }
        //放入loaderMap中
        loaderMap.put(loadClass.getName(),keyCLassMap);
        return keyCLassMap;
    }


    /**
     * 获取某一个类的实例化
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?> tClass,String key){

        String tClassName = tClass.getName(); //类名字
        Map<String, Class<?>> keyCLassMap = loaderMap.get(tClassName); //通过类名获得map
        if (keyCLassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s类型",tClassName));
        }
        if (!keyCLassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader 不存在 %s类型,key为%s",tClassName,key));
        }

        //根据key获取类
        Class<?> implClass = keyCLassMap.get(key);
        System.out.println("key:"+ key);
        //获得类名：
        String implClassName = implClass.getName();
        //从实例缓存中加载指定类型的实例；
        if (!instanceCache.containsKey(implClassName)){
            try {
                instanceCache.put(implClassName,implClass.newInstance());//实例化
            }catch (InstantiationException | IllegalAccessException e){
                String errorMessage=String.format("实例化%s失败",implClassName);
                log.error(errorMessage,e);
            }
        }
        //返回实例化类：
        return (T) instanceCache.get(implClassName);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        loadAll();
        System.out.println(loaderMap);
        Serializer serializer = getInstance(Serializer.class, "e");
        System.out.println(serializer);
    }

}
