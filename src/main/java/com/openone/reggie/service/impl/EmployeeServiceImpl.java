package com.openone.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openone.reggie.entity.Employee;
import com.openone.reggie.mapper.EmployeeMapper;
import com.openone.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录功能
     *
     * @param employee
     * @return
     */
    @Override
    public Employee login(Employee employee) {
        //根据页面提交的用户名查询数据库,创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //查询条件
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //执行查询对象操作
        return employeeMapper.selectOne(queryWrapper);

    }


    /**
     * 新增员工
     *
     * @param employee
     */
    @Override
    public void add(Employee employee) {
        //设置初始密码为123456，需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //执行添加操作
        employeeMapper.insert(employee);
    }


    /**
     * 员工信息分页查询
     *
     * @param page     当前查询页码
     * @param pageSize 每页展示记录数
     * @param name     员工姓名 - 可选参数
     * @return
     */
    @Override
    public Page selectByPages(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //判断是否添加搜索姓名查询过滤条件
        lqw.like(StringUtils.isNotBlank(name), Employee::getName, name);
        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);


        return employeeMapper.selectPage(pageInfo, lqw);
    }

    /**
     * 根据Id查询员工
     *
     * @param id
     * @return
     */
    @Override
    public Employee selectById(Long id) {

        return employeeMapper.selectById(id);
    }

    /**
     * 修改员工
     *
     * @param employee
     */
    @Override
    public void update(Employee employee) {

        employeeMapper.updateById(employee);
    }
}
