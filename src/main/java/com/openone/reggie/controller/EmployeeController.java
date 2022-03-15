package com.openone.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openone.reggie.common.R;
import com.openone.reggie.entity.Employee;
import com.openone.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author OPenOne
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录功能
     *
     * @param request
     * @param employee
     * @return
     */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //根据页面提交的密码进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        Employee emp = employeeService.login(employee);

        //1.如果用户不存在返回登录失败结果
        if (emp == null) {
            return R.error("登录失败，用户名不存在");
        }
        //2.如果密码错误返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }
        //3.如果员工状态是禁用状态返回账号已被禁用的结果
        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用！");
        }

        //4.登录成功，将员工ID存入Session并返回结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }


    /**
     * 登出功能实现
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> add(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息{}", employee.toString());


      /*  //记录创建修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());


        //获取当前登录用户ID
        Long empId = (Long) request.getSession().getAttribute("employee");
        //记录修改创建人ID
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        //添加员工
        employeeService.add(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     *
     * @param page     当前查询页码
     * @param pageSize 每页展示记录数
     * @param name     员工姓名 - 可选参数
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);
        //调用service分页查询方法
        Page page1 = employeeService.selectByPages(page, pageSize, name);

        //返回响应结果
        return R.success(page1);
    }


    /**
     * 根据ID查询员工数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> seletById(@PathVariable Long id) {
        //记录日志
        log.info("根据id查询员工数据...");
    /*
        //创建条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //构造条件为id相等
        lqw.eq(Employee::getId,id);*/

        //查询
        Employee employee = employeeService.selectById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("未查询到该用户数据");
    }

    /**
     * 修改员工数据
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("修改员工数据：{}", employee.toString());
        //执行修改操作
        employeeService.update(employee);
        return R.success("修改成功");
    }

}
