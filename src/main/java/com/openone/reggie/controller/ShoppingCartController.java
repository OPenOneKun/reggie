package com.openone.reggie.controller;

import com.openone.reggie.common.R;
import com.openone.reggie.entity.ShoppingCart;
import com.openone.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart cart = shoppingCartService.add(shoppingCart);

        if (cart!=null){
            return R.success(cart);
        }
        return R.error("添加失败");

    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> selectCart(){
        List<ShoppingCart> shoppingCarts = shoppingCartService.selectCart();
        if (shoppingCarts!=null){
            return R.success(shoppingCarts);
        }

        return R.error("未查询到数据");
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> deleteCart(){

        shoppingCartService.deleteCart();

        return R.success("已清空");

    }

    @PostMapping("/sub")
    public R<ShoppingCart> deleteOne(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart shoppingCart1 = shoppingCartService.deleteOne(shoppingCart);
        if(shoppingCart1!=null){

            return R.success(shoppingCart1);
        }


        return R.error("删除失败");
    }
}
