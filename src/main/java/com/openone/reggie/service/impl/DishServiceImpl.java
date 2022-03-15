package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.dto.DishDto;
import com.openone.reggie.entity.Category;
import com.openone.reggie.entity.Dish;
import com.openone.reggie.entity.DishFlavor;
import com.openone.reggie.common.CustomException;
import com.openone.reggie.mapper.CategoryMapper;
import com.openone.reggie.mapper.DishMapper;
import com.openone.reggie.service.DishFlavorService;
import com.openone.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜品新增
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<DishDto> selectByPages(Integer page, Integer pageSize, String name) {
        //创建分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //创建条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        lqw.orderByDesc(Dish::getUpdateTime);
        //分页查询
        dishMapper.selectPage(pageInfo, lqw);


        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map(new Function<Dish, DishDto>() {
            @Override
            public DishDto apply(Dish dish) {
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(dish, dishDto);
                Long categoryId = dish.getCategoryId();//分类id
                //根据id查询分类对象
                Category category = categoryMapper.selectById(categoryId);

                if (category != null) {
                    String categoryName = category.getName();
                    dishDto.setCategoryName(categoryName);
                }
                return dishDto;
            }
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);


        return dishDtoPage;
    }

    /**
     * 新增菜品,同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表dish
        dishMapper.insert(dishDto);

        Long dishId = dishDto.getId();//菜品ID

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(new Function<DishFlavor, DishFlavor>() {
            @Override
            public DishFlavor apply(DishFlavor flavor) {
                flavor.setDishId(dishId);
                return flavor;
            }
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        for (DishFlavor flavor : flavors) {
            dishFlavorService.add(flavor);
        }

    }

    /**
     * 根据Id查询回显数据
     *
     * @param id
     * @return
     */
    @Override
    public DishDto selectById(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = dishMapper.selectById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lqw);

        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品数据
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void update(DishDto dishDto) {
        this.updateById(dishDto);

        Long dishId = dishDto.getId();

        //先清空口味表
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(lqw);


        //再添加新口味表
        List<DishFlavor> collect = dishDto.getFlavors().stream().map((dishFlavor) -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(collect);

    }

    /**
     * 修改菜品状态
     * @param dish
     * @param ids
     */
    @Override
    @Transactional
    public void updateStatus(Dish dish, Long[] ids) {


        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);

        dishMapper.update(dish, lqw);
    }

    /**
     * 删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void delete(Long[] ids) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        lqw.eq(Dish::getStatus, 1);

        Integer count = dishMapper.selectCount(lqw);

        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("菜品正在售卖中，不能删除");
        }
        dishMapper.delete(lqw);

        LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(DishFlavor::getDishId, ids);
        //删除关联口味
        dishFlavorService.remove(lqw2);

        //dishMapper.delete(lqw);
    }

    /**
     * 查询套餐
     * @param dish
     * @return
     */
    @Override
    public List<DishDto> selectByCategoryId(Dish dish) {
        //创建Dish条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(dish.getName()), Dish::getName, dish.getName());
        lqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        lqw.eq(Dish::getStatus, 1);
        //添加排序条件
        lqw.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishMapper.selectList(lqw);
        //将Dish集合转换成DishDto集合
        List<DishDto> dishDtoList = dishes.stream().map((dish1) -> {
            DishDto dishDto1 = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto1);
            //查询口味
            LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(DishFlavor::getDishId,dish1.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(lqw2);
            //封装口味
            dishDto1.setFlavors(dishFlavors);

            Long categoryId = dish1.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryMapper.selectById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto1.setCategoryName(categoryName);
            }
            return dishDto1;
        }).collect(Collectors.toList());


        return dishDtoList;
    }


}
