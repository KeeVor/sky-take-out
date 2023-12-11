package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderReportVO;
import com.sky.vo.OrderVO;
import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 新增订单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 修改订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     * @param number
     * @param userId
     * @return
     */
    @Select("select * from orders where number = #{number} and user_id = #{userId}")
    Orders queryByNumberAndUserId(String number, Long userId);

    /**
     * 根据id查
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders queryById(Long id);

    /**
     * 分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<OrderVO> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 根据状态查订单
     * @param status
     * @return
     */
    @Select("select * from orders where status = #{status}")
    List<Orders> queryByStatus(Integer status);

    /**
     * 根据订单日期查询已完成订单的销售额总和
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TurnoverReportVO> queryAmountByOrderTime(LocalDate beginDate, LocalDate endDate);

    /**
     * 根据订单日期查询有效订单总数和订单总数
     * @param begin
     * @param end
     * @return
     */
    List<OrderReportVO> queryTotalOrdersByCreate(LocalDate begin, LocalDate end);

    /**
     * 根据订单时间查询消费用户总人数
     * @param now
     * @return
     */
    Integer queryTotalUserByOrderTime(LocalDate now);

    /**
     * 根据订单时间和状态查订单数量
     * @param status
     * @return
     */
    Integer queryOrderTotalByStatusAndOrderTime(Integer status, LocalDate order_time);
}
