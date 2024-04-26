package com.food.mapper;

import com.github.pagehelper.Page;
import com.food.dto.OrdersPageQueryDTO;
import com.food.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 添加订单
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 根据id查询订单
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 条件分页查询
     * @param pageQueryDTO
     * @return
     */
    Page<Orders> orderPageQuery(OrdersPageQueryDTO pageQueryDTO);

    /**
     * 根据状态查询订单数量
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer getcountByStatus(Integer status);

    /**
     * 在定时任务中根据状态和下单时间查询所有订单
     * @param status
     * @param time
     * @return
     */
    @Select("select * from  orders where status = #{status} and order_time < #{time}")
    List<Orders> getOrderByStatusAndOrderTime(Integer status, LocalDateTime time);

    /**
     * 统计当天的营业额
     * @param map
     * @return
     */
    Double sumTurnoverByMap(Map map);

    /**
     * 根据map查询订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
