package com.jiang.example.provider;

import com.jiang.duck.rpc.easy.register.LocalRegister;
import com.jiang.duck.rpc.easy.server.VertxHttpServer;
import com.jiang.example.common.service.UserService;

public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务
        LocalRegister.register(UserService.class.getName(),UserServiceImpl.class);
        //启动web服务器:
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8020);
    }
}
