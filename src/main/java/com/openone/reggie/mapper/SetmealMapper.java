package com.openone.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openone.reggie.dto.SetmealDto;
import com.openone.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

   //@Select("SELECT s.id,c.name category_name,s.name,s.`price`,s.`status`,s.`code`,s.`description`,s.`image`,s.`create_time`,s.`update_time`,s.`create_user`,s.`update_user`,s.`is_deleted` FROM setmeal s,(SELECT id,NAME FROM category WHERE TYPE = 2) c WHERE s.category_id = c.id")
   //List<SetmealDto> selectAll(String name);
}
