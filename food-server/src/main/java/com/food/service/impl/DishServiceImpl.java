package com.food.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.food.constant.MessageConstant;
import com.food.constant.StatusConstant;
import com.food.dto.DishDTO;
import com.food.dto.DishPageQueryDTO;
import com.food.entity.Dish;
import com.food.entity.DishFlavor;
import com.food.entity.Setmeal;
import com.food.exception.DeletionNotAllowedException;
import com.food.mapper.DishFlavorMapper;
import com.food.mapper.DishMapper;
import com.food.mapper.SetmealDishMapper;
import com.food.mapper.SetmealMapper;
import com.food.result.PageResult;
import com.food.service.DishService;
import com.food.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    SetmealMapper setmealMapper;
    /**
     * 添加菜品及其相对应的口味
     */
    @Transactional
    public void savaWithFlavor(DishDTO dishDTO) {
        // 添加一条菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 创建时间和创建人可以通过aop实现
        // 插入，需要进行主键回显，后续插入口味时需要用到
        dishMapper.insert(dish);
        // 获取id当作口味表中的菜品id
        Long dishId = dish.getId();
        // 添加该菜品对应的多条口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 可能没有添加口味，则不需要插入
        if (flavors != null && flavors.size() >0){
            // 插入口味数据之前先设置菜品的id
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            // 批量插入口味数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        // 分页查询
        Page<DishVO> page = dishMapper.dishPageQuery(dishPageQueryDTO);
        // 封装分页数据
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;

    }
    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @Override
    public void deleteDishWithFlavor(List<Long> ids) {
        // 判断该菜品是否能够删除
        // 1.起售状态下的菜品不能删除，根据菜品id和起售状态查询
        Integer dishCount = dishMapper.getByIdAndStatus(ids, StatusConstant.ENABLE);
        if (dishCount > 0){
            // 有菜品为起售状态，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        // 2.关联了套餐的菜品不能删除
        // 根据菜品id批量查询套餐id
        List<Long> setMealIds = setmealDishMapper.getSetMealIdsByDIshIds(ids);
        if (setMealIds != null && setMealIds.size() >0){
            // 关联了套餐，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品
        dishMapper.deleteByIds(ids);
        // 删除菜品对应的口味
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询信息
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        //  多表查询
        DishVO dishVO = dishMapper.getByIdWithFlavor(id);
        System.out.println("dishVO = " + dishVO);
        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        // 修改菜品表的基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        // 根据菜品id删除原有的口味数据
        List<Long> dishIds = new ArrayList<>();
        dishIds.add(dishDTO.getId());
        dishFlavorMapper.deleteByDishIds(dishIds);

        // 重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 可能没有添加口味，则不需要插入
        if (flavors != null && flavors.size() >0){
            // 插入口味数据之前先设置菜品的id
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            // 批量插入口味数据
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    @Override
    public void startOrstop(Integer status, Long id) {
        Dish dish = new Dish();
        // 更新时间和更新人会通过aop设置
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
        // 更新完以后需要将该菜品相关联的套餐全部停售
        if (status == StatusConstant.DISABLE){
            // 若你要将菜品停售，则将对应的套餐全部停售（可以直接使用之前写的批量插入，省得再写方法）
            // 第一步：先查出该菜品相对于的套餐表的id
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            List<Long> setMealIds = setmealDishMapper.getSetMealIdsByDIshIds(dishIds);
            if (setMealIds != null && setMealIds.size() > 0){
                // 开始修改，最好不要用批量修改，这样写的修改语句就不是通用的了
                for (Long setMealId: setMealIds) {
                    Setmeal setmeal = new Setmeal();
                    setmeal.setId(setMealId);
                    setmeal.setStatus(StatusConstant.DISABLE);
                    // 开始修改套餐表
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 通过分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getDishListByCategory(Long categoryId) {
        Dish dish = new Dish();
        dish.setStatus(StatusConstant.ENABLE);
        dish.setCategoryId(categoryId);
        /**
         * 可以使用动态条件查询，这样就可以通用了，搜索框？分页查询都可以使用这个
         */
        List<Dish> list = dishMapper.getDishList(dish);
        return list;
    }

    @Override
    public List<DishVO> getDishWithFlavor(Long categoryId) {
        Dish dish = new Dish();
        dish.setStatus(StatusConstant.ENABLE);
        dish.setCategoryId(categoryId);
        List<DishVO> dishVOList = dishMapper.getDishWithFlavor(dish);
        return dishVOList;
    }
}
