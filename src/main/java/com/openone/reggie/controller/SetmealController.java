package com.openone.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openone.reggie.common.R;
import com.openone.reggie.dto.SetmealDto;
import com.openone.reggie.entity.Setmeal;
import com.openone.reggie.entity.SetmealDish;
import com.openone.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
     @Autowired
     private SetmealService setmealService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
     @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto){
         setmealService.add(setmealDto);

         return R.success("添加成功");
     }

    /**
     * 分页查询套餐
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
     @GetMapping("/page")
    public R<Page> selectByPages(Integer page,Integer pageSize,String name){

         Page page1 = setmealService.selectByPages(page, pageSize, name);

         return R.success(page1);
     }

    /**
     * 根据Id查询回显数据
     * @param id
     * @return
     */
     @GetMapping("/{id}")
    public R<SetmealDto> selectById(@PathVariable Long id){
         SetmealDto setmealDto = setmealService.selectById(id);
         if(setmealDto!=null){

             return R.success(setmealDto);
         }

         return R.error("未查询到数据");
     }

    /**
     * 修改数据
     * @param setmealDto
     * @return
     */
     @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
         setmealService.update(setmealDto);

         return R.success("修改套餐成功");

     }

    /**
     * 修改套餐状态
     * @param setmeal
     * @param ids
     * @return
     */
     @PostMapping("/status/{status}")
    public R<String> updateStatus(Setmeal setmeal,Long[] ids){

         setmealService.updateStastus(setmeal,ids);

         return R.success("修改状态成功");
     }

    /**
     * 删除状态
     * @param ids
     * @return
     */
     @DeleteMapping
    public R<String> delete(Long[] ids){

         setmealService.delete(ids);

         return R.success("删除套餐成功");

     }

    /**
     * 查询根据分类查套餐
     * @param categoryId
     * @return
     */
     @GetMapping("/list")
    public R<List<Setmeal>> selectSetmeal(Long categoryId){
         List<Setmeal> setmeals = setmealService.selectSetmeal(categoryId);

         if (setmeals!=null){

            return R.success(setmeals);
         }

         return R.error("查询失败");
     }

     @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> selectSetmealDish(Long id){

         List<SetmealDish> setmealDishes = setmealService.selectSetmealDish(id);

         return R.success(setmealDishes);
     }

}
