package com.food.service;

import com.food.dto.DishDTO;
import com.food.dto.DishPageQueryDTO;
import com.food.entity.Dish;
import com.food.result.PageResult;
import com.food.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 添加菜品及其相对应的口味
     */
    void savaWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    void deleteDishWithFlavor(List<Long> ids);

    DishVO getById(Long id);

    void updateWithFlavor(DishDTO dishDTO);

    void startOrstop(Integer status, Long id);

    List<Dish> getDishListByCategory(Long categoryId);

    List<DishVO> getDishWithFlavor(Long categoryId);
}
