package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.common.R;
import com.openone.reggie.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    //添加地址
    AddressBook add(AddressBook addressBook);
    //设置默认地址
    void setDefault(AddressBook addressBook);
    //查询所有地址
    List<AddressBook> selectAll();
    //查询默认地址
    AddressBook getDefault();
    //根据Id查询地址
    AddressBook selectById(Long id);
}
