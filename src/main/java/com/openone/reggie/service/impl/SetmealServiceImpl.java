package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.common.CustomException;
import com.openone.reggie.dto.SetmealDto;
import com.openone.reggie.entity.Category;
import com.openone.reggie.entity.Setmeal;
import com.openone.reggie.entity.SetmealDish;
import com.openone.reggie.mapper.CategoryMapper;
import com.openone.reggie.mapper.SetmealDishMapper;
import com.openone.reggie.mapper.SetmealMapper;
import com.openone.reggie.service.SetmealDishService;
import com.openone.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加套餐
     * @param setmealDto
     */
    @Override
    public void add(SetmealDto setmealDto) {
        log.info("开始添加数据：{}",setmealDto);

        setmealMapper.insert(setmealDto);

        Long DishlId = setmealDto.getId();
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes().stream().map((setmealDish) -> {
            setmealDish.setSetmealId(DishlId);
            return setmealDish;
        }).collect(Collectors.toList());


        for (SetmealDish setmealDish : setmealDishList) {
            setmealDishService.add(setmealDish);
        }
    }

    /**
     * 分页查询套餐
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> selectByPages(Integer page, Integer pageSize, String name) {
        //创建分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        //传入条件查询条件
        lqw.like(name!=null,Setmeal::getName,name);
        //分页查询Setmeal信息
        setmealMapper.selectPage(pageInfo,lqw);
        //拷贝分页对象，排除records
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //获取Setmeal表数据
        List<Setmeal> records = pageInfo.getRecords();
        //将records进行处理
        List<SetmealDto> setmealDtoList = records.stream().map(setmeal -> {
            //创建Dto对象
            SetmealDto setmealDto = new SetmealDto();
            //将遍历的setmeal对象拷贝到dto对象
            BeanUtils.copyProperties(setmeal, setmealDto);
            //获取categoryId
            Long categoryId = setmeal.getCategoryId();
            //调用categorymapper查询到对应对象
            Category category = categoryMapper.selectById(categoryId);
            //获取到categoryName
            String categoryName = category.getName();
            //将categoryName传入dto
            setmealDto.setCategoryName(categoryName);
            //返回对象
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtoList);

        return dtoPage;
    }

    /**
     * 根据Id查询回显
     * @param id
     * @return
     */
    @Override
    public SetmealDto selectById(Long id) {
        //创建dto对象
        SetmealDto setmealDto = new SetmealDto();
        //查询setmeal数据
        Setmeal setmeal = setmealMapper.selectById(id);
        //将setmeal数据拷贝到dto对象
        BeanUtils.copyProperties(setmeal,setmealDto);

        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        //传入SetmealDish中的setmealId与Setmeal中的id相等的条件
        lqw.eq(SetmealDish::getSetmealId,setmeal.getId());

        //查询出对应条件的对象集合
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw);
        //将对象集合添加到dto对应属性
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    /**
     * 修改套餐数据
     * @param setmealDto
     */
    @Override
    public void update(SetmealDto setmealDto) {
        setmealMapper.updateById(setmealDto);
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,setmealId);

        //setmealDishService.remove(lqw);
        setmealDishMapper.delete(lqw);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes().stream().map((setmealDish) -> {
            setmealDish.setSetmealId(setmealId);
            return setmealDish;
        }).collect(Collectors.toList());

        //setmealDishService.saveBatch(setmealDishList);
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDishMapper.insert(setmealDish);
        }


    }

    /**
     * 修改套餐状态
     * @param setmeal
     * @param ids
     */
    @Override
    public void updateStastus(Setmeal setmeal, Long[] ids) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<Setmeal>();
        lqw.in(Setmeal::getId,ids);

        setmealMapper.update(setmeal,lqw);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        lqw.eq(Setmeal::getStatus,1);
        Integer count = setmealMapper.selectCount(lqw);
        if(count>0){

            throw new CustomException("该套餐正在售卖，不能删除！");
        }

        LambdaQueryWrapper<Setmeal> lqw3 = new LambdaQueryWrapper<>();
        lqw3.in(Setmeal::getId,ids);
        setmealMapper.delete(lqw3);

        LambdaQueryWrapper<SetmealDish> lqw2 = new LambdaQueryWrapper<>();
        lqw2.in(SetmealDish::getSetmealId,ids);
        setmealDishMapper.delete(lqw2);

    }

    /**
     * 根据分类查询套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> selectSetmeal(Long categoryId) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId,categoryId);

        List<Setmeal> setmeals = setmealMapper.selectList(lqw);
        return setmeals;
    }

    @Override
    public List<SetmealDish> selectSetmealDish(Long id) {
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(lqw);
        return setmealDishes;
    }


}
