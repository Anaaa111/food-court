package com.food.service;

import com.food.dto.EmployeeDTO;
import com.food.dto.EmployeeLoginDTO;
import com.food.dto.EmployeePageQueryDTO;
import com.food.entity.Employee;
import com.food.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     * @param employeeDTO
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    Employee getById(Long id);

    void updateEmployeeInfo(EmployeeDTO employeeDTO);

    void startOrStop(Integer status, Long id);
}
