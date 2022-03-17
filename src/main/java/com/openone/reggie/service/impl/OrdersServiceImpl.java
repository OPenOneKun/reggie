package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.common.BaseContext;
import com.openone.reggie.dto.OrdersDto;
import com.openone.reggie.entity.*;
import com.openone.reggie.mapper.*;
import com.openone.reggie.service.OrderDetailService;
import com.openone.reggie.service.OrdersService;
import com.openone.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 用户查询订单明细
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> selectByPages(Integer page, Integer pageSize) {
        //创建分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        //创建Dto分页构造器
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        //根据订单时间排序
        lqw.orderByDesc(Orders::getOrderTime);

        //分页查询
        ordersMapper.selectPage(pageInfo, lqw);

        //将分页查询数据拷贝到DTO，排除records
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        //获取到基本的records
        List<Orders> records = pageInfo.getRecords();
        //对records进行处理
        List<OrdersDto> ordersDtoList1 = records.stream().map(orders -> {
            OrdersDto ordersDto = new OrdersDto();
            //拷贝到dto对象
            BeanUtils.copyProperties(orders, ordersDto);

            LambdaQueryWrapper<OrderDetail> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(OrderDetail::getOrderId, orders.getId());
           //统计订单商品总数
            Integer count = orderDetailMapper.selectCount(lqw2);
            ordersDto.setSumNum(count);

            //根据Id查询订单明细
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(lqw2);
            //将订单明细进行处理
            List<OrdersDto> ordersDtoList = orderDetails.stream().map(orderDetail -> {
                Long dishId = orderDetail.getDishId();
                Long setmealId = orderDetail.getSetmealId();

                Dish dish = dishMapper.selectById(dishId);
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                if (dish != null) {
                    String name = dish.getName();
                    ordersDto.setName(name);
                }else {
                    String name = setmeal.getName();
                    ordersDto.setName(name);
                }

                return ordersDto;

            }).collect(Collectors.toList());
            //将订单明细设置到dto对象
            ordersDto.setOrderDetails(orderDetails);

            return ordersDto;
        }).collect(Collectors.toList());

        //将所有数据封装到dto分页数据
        ordersDtoPage.setRecords(ordersDtoList1);

        return ordersDtoPage;
    }


    /**
     * 支付订单
     * @param orders
     */
    @Override
    public void pay(Orders orders) {

        long orderId = IdWorker.getId();//生成订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<ShoppingCart> shoppingCarts = shoppingCartService.selectCart();
        //组装订单明细信息
        List<OrderDetail> orderDetailList = shoppingCarts.stream().map(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setName(shoppingCart.getName());
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        User user = userMapper.selectById(BaseContext.getCurrentId());
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookMapper.selectById(addressBookId);

        //组装订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(BaseContext.getCurrentId());
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        ordersMapper.insert(orders);
        //向订单明细表插入数据，多条数据
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetailMapper.insert(orderDetail);
        }
        //清空购物车数据
        shoppingCartService.deleteCart();
    }

    /**
     * 分页查询管理端订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param localDateTime
     * @return
     */
    @Override
    public Page selectOredPages(Integer page, Integer pageSize, String number, LocalDateTime localDateTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.like(number!=null,Orders::getNumber,number);
        lqw.eq(localDateTime!=null,Orders::getOrderTime,localDateTime);

        Page<Orders> ordersPage1 = ordersMapper.selectPage(ordersPage, lqw);

        return ordersPage1;
    }

    /**
     * 修改后台订单状态
     * @param orders
     */
    @Override
    public void updateStatus(Orders orders) {
        LambdaUpdateWrapper<Orders> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(Orders::getId,orders.getId());
        lqw.set(orders.getStatus()!=null,Orders::getStatus,orders.getStatus());

        ordersMapper.update(orders,lqw);

    }

    /**
     * 再来一单
     * @param orders
     */
    @Override
    public void againOrders(Orders orders) {
        LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();
        lqw.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(lqw);
        ShoppingCart shoppingCart = new ShoppingCart();
        for (OrderDetail orderDetail : orderDetails) {
        BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCartService.add(shoppingCart);
        }
    }


}
