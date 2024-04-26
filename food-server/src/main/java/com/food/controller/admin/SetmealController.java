package com.food.controller.admin;

import com.food.dto.SetmealDTO;
import com.food.dto.SetmealPageQueryDTO;
import com.food.result.PageResult;
import com.food.result.Result;
import com.food.service.SetmealService;
import com.food.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    /**
     * 保存套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("保存套餐")
    public Result savaSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("保存套餐：{}", setmealDTO);
        setmealService.sava(setmealDTO);
        return Result.success();
    }
    /**
     * 套餐的分页查询
     */
    @GetMapping("page")
    @ApiOperation("套餐的分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐的分页查询：{}", setmealPageQueryDTO);
        PageResult pageInfo = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 套餐的删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("套餐的删除")
    public Result deleteSetmeal(@RequestParam List<Long> ids){
        setmealService.deleteSetmeal(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getWithDishById(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.getWithDishById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐信息")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @PostMapping("status/{status}")
    @ApiOperation("套餐的起售停售")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result StartOrStop(@PathVariable Integer status, Long id){
        setmealService.StartOrStop(status, id);
        return Result.success();
    }
}
