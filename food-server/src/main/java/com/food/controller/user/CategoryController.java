package com.food.controller.user;

import com.food.entity.Category;
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
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
@Api(tags = "用户端分类相关接口")
public class CategoryController {

    @Autowired
    CategoryService categoryService;
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
