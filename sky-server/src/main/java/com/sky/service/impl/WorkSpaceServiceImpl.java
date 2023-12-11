package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class WorkSpaceServiceImpl implements WorkspaceService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    public BusinessDataVO getBusinessData() {
        //查询新增用户数
        Integer newUsers = userMapper.queryTotalUserByCreateTime(LocalDate.now());
        //查询今日营业额
        Double turnover = orderDetailMapper.queryTotalAmount(LocalDate.now());
        //查询有效订单数
        Integer validOrderCount = null;
        Double orderCompletionRate = null;
        List<OrderReportVO> orderReportVOS = orderMapper.queryTotalOrdersByCreate(LocalDate.now(), LocalDate.now().plusDays(1));
        if (orderReportVOS != null && !orderReportVOS.isEmpty()){
            OrderReportVO orderReportVO = orderReportVOS.get(0);
            //有效订单数
            validOrderCount = Integer.valueOf(orderReportVO.getValidOrderCountList());
            //订单完成率
            orderCompletionRate = validOrderCount == 0 ? 0 : validOrderCount / Double.parseDouble(orderReportVO.getOrderCountList());
        }
        //查询当日消费顾客数量
        Integer totalUser = orderMapper.queryTotalUserByOrderTime(LocalDate.now());
        Double unitPrice = turnover == 0 ? 0 : turnover / totalUser;
        //返回数据
        return BusinessDataVO.builder()
                .newUsers(newUsers == null ? 0 : newUsers)
                .turnover(turnover)
                .validOrderCount(validOrderCount == null ? 0 : validOrderCount)
                .orderCompletionRate(orderCompletionRate == null ? 0 : orderCompletionRate)
                .unitPrice(unitPrice).build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    public OrderOverViewVO getOverviewOrders() {
        //查询全部订单数量
        Integer allOrders = orderMapper.queryOrderTotalByStatusAndOrderTime(null, LocalDate.now());
        //查询已取消订单数量
        Integer cancelledOrders = orderMapper.queryOrderTotalByStatusAndOrderTime(Orders.CANCELLED, LocalDate.now());
        //查询已完成订单数量
        Integer completedOrders = orderMapper.queryOrderTotalByStatusAndOrderTime(Orders.COMPLETED, LocalDate.now());
        //查询待派送订单数量
        Integer deliveredOrders = orderMapper.queryOrderTotalByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDate.now());
        //查询待接单订单数量
        Integer waitingOrders = orderMapper.queryOrderTotalByStatusAndOrderTime(Orders.TO_BE_CONFIRMED, LocalDate.now());
        //返回数据
        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders).build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    public DishOverViewVO getOverviewDishes() {
        //已停售菜品数量
        Integer discontinued =  dishMapper.queryTotalByStatus(0);
        //已起售菜品数量
        Integer sold = dishMapper.queryTotalByStatus(1);
        return DishOverViewVO.builder().discontinued(discontinued).sold(sold).build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    public SetmealOverViewVO getOverviewSetmeals() {
        //已停售套餐数量
        Integer discontinued =  setmealMapper.queryTotalByStatus(0);
        //已起售套餐数量
        Integer sold = dishMapper.queryTotalByStatus(1);
        return SetmealOverViewVO.builder().discontinued(discontinued).sold(sold).build();
    }
}
