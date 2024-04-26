package com.food.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.food.constant.MessageConstant;
import com.food.constant.StatusConstant;
import com.food.dto.SetmealDTO;
import com.food.dto.SetmealPageQueryDTO;
import com.food.entity.Dish;
import com.food.entity.Setmeal;
import com.food.entity.SetmealDish;
import com.food.exception.DeletionNotAllowedException;
import com.food.mapper.DishMapper;
import com.food.mapper.SetmealDishMapper;
import com.food.mapper.SetmealMapper;
import com.food.result.PageResult;
import com.food.service.SetmealService;
import com.food.vo.DishItemVO;
import com.food.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    DishMapper dishMapper;
    @Transactional
    public void sava(SetmealDTO setmealDTO) {
        // 插入套餐的基本信息，并回显主键
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        // 套餐插入完以后，插入套餐和菜品的关系
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        // 首先设置套餐id
        for (SetmealDish setmealDish: setmealDishList) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        // 然后进行套餐和菜品关系的批量插入
        setmealDishMapper.insertBatch(setmealDishList);
    }

    /**
     * 套餐的分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        // 分页查询
        Page<SetmealVO> page = setmealMapper.setmealPageQuery(setmealPageQueryDTO);
        // 封装分页数据
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Transactional
    public void deleteSetmeal(List<Long> ids) {
        // 起售的套餐不能删除
        // 通过ids获取到要删除的套餐，然后查看要删除的套餐是否有起售状态
        List<Setmeal> setmealList = setmealMapper.getListById(ids);

        for (Setmeal setmeal: setmealList){
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        // 删除套餐的基本信息
        setmealMapper.deleteBatchById(ids);
        // 删除套餐中和菜品的联系
        setmealDishMapper.deleteBatchBySetmealId(ids);
    }

    @Override
    public SetmealVO getWithDishById(Long id) {
        SetmealVO setmealVO = setmealMapper.getWithDishById(id);
        return setmealVO;
    }

    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        // 修改套餐表的基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        // 根据套餐id删除原有的套餐中的与菜品的关系
        List<Long> setmealIDs = new ArrayList<>();
        setmealIDs.add(setmealDTO.getId());
        setmealDishMapper.deleteBatchBySetmealId(setmealIDs);

        // 重新插入套餐菜品关系数据
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        // 可能没有添加套餐菜品关系数据，则不需要插入
        if (setmealDishList != null && setmealDishList.size() >0){
            // 插入套餐菜品关系之前先设置套餐的id
            setmealDishList.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
            // 批量插入口味数据
            setmealDishMapper.insertBatch(setmealDishList);
        }
    }

    /**
     * 套餐的起售或停售
     * @param status
     */
    @Override
    public void StartOrStop(Integer status, Long id) {
        // 若需要将套餐起售，需要查看套餐中的菜品是否都是起售状态，若有菜品不是起售状态则无法起售
        if (status == StatusConstant.ENABLE){
            // 首先通过套餐的id，查到所关联的菜品
            List<Dish> dishList = dishMapper.getDishListBySetmealId(id);
            dishList.forEach(dish -> {
                if (dish.getStatus() == StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            });
        }
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询所有起售状态下的套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> list(Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(StatusConstant.ENABLE);
        setmeal.setCategoryId(categoryId);
        List<Setmeal> setmealList = setmealMapper.list(setmeal);
        return setmealList;
    }

    @Override
    public List<DishItemVO> getDishItemBySetmealId(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
