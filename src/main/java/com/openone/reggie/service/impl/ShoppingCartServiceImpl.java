package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.common.BaseContext;
import com.openone.reggie.entity.ShoppingCart;
import com.openone.reggie.mapper.ShoppingCartMapper;
import com.openone.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 查询购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> selectCart() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(lqw);

        return shoppingCarts;
    }

    /**
     * 添加到购物车
     *
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        log.info("购物车数据：{}", shoppingCart);
        //设置用户id，指定当前是哪个用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentId());
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        if (dishId != null) {
            //添加到购物车的是菜品
            lqw.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartMapper.selectOne(lqw);

        if (cartServiceOne != null) {
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();

            cartServiceOne.setNumber(number + 1);

            shoppingCartMapper.updateById(cartServiceOne);
        } else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return cartServiceOne;
    }

    /**
     * 清空购物车
     */
    @Override
    @Transactional
    public void deleteCart() {
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartMapper.delete(lqw);
    }

    /**
     * 减少购物车商品
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart deleteOne(ShoppingCart shoppingCart) {
        //获取请求的菜品Id
        Long dishId = shoppingCart.getDishId();
        //创建购物车条件构造器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //先判断当前登录用户id是否跟下单用户id一致
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        //判断菜品id是否为空
        if (dishId != null) {
            //如果不为空则与购物车菜品id进行菜品匹配
            lqw.eq(ShoppingCart::getDishId, dishId);
        } else {
            //否则与套餐id进行套餐匹配
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //根据条件查询出当前购物车对象
        ShoppingCart cart = shoppingCartMapper.selectOne(lqw);
        //判断购物车是否为空
        if (cart != null) {
            //如果不为空则获取到购物车中商品数量
            Integer number = cart.getNumber();
            //如果数量大于0
            if (number > 0) {
                //则执行减少数量
                cart.setNumber(number - 1);
                shoppingCartMapper.updateById(cart);
            }

            //如果等于0则执行删除操作
            if (cart.getNumber() == 0) {
                shoppingCartMapper.deleteById(cart);
            }
        }

        return cart;
    }
}
