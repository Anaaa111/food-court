package com.food.service;

import com.food.dto.*;
import com.food.result.PageResult;
import com.food.vo.OrderStatisticsVO;
import com.food.vo.OrderSubmitVO;
import com.food.vo.OrderVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 支付成功
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetail(Long id);

    /**
     * 分页查询历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult PageQueryOrder(int page, int pageSize, Integer status);

    /**
     *  取消订单
     * @param id
     */
    void cancelOrder(Long id);

    /**
     * 再来一单
     * @param id
     */
    void repetitionOrder(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO pageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);
    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 客户催单
     * @param id
     */
    void reminder(Long id);
}
