package com.jiang.duck.rpc.core;

import com.jiang.duck.rpc.core.config.RegisterConfig;
import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.constant.RpcConstant;
import com.jiang.duck.rpc.core.register.Register;
import com.jiang.duck.rpc.core.register.RegisterFactory;
import com.jiang.duck.rpc.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * 使用单例双重检测锁模式加载配置类
 */
@Slf4j
public class RpcApplication {

    //volatile 关键字，禁止指令重排
    private static volatile RpcConfig rpcConfig;


    /**
     * 获取配置, 使用单例的双重检测锁模式
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if (rpcConfig == null){
                    init(); //初始化
                }
            }
        }
        return rpcConfig;
    }

    /**
     * 初始化
     */
    public static void  init(){
        RpcConfig newRpcConfig;
        try {
            //如果存在就加载配置类
            newRpcConfig  = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            //如果没有，就使用默认的配置类：
            newRpcConfig=new RpcConfig();
        }
        //初始化打印日志
        init(newRpcConfig);
    }


    /**
     *  真正初始化
     * @param newRpcConfig
     */
    public static void  init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("RpcApplication init, config:{}", rpcConfig.toString());
        //初始化注册中心配置
        RegisterConfig registerConfig = rpcConfig.getRegisterConfig();
        //注册中心实例
        Register registerInstance = RegisterFactory.getInstance(registerConfig.getRegisterType()); //etcd 或者zookeeper;
        //服务初始化：
        registerInstance.init(registerConfig);
        log.info("RpcApplication init, 注册中心实例为:{}", registerInstance);

        //程序正常退出后：创建showdown hook
        System.out.println("程序正常退出，销毁服务。。。");
        Runtime.getRuntime().addShutdownHook(new Thread(registerInstance::destroy));
    }




}
