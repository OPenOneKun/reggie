package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    //新增分类
    void add(Category category);
    //分页查询
    Page selectByPages(Integer Page,Integer PageSize,String name);
    //根据Id删除
    void deleteById(Long id);
    //根据Id查询菜品分类
    Category selectById(Long id);
    //修改菜品套餐分类
    void update(Category category);
    //根据type查询
    //List<Category> selectByType(Integer type);
    List<Category> selectByType(Category category);

}
