package com.food.task;

import com.food.entity.Orders;
import com.food.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务调度方法
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    OrderMapper orderMapper;
    /**
     * 处理超时的待付款订单(即下单后过了15分钟还没有付款的订单)
     * 每隔一分钟就查询待付款状态下且已下单15分钟以上的订单，并将这些订单的状态都设置为已取消状态
     */
    // 每分钟触发一次
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        // 查询待付款状态下且已下单15分钟以上的订单（即下单时间小于当前时间减去15分钟）
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> ordersList = orderMapper.getOrderByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);
        if (ordersList != null && ordersList.size() > 0){
            // 有这种订单，则将状态修改为已取消状态
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(LocalDateTime.now());
                orders.setCancelReason("订单超时");
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理当天那些还在派送中的订单，自动将那些在派送中的订单设置为已完成
     */
    // 每天凌晨一点自动调用该方法
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliverOrder(){
        log.info("定时处理派送中订单：{}", LocalDateTime.now());
        // 在每天凌晨一点处理上一天中的处于派送中的订单，即order_time < 当前时间(为凌晨一点)-60分钟
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);
        List<Orders> ordersList = orderMapper.getOrderByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && ordersList.size() > 0){
            // 有这种订单，则将状态修改为已完成状态
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }

    }

}
