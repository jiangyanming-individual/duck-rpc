package com.jiang.example.consumer;

import com.jiang.duck.rpc.easy.proxy.ServiceProxyFactory;
import com.jiang.example.common.model.User;
import com.jiang.example.common.service.UserService;

public class EasyConsumerExample {

    public static void main(String[] args) {
        // UserService userService=new UserServiceProxy();//代理对象

        //动态代理：
        UserService userService= ServiceProxyFactory.getProxy(UserService.class);
        System.out.println(userService.hashCode());
        User user = new User();
        user.setName("yupi");
        User newUser = userService.getUser(user);
        if (newUser!=null){
            System.out.println("consumer get: "+newUser.getName());
        }else {
            System.out.println("user ==null");
        }
    }
}
