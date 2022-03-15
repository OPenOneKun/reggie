package com.openone.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.entity.DishFlavor;
import com.openone.reggie.mapper.DishFlavorMapper;
import com.openone.reggie.service.DishFlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 口味新增
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService{
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public void add(DishFlavor flavor) {
        dishFlavorMapper.insert(flavor);
    }
}
