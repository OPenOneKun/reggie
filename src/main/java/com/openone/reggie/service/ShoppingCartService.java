package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

    //查询购物车
    List<ShoppingCart> selectCart();
   //添加购物车
    ShoppingCart add(ShoppingCart shoppingCart);
   //清空购物车
    void deleteCart();

    ShoppingCart deleteOne(ShoppingCart shoppingCart);
}
