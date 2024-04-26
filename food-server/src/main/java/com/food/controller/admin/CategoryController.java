package com.food.controller.admin;

import com.food.dto.CategoryDTO;
import com.food.dto.CategoryPageQueryDTO;
import com.food.entity.Category;
import com.food.result.PageResult;
import com.food.result.Result;
import com.food.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 分类分页查询
     */
    @GetMapping("page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> pageCategory(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("categoryPageQueryDTO: {}", categoryPageQueryDTO);
        PageResult page = categoryService.page(categoryPageQueryDTO);
        return Result.success(page);
    }

    /**
     * 新增分类
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }
    /**
     * 启用禁用分类
     */
    @PostMapping("status/{status}")
    @ApiOperation("启用或禁用分类")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用或禁用分类：{},{}", status, id);
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PutMapping
    @ApiOperation("修改分类信息")
    public Result updateCategoryInfo(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类信息：{}", categoryDTO);
        categoryService.updateCategoryInfo(categoryDTO);
        return Result.success();
    }
    /**
     * 删除分类信息
     */
    @DeleteMapping
    @ApiOperation("删除分类信息")
    public Result deleteCategory(Long id){
        log.info("删除分类信息：{}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 根据类型获取分类
     */
    @GetMapping("list")
    @ApiOperation("根据类型获取分类")
    public Result<List<Category>> getByType(Integer type){
        log.info("根据类型获取分类:{}", type);
        List<Category> categoryList = categoryService.getByType(type);
        return Result.success(categoryList);
    }


}
