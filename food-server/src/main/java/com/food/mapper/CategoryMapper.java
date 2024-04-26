package com.food.mapper;

import com.github.pagehelper.Page;
import com.food.annotation.AutoFill;
import com.food.dto.CategoryPageQueryDTO;
import com.food.entity.Category;
import com.food.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> categoryPageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            " VALUES" +
            " (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Category category);
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
    List<Category> getByType(Integer type);
}
