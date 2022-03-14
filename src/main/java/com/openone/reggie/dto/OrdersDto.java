package com.openone.reggie.dto;


import com.openone.reggie.entity.OrderDetail;
import com.openone.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private String name;

    private List<OrderDetail> orderDetails;

    private Integer sumNum;
	
}
