package com.jiang.duck.rpc.core.register;


import com.jiang.duck.rpc.core.register.impl.EtcdRegister;
import com.jiang.duck.rpc.core.spi.SpiLoader;

/**
 * 注册中心生成工厂
 */
public class RegisterFactory {

    /**
     * 静态代码块，会随着类的加载而加载：
     */
    static {
        SpiLoader.load(Register.class);
    }

    /**
     * 默认序注册中心
     */
    private static final Register DEFAULT_REGISTER = new EtcdRegister();

    /**
     *
     *根据key =>获取哪种实例注册中心: SPI配置文件的key
     * @param key
     * @return
     */
    public static Register getInstance(String key){
        return SpiLoader.getInstance(Register.class,key);
    }
}
