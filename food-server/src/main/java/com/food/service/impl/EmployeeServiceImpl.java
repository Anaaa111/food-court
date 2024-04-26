package com.food.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.food.constant.MessageConstant;
import com.food.constant.PasswordConstant;
import com.food.constant.StatusConstant;
import com.food.dto.EmployeeDTO;
import com.food.dto.EmployeeLoginDTO;
import com.food.dto.EmployeePageQueryDTO;
import com.food.entity.Employee;
import com.food.exception.AccountLockedException;
import com.food.exception.AccountNotFoundException;
import com.food.exception.PasswordErrorException;
import com.food.mapper.EmployeeMapper;
import com.food.result.PageResult;
import com.food.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     */
    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        /**
         * 用户名唯一性，可以实现全局异常处理器来捕捉
         */
        // 构建新增员工实体类
        Employee employee = new Employee();
        // 将从前端接收的信息拷贝到员工实体类上
        BeanUtils.copyProperties(employeeDTO, employee);
        // 密码设置(设置默认值为123456)
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        // 设置状态码(启用或禁用)
        employee.setStatus(StatusConstant.ENABLE);
        // 设置创建时间，设置创建人id，使用aop实现
        // 插入到员工表中
        employeeMapper.insert(employee);
    }
    /**
     * 分页查询
     */
    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        // 设置分页参数(Mybatis的PageHelper插件)
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        // 分页查询
        Page<Employee> page = employeeMapper.employeePageQuery(employeePageQueryDTO);
        // 封装分页数据
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }
    /**
     * 根据id回显员工信息
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }
    /**
     * 根据id修改员工信息
     */
    @Override
    public void updateEmployeeInfo(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 将DTO复制给员工实体
        BeanUtils.copyProperties(employeeDTO,employee);
        // 设置修改时间和修改人，使用aop实现
        // 进行修改
        employeeMapper.update(employee);
    }
    /**
     * 启用或禁用员工
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        // 根据id修改启用或禁用状态
        employeeMapper.update(employee);
    }

}
