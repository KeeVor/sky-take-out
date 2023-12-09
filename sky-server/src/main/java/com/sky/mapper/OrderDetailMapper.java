package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 新增订单详细数据
     * @param orderDetail
     */
    void insert(OrderDetail orderDetail);

    /**
     * 根据订单id查
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> queryByOrderId(Long orderId);
}
