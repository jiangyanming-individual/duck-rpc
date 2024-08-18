package com.jiang.example.consumer;

import com.jiang.duck.rpc.core.RpcApplication;
import com.jiang.duck.rpc.core.bootstrap.ConsumerBootStrap;
import com.jiang.duck.rpc.core.proxy.ServiceProxyFactory;
import com.jiang.example.common.model.User;
import com.jiang.example.common.service.UserService;

/**
 * 服务消费者
 */
public class ConsumerBootStrapStart {

    public static void main(String[] args) {

        ConsumerBootStrap.init();
        //消费者端获取服务：
        UserService userService = ServiceProxyFactory.getTcpProxy(UserService.class);
        User user = new User();
        user.setName("duck-rpc");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("getUserName:" + newUser.getName());
        } else {
            System.out.println("newUser is null");
        }
        short number = userService.getNumber();
        System.out.println("number:" + number);
    }
}
