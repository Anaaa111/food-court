package com.food.mapper;

import com.github.pagehelper.Page;
import com.food.annotation.AutoFill;
import com.food.dto.DishPageQueryDTO;
import com.food.entity.Dish;
import com.food.enumeration.OperationType;
import com.food.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类查询菜品的数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    /**
     * 插入菜品数据
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> dishPageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id和起售状态批量查询
     * @param ids
     * @param enable
     * @return
     */
    Integer getByIdAndStatus(List<Long> ids, Integer enable);

    void deleteByIds(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    List<Dish> getDishList(Dish dish);

    List<Dish> getDishListBySetmealId(Long id);

    List<DishVO> getDishWithFlavor(Dish dish);

    /**
     * 根据id查询菜品
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据map查询菜品数量
     */
    @Select("select count(id) from dish where status = #{status}")
    Integer countByStatus(Integer status);
}
