package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.common.BaseContext;
import com.openone.reggie.entity.AddressBook;
import com.openone.reggie.mapper.AddressBookMapper;
import com.openone.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 添加地址
     * @param addressBook
     * @return
     */
    @Override
    @Transactional
    public AddressBook add(AddressBook addressBook) {
        addressBookMapper.insert(addressBook);
        return addressBook;
    }

    /**
     *设置默认地址
     * @param addressBook
     * @return
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        //创建修改条件构造器
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();
        //传入条件
        //lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //将地址默认值全部修改成0
        lqw.set(AddressBook::getIsDefault,0);
        addressBookMapper.update(null,lqw);

        //将需要
        addressBook.setIsDefault(1);

        addressBookMapper.updateById(addressBook);

    }

    /**
     * 查询所有地址
     * @return
     */
    @Override
    public List<AddressBook> selectAll() {
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        return addressBookMapper.selectList(lqw);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook getDefault() {
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lqw.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookMapper.selectOne(lqw);

        return addressBook;
    }

    /**
     * 根据ID查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook selectById(Long id) {
        AddressBook addressBook = addressBookMapper.selectById(id);
        return addressBook;
    }
}
