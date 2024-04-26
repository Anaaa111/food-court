package com.food.service;

import com.food.dto.CategoryDTO;
import com.food.dto.CategoryPageQueryDTO;
import com.food.entity.Category;
import com.food.result.PageResult;

import java.util.List;

public interface CategoryService {
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void addCategory(CategoryDTO categoryDTO);

    void startOrStop(Integer status, Long id);

    void updateCategoryInfo(CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    List<Category> getByType(Integer type);
}
