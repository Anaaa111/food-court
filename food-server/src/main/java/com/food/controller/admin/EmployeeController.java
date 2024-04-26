package com.food.controller.admin;

import com.food.constant.JwtClaimsConstant;
import com.food.dto.EmployeeDTO;
import com.food.dto.EmployeeLoginDTO;
import com.food.dto.EmployeePageQueryDTO;
import com.food.entity.Employee;
import com.food.properties.JwtProperties;
import com.food.result.PageResult;
import com.food.result.Result;
import com.food.service.EmployeeService;
import com.food.utils.JwtUtil;
import com.food.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();


        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result<String> addEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}", employeeDTO);
        employeeService.addEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     */
    @GetMapping("page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> pageEmployee(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("employeePageQueryDTO: {}", employeePageQueryDTO);
        PageResult page = employeeService.page(employeePageQueryDTO);
        // page中的时间属性转换成json时会将其装换成数组，并没有将其转换成时分秒的形式
        //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")可以设置转换的json的格式
        // 或者可以配置mvc的消息转换器，统一配置时间格式
        return Result.success(page);
    }

    /**
     * 根据id查询员工(员工信息回显)
     */
    @GetMapping("{id}")
    @ApiOperation("回显员工信息")
    public Result<Employee> getEmployeeById(@PathVariable Long id){
        log.info("根据id回显员工信息：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }
    /**
     * 编辑员工
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result updateEmployeeInfo(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息：{}", employeeDTO);
        employeeService.updateEmployeeInfo(employeeDTO);
        return Result.success();
    }
    /**
     * 启用或禁用员工
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或禁用员工")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用或禁用员工：{},{}", status, id);
        employeeService.startOrStop(status, id);
        return Result.success();
    }
}
