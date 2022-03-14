package com.openone.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openone.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author OPenKUN
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
