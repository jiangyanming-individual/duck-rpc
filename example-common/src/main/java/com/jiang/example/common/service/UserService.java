package com.jiang.example.common.service;

import com.jiang.example.common.model.User;

public interface UserService {

    User getUser(User user);


    /**
     * 接口中的默认方法
     * @return
     */
    default short getNumber(){
        return 1;
    };
}
