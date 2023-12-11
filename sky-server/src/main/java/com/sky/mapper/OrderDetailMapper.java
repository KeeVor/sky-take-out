package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import com.sky.vo.SalesTop10ReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
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

    /**
     * 查询销量top10
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> queryTop10(LocalDate begin, LocalDate end);

    /**
     * 查询某日销售总额
     * @param now
     * @return
     */
    Double queryTotalAmount(LocalDate now);
}
