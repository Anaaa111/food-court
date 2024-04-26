package com.food.mapper;

import com.food.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetMealIdsByDIshIds(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishes);

    void deleteBatchBySetmealId(List<Long> setmealIds);
}
