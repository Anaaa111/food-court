package com.food.mapper;

import com.food.entity.AddressBook;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 新增菜品
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default)" +
            "values " +
            "(#{userId}, #{consignee}, #{sex}, #{phone}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 动态条件查询
     */
    List<AddressBook> list(AddressBook addressBook);

    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    void update(AddressBook addressBook);
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void unDefaultByUserId(AddressBook addressBook);
}
