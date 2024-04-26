package com.food.controller.user;

import com.food.dto.OrdersPaymentDTO;
import com.food.dto.OrdersSubmitDTO;
import com.food.result.PageResult;
import com.food.result.Result;
import com.food.service.OrderService;
import com.food.vo.OrderSubmitVO;
import com.food.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("user/order")
@Slf4j
@Api(tags = "用户订单相关接口")
public class OrderController {
    @Autowired
    OrderService orderService;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 用户支付，直接修改数据库，不去调用微信支付接口
     * @param paymentDTO
     * @return
     */
    @PutMapping("payment")
    @ApiOperation("用户支付")
    public Result payment(@RequestBody OrdersPaymentDTO paymentDTO){
        log.info("用户支付：{}", paymentDTO);
        orderService.paySuccess(paymentDTO.getOrderNumber());
        return Result.success();
    }

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @GetMapping("orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        log.info("查询订单详情：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 历史订单查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> historyOrders(int page, int pageSize, Integer status){
        log.info("查询历史订单：{},{},{}",page, pageSize, status);
        PageResult pageInfo = orderService.PageQueryOrder(page, pageSize, status);
        return Result.success(pageInfo);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancelOrder(@PathVariable Long id){
        log.info("取消订单：{}", id);
        orderService.cancelOrder(id);
        return Result.success();
    }
    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("repetition/{id}")
    public Result repetitionOrder(@PathVariable Long id){
        log.info("再来一单：{}", id);
        orderService.repetitionOrder(id);
        return Result.success();
    }

    /**
     * 客户催单
     * @param id
     * @return
     */
    @GetMapping("reminder/{id}")
    @ApiOperation("客户催单")
    public Result reminder(@PathVariable Long id){
        log.info("客户催单：{}", id);
        orderService.reminder(id);
        return Result.success();
    }

}
