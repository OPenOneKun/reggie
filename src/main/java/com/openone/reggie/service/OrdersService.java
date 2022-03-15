package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.dto.OrdersDto;
import com.openone.reggie.entity.Orders;

import java.time.LocalDateTime;

public interface OrdersService extends IService<Orders> {

    //用户分页查询订单
    Page selectByPages(Integer page, Integer pageSize);
   //支付订单
    void pay(Orders orders);
    //管理端分页查询
    Page selectOredPages(Integer page, Integer pageSize, String number, LocalDateTime localDateTime);

    void updateStatus(Orders orders);

    void againOrders(Orders orders);
}
