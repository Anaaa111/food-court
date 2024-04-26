package com.food.mapper;

import com.food.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid=#{openid}")
    User getUserByOpenid(String openid);

    void insert(User user);

    Integer countByMap(Map map);
}
