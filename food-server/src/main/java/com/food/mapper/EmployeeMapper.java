package com.food.mapper;

import com.github.pagehelper.Page;
import com.food.annotation.AutoFill;
import com.food.dto.EmployeePageQueryDTO;
import com.food.entity.Employee;
import com.food.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);
    @Insert("insert into employee (name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Employee employee);

    Page<Employee> employeePageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);
}
