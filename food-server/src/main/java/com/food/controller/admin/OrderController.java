package com.food.controller.admin;

import com.food.dto.*;
import com.food.result.PageResult;
import com.food.result.Result;
import com.food.service.OrderService;
import com.food.vo.OrderStatisticsVO;
import com.food.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("admin/order")
@Slf4j
@Api(tags = "管理端订单相关接口")
public class OrderController {
    @Autowired
    OrderService orderService;

    /**
     * 条件查询(分页)，就是分页查询
     */
    @GetMapping("conditionSearch")
    @ApiOperation("条件查询(分页)")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO pageQueryDTO){
        log.info("条件查询(分页)：{}", pageQueryDTO);
        PageResult page = orderService.conditionSearch(pageQueryDTO);
        return Result.success(page);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    @GetMapping("statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getDetail(@PathVariable Long id){
        OrderVO orderDetail = orderService.getOrderDetail(id);
        return Result.success(orderDetail);
    }
    /**
     * 接单
     *
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }
    /**
     * 拒单
     *
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    /**
     * 取消订单
     *
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }
    /**
     * 派送订单
     */
    @PutMapping("delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     */
    @PutMapping("complete/{id}")
    @ApiOperation("派送订单")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
}
