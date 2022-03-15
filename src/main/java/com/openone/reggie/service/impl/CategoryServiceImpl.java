package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.common.CustomException;
import com.openone.reggie.entity.Category;
import com.openone.reggie.entity.Dish;
import com.openone.reggie.entity.Employee;
import com.openone.reggie.entity.Setmeal;
import com.openone.reggie.mapper.CategoryMapper;
import com.openone.reggie.mapper.DishMapper;
import com.openone.reggie.mapper.EmployeeMapper;
import com.openone.reggie.mapper.SetmealMapper;
import com.openone.reggie.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品分类
     *
     * @param category
     */
    @Override
    public void add(Category category) {
        categoryMapper.insert(category);
    }

    /**
     * 分页查询菜品
     *
     * @param Page
     * @param PageSize
     * @param name
     * @return
     */
    @Override
    public Page selectByPages(Integer Page, Integer PageSize, String name) {
        //创建分页构造器
        Page pageInfo = new Page(Page, PageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //添加条件
        lqw.eq(StringUtils.isNotEmpty(name), Category::getName, name);
        lqw.orderByDesc(Category::getSort);

        return categoryMapper.selectPage(pageInfo, lqw);
    }


    /**
     * 根据Id删除菜品分类
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, id);
        Integer count = dishMapper.selectCount(lqw);

        if(count>0){
            throw new CustomException("删除失败，该分类已绑定菜品！");
        }

        LambdaQueryWrapper<Setmeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Setmeal::getCategoryId,id);
        Integer count1 = setmealMapper.selectCount(lqw2);

        if(count1>0){
            throw new CustomException("删除失败，该分类已绑定套餐！");
        }


        categoryMapper.deleteById(id);
    }

    /**
     * 根据Id查询菜品分类
     *
     * @param id
     * @return
     */
    @Override
    public Category selectById(Long id) {

        return categoryMapper.selectById(id);
    }

    /**
     * 修改菜品套餐分类
     *
     * @param category
     */
    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
    }

    /**
     * 根据type查询
     *
     * @param
     * @return
     */
    @Override
    public List<Category> selectByType(Category category) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType()!=null,Category::getType,category.getType());
        //lqw.eq(2==2,Category::getType,2);

        List<Category> categories = categoryMapper.selectList(lqw);
        return categories;
    }

}
