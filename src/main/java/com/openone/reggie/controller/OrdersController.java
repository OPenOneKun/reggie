package com.openone.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openone.reggie.common.R;
import com.openone.reggie.dto.OrdersDto;
import com.openone.reggie.entity.Orders;
import com.openone.reggie.service.OrderDetailService;
import com.openone.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;




    @GetMapping("/page")
    public R<Page> selectOredPage(Integer page, Integer pageSize, String number, LocalDateTime localDateTime) {
        Page page1 = ordersService.selectOredPages(page, pageSize,number,localDateTime);

        return R.success(page1);

    }

    /**
     * 查询用户订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> selectUserPage(Integer page, Integer pageSize) {
        Page page1 = ordersService.selectByPages(page, pageSize);

        return R.success(page1);

    }

    /**
     * 下单结算
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> pay(@RequestBody Orders orders){

        ordersService.pay(orders);

        return R.success("支付成功");

    }

    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        ordersService.updateStatus(orders);

        return R.success("修改成功！");

    }
}
