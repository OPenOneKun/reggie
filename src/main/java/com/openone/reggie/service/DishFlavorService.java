package com.openone.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.openone.reggie.entity.DishFlavor;

import java.util.List;

/**
 * 口味新增
 */
public interface DishFlavorService extends IService<DishFlavor> {
      void add(DishFlavor flavors);
}
