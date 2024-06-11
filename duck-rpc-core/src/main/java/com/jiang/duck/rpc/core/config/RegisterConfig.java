package com.jiang.duck.rpc.core.config;


import com.jiang.duck.rpc.core.constant.RegisterKeys;
import lombok.Data;

/**
 * 注册中心配置类
 */
@Data
public class RegisterConfig {

    /**
     * 注册中心类型：
     */
    private String registerType= RegisterKeys.ETCD; //默认为etcd

    /**
     * 注册中心地址
     */
    private String address="http://localhost:2379";

    /**
     * 用户名
     */
    private String username;


    /**
     * 用户密码
     */
    private String password;

    /**
     * 超时时间
     */
    private Long timeout=10000L; //10 秒
}
