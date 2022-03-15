package com.openone.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openone.reggie.common.R;
import com.openone.reggie.entity.Category;
import com.openone.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 菜品套餐增加
     * @param category
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Category category){
        log.info("category:{}",category);
        //执行增加方法
        categoryService.add(category);
        return R.success("新增成功");
    }

    /**
     * 分页查询菜品分类
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> selectByPages(Integer page,Integer pageSize,String name){
       Page page1 = categoryService.selectByPages(page, pageSize, name);
        return R.success(page1);
    }

    /**
     * 删除菜品分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(Long id){
        categoryService.deleteById(id);
        return R.success("删除成功");
    }

    /**
     * 根据Id查询回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Category> selectById(@PathVariable Long id){
        Category category = categoryService.selectById(id);

        if(category!=null){
            return R.success(category);
        }
       return R.error("未查询到数据");
    }

    /**
     * 修改菜品套餐
     * @param category
     * @return
     */
   @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.update(category);

        return R.success("修改成功");
   }

    /**
     * 根据菜品类型查询
     * @param
     * @return
     */
  /* @GetMapping("/list")
    public R<List<Category>> selectByType(Integer type){
       List<Category> categories = categoryService.selectByType(type);

       if(categories!=null){

           return R.success(categories);
       }
       return R.error("未查询到菜品分类信息");
   }*/

    @GetMapping("/list")
    public R<List<Category>> selectByType(Category category){


        List<Category> categories = categoryService.selectByType(category);

        if(categories!=null){

            return R.success(categories);
        }
        return R.error("未查询到菜品分类信息");
    }









}
