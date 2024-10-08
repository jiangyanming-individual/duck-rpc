package com.jiang.example.consumer;

import com.jiang.duck.rpc.core.config.RpcConfig;
import com.jiang.duck.rpc.core.proxy.ServiceProxy;
import com.jiang.duck.rpc.core.proxy.ServiceProxyFactory;
import com.jiang.duck.rpc.core.utils.ConfigUtils;
import com.jiang.example.common.model.User;
import com.jiang.example.common.service.UserService;

/**
 * 测试配置类工具类是否生效
 */
public class ConsumerExample {
    public static void main(String[] args) {

        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        //消费者端获取服务：
        UserService userService= ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("duck-rpc");
        User newUser = userService.getUser(user);
        if (newUser!=null){
            System.out.println("getUserName:"+ newUser.getName());
        }else {
            System.out.println("newUser is null");
        }
        short number = userService.getNumber();
        System.out.println("number:"+number);

    }
}
