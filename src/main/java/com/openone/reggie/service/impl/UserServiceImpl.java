package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.entity.User;
import com.openone.reggie.mapper.UserMapper;
import com.openone.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 添加用户
     * @param user
     */
    @Override
    public void add(User user) {
        userMapper.insert(user);
    }

    /**
     * 根据phone查询用户
     * @param phone
     */
    @Override
    public User selectByPhone(String phone) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone,phone);

        User user = userMapper.selectOne(lqw);

        return user;
    }
}
