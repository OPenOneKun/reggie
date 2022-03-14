package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.entity.SetmealDish;
import com.openone.reggie.mapper.SetmealDishMapper;
import com.openone.reggie.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper,SetmealDish> implements SetmealDishService {
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void add(SetmealDish setmealDish) {
        setmealDishMapper.insert(setmealDish);
    }


}
