package com.openone.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openone.reggie.common.BaseContext;
import com.openone.reggie.common.R;
import com.openone.reggie.entity.AddressBook;
import com.openone.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 添加地址
     *
     * @param addressBook
     * @param request
     * @return
     */
    @PostMapping
    public R<AddressBook> saveAddress(@RequestBody AddressBook addressBook, HttpServletRequest request) {
        log.info("添加地址：{}", addressBook.toString());
        Long userId = (Long) request.getSession().getAttribute("user");
        addressBook.setUserId(userId);
        addressBookService.add(addressBook);

        return R.success(addressBook);

    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        log.info("设置默认地址...");
        addressBookService.setDefault(addressBook);

        return R.success("设置成功");
    }

    /**
     * 查询所有地址
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> selectAll(){
        List<AddressBook> addressBooks = addressBookService.selectAll();

        return R.success(addressBooks);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    public R<AddressBook> getDefault(){
        AddressBook aDefault = addressBookService.getDefault();

        log.info("查询默认地址....");
        if(aDefault!=null){
            return R.success(aDefault);
        }
        return R.error("查询失败");
    }

    @GetMapping("/{id}")
    public R<AddressBook> selectById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.selectById(id);
        if(addressBook!=null){
            return R.success(addressBook);
        }

        return R.error("未查询到");

    }
}
