package com.jiang.example.provider;

import com.jiang.duck.rpc.core.bootstrap.ProviderBootStrap;
import com.jiang.duck.rpc.core.model.RpcServiceRegistryInfo;
import com.jiang.example.common.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者提供服务开始。。。
 */
public class ProviderBootStrapStart {

    public static void main(String[] args) {
        //初始化要新建的服务：
        List<RpcServiceRegistryInfo<?>> rpcServiceRegistryInfoList = new ArrayList<>();
        RpcServiceRegistryInfo<UserService> rpcServiceRegistryInfo = new RpcServiceRegistryInfo<>(UserService.class.getName(), UserServiceImpl.class);
        rpcServiceRegistryInfoList.add(rpcServiceRegistryInfo);
        //服务提供者注册服务：
        ProviderBootStrap.init(rpcServiceRegistryInfoList);
    }
}
