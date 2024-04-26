package com.food.mapper;

import com.food.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     */
    void insertBatch(List<DishFlavor> flavors);
    /**
     * 根据菜品id批量删除对应的口味
     */
    void deleteByDishIds(List<Long> dishIds);
}
