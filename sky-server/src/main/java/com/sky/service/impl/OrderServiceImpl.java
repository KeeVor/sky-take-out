package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //1.异常处理
        //地址异常处理
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车异常
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(currentId)
                .build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if (shoppingCarts == null || shoppingCarts.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //2.向订单表添加一条数据
        //创建订单对象
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));//订单号
        orders.setStatus(Orders.PENDING_PAYMENT);//订单状态
        orders.setUserId(currentId);//用户id
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setPayStatus(Orders.UN_PAID);//支付状态
        orders.setPhone(addressBook.getPhone());//手机号
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());
        orderMapper.insert(orders);
        //3.向订单明细表添加n条数据
        for (ShoppingCart cart : shoppingCarts) {
            //创建订单详细表
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailMapper.insert(orderDetail);
        }
        //4.清空用户购物车
        shoppingCartMapper.delete(ShoppingCart.builder().userId(currentId).build());
        //5.封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    /**
     * 用户支付
     * @param ordersPaymentDTO
     */
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        //获取订单id
        Long currentId = BaseContext.getCurrentId();
        Long id = orderMapper.queryByNumberAndUserId(ordersPaymentDTO.getOrderNumber(), currentId).getId();
        //修改订单状态
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.TO_BE_CONFIRMED)
                .checkoutTime(LocalDateTime.now())
                .payMethod(ordersPaymentDTO.getPayMethod())
                .payStatus(Orders.PAID)
                .build();
        orderMapper.update(orders);
        //向商家推送消息
        Map map = new HashMap();
        map.put("type",1);// 1表示来单提醒 2表示客户催单
        map.put("orderId",id);
        map.put("content","订单号:"+ordersPaymentDTO.getOrderNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    public OrderVO orderDetail(Long id) {
        //获取订单信息
        Orders orders = orderMapper.queryById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.queryByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        //分页插件
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        //获取订单列表
        Page<OrderVO> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> ordersList = page.getResult();
        for (OrderVO ordersVO : ordersList) {
            StringBuilder sb = new StringBuilder();
            //获取订单详细信息
            List<OrderDetail> orderDetails = orderDetailMapper.queryByOrderId(ordersVO.getId());
            for (OrderDetail orderDetail : orderDetails) {
                //拼接菜品名称
                sb.append(orderDetail.getName());
                sb.append(",");
            }
            //封装订单详细信息
            ordersVO.setOrderDetailList(orderDetails);
            ordersVO.setOrderDishes(String.valueOf(sb));

        }
        //封装返回信息
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(ordersList);
        return pageResult;
    }

    /**
     * 取消订单
     * @param id
     */
    public void cancel(Long id) {
        //获取当前订单
        Orders orders = orderMapper.queryById(id);
        //修改订单信息
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);

    }

    /**
     * 再来一单
     * @param id
     */
    public void repetition(Long id) {
        //根据订单id找到订单详情
        List<OrderDetail> orderDetails = orderDetailMapper.queryByOrderId(id);
        //把订单菜品添加到购物车
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 用户催单
     * @param id
     */
    public void reminder(Long id) {
        Orders orders = orderMapper.queryById(id);
        if (orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map = new HashMap();
        map.put("type",2);// 1表示来单提醒 2表示客户催单
        map.put("orderId",id);
        map.put("content","订单号:"+orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 接单
     * @param id
     */
    public void confirm(Long id) {
        //查询订单
        Orders orders = orderMapper.queryById(id);
        //修改订单信息
        orders.setStatus(Orders.CONFIRMED);
        //提交订单
        orderMapper.update(orders);
    }

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //查询订单信息
        Orders orders = orderMapper.queryById(ordersRejectionDTO.getId());
        //修改订单信息
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setStatus(Orders.CANCELLED);
        orders.setEstimatedDeliveryTime(null);
        orderMapper.update(orders);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = orderMapper.queryById(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setEstimatedDeliveryTime(null);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orderMapper.update(orders);
    }

    /**
     * 派送订单
     * @param id
     */
    public void delivery(Long id) {
        Orders orders = orderMapper.queryById(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    public void complete(Long id) {
        Orders orders = orderMapper.queryById(id);
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    /**
     * 各个状态的订单数量统计
     * @return
     */
    public OrderStatisticsVO statistics() {
        //构造返回对象
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        //查询订单数据
        int tbc = orderMapper.queryByStatus(Orders.TO_BE_CONFIRMED).size();//待接单
        int confirmed = orderMapper.queryByStatus(Orders.CONFIRMED).size();//待派送
        int dip = orderMapper.queryByStatus(Orders.DELIVERY_IN_PROGRESS).size();//派送中
        //封装数据
        orderStatisticsVO.setToBeConfirmed(tbc);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(dip);
        return orderStatisticsVO;
    }
}


