package com.food.service;

import com.food.vo.BusinessDataVO;
import com.food.vo.DishOverViewVO;
import com.food.vo.OrderOverViewVO;
import com.food.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {
    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO overviewOrders(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 查询菜品总览
     * @return
     */
    DishOverViewVO overviewDishes();
    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO overviewSetmeals();
}
