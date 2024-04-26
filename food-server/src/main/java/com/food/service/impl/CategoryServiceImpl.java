package com.food.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.food.constant.MessageConstant;
import com.food.constant.StatusConstant;
import com.food.dto.CategoryDTO;
import com.food.dto.CategoryPageQueryDTO;
import com.food.entity.Category;
import com.food.exception.DeletionNotAllowedException;
import com.food.mapper.CategoryMapper;
import com.food.mapper.DishMapper;
import com.food.mapper.SetmealMapper;
import com.food.result.PageResult;
import com.food.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class  CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    /**
     * 分类的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        // 分页查询
        Page<Category> page = categoryMapper.categoryPageQuery(categoryPageQueryDTO);
        // 封装分页数据
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 新增分类
     * @param categoryDTO
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        // 给category赋值
        BeanUtils.copyProperties(categoryDTO, category);
        // 设置分类状态(默认为禁用)
        category.setStatus(StatusConstant.DISABLE);
        // 设置创建时间,和创建人id，使用aop实现
        // 插入到分类表中
        categoryMapper.insert(category);
    }

    /**
     * 启用或禁用分类
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        categoryMapper.update(category);
    }

    /**
     * 修改分类信息
     * @param categoryDTO
     */
    @Override
    public void updateCategoryInfo(CategoryDTO categoryDTO) {
        Category category = new Category();
        // 给category赋值
        BeanUtils.copyProperties(categoryDTO, category);
        // 设置修改时间和修改人id，使用aop实现
        // 进行修改
        categoryMapper.update(category);
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void deleteCategory(Long id) {
        // 删除分类之前，需要先查询该分类下是否有菜品或者套餐
        Integer countDish = dishMapper.countByCategoryId(id);
        if (countDish > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        Integer countSetmeal = setmealMapper.countByCategoryId(id);
        if (countSetmeal > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 进行删除
        categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> getByType(Integer type) {
        return categoryMapper.getByType(type);
    }
}
