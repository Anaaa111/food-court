package com.food.service.impl;

import com.food.constant.StatusConstant;
import com.food.entity.Orders;
import com.food.mapper.DishMapper;
import com.food.mapper.OrderMapper;
import com.food.mapper.SetmealMapper;
import com.food.mapper.UserMapper;
import com.food.service.WorkspaceService;
import com.food.vo.BusinessDataVO;
import com.food.vo.DishOverViewVO;
import com.food.vo.OrderOverViewVO;
import com.food.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime beginTime, LocalDateTime endTime) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */
        // 计算营业额
        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", Orders.COMPLETED);

        Double turnover = orderMapper.sumTurnoverByMap(map);
        // 若今日没有营业额，则营业额设置为0
        turnover = (turnover == null ? 0.0 : turnover);

        // 计算有效订单
        Integer validOrderCount = orderMapper.countByMap(map);

        // 总订单数
        map.put("status", null);
        Integer totalOrderCount = orderMapper.countByMap(map);

        // 订单完成率
        Double orderCompletionRate = (totalOrderCount == 0 ? 0.0 : (validOrderCount.doubleValue() / totalOrderCount));

        // 平均客单价
        Double unitPrice = (validOrderCount == 0 ? 0.0 : (turnover / validOrderCount));

        // 新增用户数
        Integer newUsers = userMapper.countByMap(map);
        // 封装返回数据
        BusinessDataVO businessDataVO = new BusinessDataVO();
        businessDataVO.setTurnover(turnover);
        businessDataVO.setValidOrderCount(validOrderCount);
        businessDataVO.setOrderCompletionRate(orderCompletionRate);
        businessDataVO.setUnitPrice(unitPrice);
        businessDataVO.setNewUsers(newUsers);
        return businessDataVO;
    }

    @Override
    public OrderOverViewVO overviewOrders(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        // 计算总订单
        Integer allOrders = orderMapper.countByMap(map);
        // 计算已取消的订单
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);
        // 计算已完成的订单
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);
        // 计算待派送的订单
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);
        // 计算待接单的订单
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(map);
        // 封装数据
        OrderOverViewVO orderOverViewVO = new OrderOverViewVO();
        orderOverViewVO.setAllOrders(allOrders);
        orderOverViewVO.setCompletedOrders(completedOrders);
        orderOverViewVO.setCancelledOrders(cancelledOrders);
        orderOverViewVO.setDeliveredOrders(deliveredOrders);
        orderOverViewVO.setWaitingOrders(waitingOrders);
        return orderOverViewVO;
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {
        // 起售状态菜品数量
        Integer sold = dishMapper.countByStatus(StatusConstant.ENABLE);
        // 停售状态菜品数量
        Integer discontinued = dishMapper.countByStatus(StatusConstant.DISABLE);
        DishOverViewVO dishOverViewVO = new DishOverViewVO();
        dishOverViewVO.setDiscontinued(discontinued);
        dishOverViewVO.setSold(sold);
        return dishOverViewVO;
    }
    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        // 起售状态套餐数量
        Integer sold = setmealMapper.countByStatus(StatusConstant.ENABLE);
        // 停售状态套餐数量
        Integer discontinued = setmealMapper.countByStatus(StatusConstant.DISABLE);
        SetmealOverViewVO setmealOverViewVO = new SetmealOverViewVO();
        setmealOverViewVO.setDiscontinued(discontinued);
        setmealOverViewVO.setSold(sold);
        return setmealOverViewVO;
    }
}
