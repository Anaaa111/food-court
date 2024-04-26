package com.food.controller.admin;

import com.food.result.Result;
import com.food.service.WorkspaceService;
import com.food.vo.BusinessDataVO;
import com.food.vo.DishOverViewVO;
import com.food.vo.OrderOverViewVO;
import com.food.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@Api(tags = "工作台相关接口")
@Slf4j
@RequestMapping("admin/workspace")
public class WorkspaceController {
    @Autowired
    WorkspaceService workspaceService;

    /**
     * 查询今日运营数据
     * @return
     */
    @GetMapping("businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> getBusinessData(){
        // 获取到今天的起始时间和结束时间
        // .with()方法会创建一个新的LocalDateTime时间，被指定的字段会改成新的值(这里指定的就是时间，日期未被指定，所以日期不会被修改改，时间会被修改为最小时间)
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> overviewOrders(){
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);

        OrderOverViewVO orderOverViewVO = workspaceService.overviewOrders(beginTime, endTime);
        return Result.success(orderOverViewVO);
    }
    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> overviewDishes(){
        DishOverViewVO dishOverViewVO = workspaceService.overviewDishes();
        return Result.success(dishOverViewVO);
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("overviewSetmeals")
    @ApiOperation("查询菜品总览")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        SetmealOverViewVO setmealOverViewVO = workspaceService.overviewSetmeals();
        return Result.success(setmealOverViewVO);
    }
}
