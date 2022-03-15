package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.dto.DishDto;
import com.openone.reggie.entity.Dish;
import com.openone.reggie.common.CustomException;

import java.util.List;

/**
 * 菜品新增
 */
public interface DishService extends IService<Dish> {
    //分页查询菜品
    Page selectByPages(Integer page, Integer pageSize, String name);

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据ID查询回显数据
    DishDto selectById(Long id);

    //修改菜品
    void update(DishDto dishDto);

    //修改销售状态
    void updateStatus(Dish dish, Long[] ids);

    //删除菜品
    void delete(Long[] ids) throws CustomException;

    //根据菜系查询菜品
    List<DishDto> selectByCategoryId(Dish dish);
}
