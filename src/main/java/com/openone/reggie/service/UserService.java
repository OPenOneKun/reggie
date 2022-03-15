package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.entity.User;

public interface UserService extends IService<User> {

    //新增用户
    void add(User user);
    //根据手机号查询用户
    User selectByPhone(String phone);
}
