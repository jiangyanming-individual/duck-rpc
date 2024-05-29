package com.jiang.example.provider;

import com.jiang.example.common.model.User;
import com.jiang.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("user name is " + user.getName());
        return user;
    }
}
