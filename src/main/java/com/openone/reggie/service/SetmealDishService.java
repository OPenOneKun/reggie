package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.entity.SetmealDish;


public interface SetmealDishService extends IService<SetmealDish> {
    //添加套餐菜品
    void add(SetmealDish setmealDish);

}
