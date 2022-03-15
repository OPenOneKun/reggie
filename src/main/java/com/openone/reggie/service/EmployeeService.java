package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.entity.Employee;

import java.util.List;

public interface EmployeeService extends IService<Employee> {
    //登录操作
    Employee login(Employee employee);
    //添加员工
    void add(Employee employee);
    //分页查询员工
    Page selectByPages(int page, int pageSize, String name);
    //根据Id查询员工
    Employee selectById(Long id);
    //修改员工数据
    void update(Employee employee);
}
