package com.food.mapper;

import com.food.dto.GoodsSalesDTO;
import com.food.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单详情信息
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单号查询详情
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getOrderId(Long orderId);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
