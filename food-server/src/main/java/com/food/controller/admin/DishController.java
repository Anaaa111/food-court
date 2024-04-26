package com.food.controller.admin;

import com.food.dto.DishDTO;
import com.food.dto.DishPageQueryDTO;
import com.food.entity.Dish;
import com.food.result.PageResult;
import com.food.result.Result;
import com.food.service.DishService;
import com.food.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@RequestMapping("admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 添加菜品及其相对应的口味
     */
    @PostMapping
    @ApiOperation("添加菜品及其相对应的口味")
    public Result saveWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("添加菜品及其相对应的口味:{}", dishDTO);
        dishService.savaWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品的分页查询
     */
    @GetMapping("page")
    @ApiOperation("菜品的分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品的分页查询：{}", dishPageQueryDTO);
        PageResult pageInfo = dishService.page(dishPageQueryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result deleteDishWithFlavor(@RequestParam List<Long> ids){
        dishService.deleteDishWithFlavor(ids);
        return Result.success();
    }

    /**
     * 根据id回显菜品信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation("根据id回显菜品信息")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    // @CacheEvict注解等同于cleanCatch房啊，清理dish缓存空间中的所有数据，若要指定清楚，则使用key即可，不用使用allEntries
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);

        // cleanCatch("dish_*");

        return Result.success();
    }

    @PostMapping("status/{status}")
    @ApiOperation("菜品起售停售")
    @CacheEvict(cacheNames = "dish", allEntries = true)
    public Result StartOrStop(@PathVariable Integer status, Long id){
        // 根据id更新菜品表中的status字段
        dishService.startOrstop(status, id);

        // cleanCatch("dish_*");

        return Result.success();
    }

    @GetMapping("list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getDishListByCategory(Long categoryId){
        List<Dish> list = dishService.getDishListByCategory(categoryId);
        return Result.success(list);

    }

    /**
     * 清除缓存的菜品数据
     * @param patten
     */
    private void cleanCatch(String patten){
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);
    }

}
