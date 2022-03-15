package com.openone.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openone.reggie.common.R;
import com.openone.reggie.dto.DishDto;
import com.openone.reggie.entity.Dish;
import com.openone.reggie.common.CustomException;
import com.openone.reggie.service.DishFlavorService;
import com.openone.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/dish")
@RestController
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> seletByPages(Integer page, Integer pageSize, String name) {
        log.info("分页查询中...");
        Page pages = dishService.selectByPages(page, pageSize, name);
        return R.success(pages);
    }

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);

        return R.success("新增成功");

    }

    /**
     * 根据id查询回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> selectById(@PathVariable Long id) {
        DishDto dishDto = dishService.selectById(id);

        if (dishDto != null) {

            return R.success(dishDto);
        }
        return R.error("未查询到数据");
    }

    /**
     * 修改菜品数据
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.update(dishDto);
        return R.success("修改成功");
    }

    /**
     * 修改菜品状态
     *
     * @param dish
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> update(Dish dish, Long[] ids) {

        dishService.updateStatus(dish, ids);
        return R.success("修改成功");
    }

    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids) {
            dishService.delete(ids);
        return R.success("删除成功");
    }

    /**
     * 根据菜品分类查询菜品
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> selectDish(Dish dish) {

        List<DishDto> dishDtos = dishService.selectByCategoryId(dish);

        if (dishDtos != null) {
            return R.success(dishDtos);
        }

        return R.error("未查询到数据！");

    }


}
