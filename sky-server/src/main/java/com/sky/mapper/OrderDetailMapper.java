package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {
    /**
     * 新增订单详细数据
     * @param orderDetail
     */
    void insert(OrderDetail orderDetail);
}
