package com.food.controller.user;

import com.food.entity.Setmeal;
import com.food.result.Result;
import com.food.service.SetmealService;
import com.food.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmealController")
@RequestMapping("user/setmeal")
@Slf4j
@Api(tags = "用户端套餐相关接口")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    /**
     * 根据套餐id查询包含的菜品
     * @param id
     * @return
     */

    @GetMapping("dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> getWithDishById(@PathVariable Long id){
        List<DishItemVO> dishItemVO = setmealService.getDishItemBySetmealId(id);
        return Result.success(dishItemVO);
    }

    /**
     * 根据分类id查询所有套餐
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    @ApiOperation("根据分类id查询所有套餐")
    @Cacheable(cacheNames = "setmeal", key = "#categoryId")
    public Result<List<Setmeal>> list(Long categoryId){
        List<Setmeal> setmealList = setmealService.list(categoryId);
        return Result.success(setmealList);
    }
}
