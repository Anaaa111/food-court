package com.food.service;

import com.food.dto.SetmealDTO;
import com.food.dto.SetmealPageQueryDTO;
import com.food.entity.Setmeal;
import com.food.result.PageResult;
import com.food.vo.DishItemVO;
import com.food.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void sava(SetmealDTO setmealDTO);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmeal(List<Long> ids);

    SetmealVO getWithDishById(Long id);

    void updateSetmeal(SetmealDTO setmealDTO);

    void StartOrStop(Integer status, Long id);

    /**
     * 根据分类id查询该分类下的所有套餐
     * @param categoryId
     */
    List<Setmeal> list(Long categoryId);

    List<DishItemVO> getDishItemBySetmealId(Long id);
}
