package com.jiang.example.consumer;

import com.jiang.example.common.model.User;
import com.jiang.example.common.service.UserService;

public class EasyConsumerExample {

    public static void main(String[] args) {
        UserService userService=null;

        User user = new User();
        user.setName("yupi");

        User newUser = userService.getUser(user);
        if (newUser!=null){
            System.out.println(newUser.getName());
        }else {
            System.out.println("user ==null");
        }
    }
}
