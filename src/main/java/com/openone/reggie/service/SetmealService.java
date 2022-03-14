package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.dto.SetmealDto;
import com.openone.reggie.entity.Setmeal;
import com.openone.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐需要操作两张表
    void add(SetmealDto setmealDto);

    //分页查询需要查询两张表
    Page<SetmealDto> selectByPages(Integer page, Integer pageSize, String name);

    //根据ID查询回显数据
    SetmealDto selectById(Long id);

    //修改套餐数据
    void update(SetmealDto setmealDto);

    //修改状态
    void updateStastus(Setmeal setmeal, Long[] ids);

    //删除套餐
    void delete(Long[] ids);

    //查询分类
    List<Setmeal> selectSetmeal(Long categoryId);

    List<SetmealDish> selectSetmealDish(Long id);
}
