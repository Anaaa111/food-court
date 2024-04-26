package com.food.mapper;

import com.github.pagehelper.Page;
import com.food.annotation.AutoFill;
import com.food.dto.SetmealPageQueryDTO;
import com.food.entity.Setmeal;
import com.food.enumeration.OperationType;
import com.food.vo.DishItemVO;
import com.food.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类查询套餐的数量
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据id更新套餐信息
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 插入
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分类查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> setmealPageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id批量查询
     * @param ids
     * @return
     */
    List<Setmeal> getListById(List<Long> ids);

    void deleteBatchById(List<Long> ids);

    SetmealVO getWithDishById(Long id);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 根据id查询
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
     * 根据map查询套餐数量
     */
    @Select("select count(id) from setmeal where status = #{status}")
    Integer countByStatus(Integer status);
}
