package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocketServer webSocketServer;


    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO setOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        //判断当前地址是否为空，空则异常提示
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);

        if (addressBook == null) {
            //当前用户的地址为空，报错
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //查询当前用户的购物车中是否包含信息
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getShoppingCart(userId);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }


        //将信息存到订单表中(订单表对订单明细表为1：M)
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        orders.setAddress(addressBook.getDetail());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));   //订单号
        orders.setOrderTime(LocalDateTime.now());

        orders.setStatus(Orders.PENDING_PAYMENT);           //订单状态
        orders.setPayStatus(Orders.UN_PAID);                //支付状态
        //插入一条数据
        orderMapper.insert(orders);

        System.out.println("订单表插入成功");
        //将信息存到订单详细表

        //购物车信息不止一个，需循环将购物车信息添加到订单明细表中
        List<OrderDetail> orderDetailList = new ArrayList<>();
        System.out.println("正在插入订单明细表");
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            //将信息放入orderDetailList链表中
            orderDetailList.add(orderDetail);
        }

        //插入多条数据
        orderDetailMapper.insertBatch(orderDetailList);
        //清理购物车数据
        shoppingCartMapper.deleteAll(userId);

        //封装到OrderSubmitVO返回,无需添加到表中
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        log.info("OrderServiceImpl");

        return orderSubmitVO;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        JSONObject jsonObject = new JSONObject();
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        paySuccess(ordersPaymentDTO.getOrderNumber());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        Map map = new HashMap<>();
        map.put("type", 1);  //  1表示来单提醒，2表示客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + outTradeNo);

        String jsonString = JSON.toJSONString(map);

        //通过websocket向客户端浏览器推送消息
        webSocketServer.sendToAllClient(jsonString);
    }

    /**
     * 查看历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult getHistory(OrdersPageQueryDTO ordersPageQueryDTO) {
        //一条订单信息绑定多条订单明细表，先查询订单信息，将此封装到VO中
        Long userId = BaseContext.getCurrentId();
        Orders orders = Orders.builder().userId(userId).status(ordersPageQueryDTO.getStatus()).build();
        List<Orders> ordersList = orderMapper.getByUserId(orders);

        Page<OrderVO> pageResult = new Page<>();

        for (Orders order : ordersList) {
            OrderVO orderVO1 = new OrderVO();
            BeanUtils.copyProperties(order, orderVO1);
            //针对单个订单表，设定对应明细表
            Long orderId = order.getId();
            //获取对应明细表
            List<OrderDetail> orderDetailLists = orderDetailMapper.getByOrderId(orderId);
            orderVO1.setOrderDetailList(orderDetailLists);

            //封装
            pageResult.add(orderVO1);
        }

        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }


    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @Override
    public void OrderAgain(Long id) {
        //根据传过的订单id，查询出其订单详细表中的信息
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        //将订单详细表的信息copy到购物车中，手动设置userId，createTime即可
        for (OrderDetail orderDetail : orderDetailList) {
            //因为购物车Mapper中暂时没写批量插入，故使用单插
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .name(orderDetail.getName())
                    .image(orderDetail.getImage())
                    .userId(BaseContext.getCurrentId())
                    .dishId(orderDetail.getDishId())
                    .setmealId(orderDetail.getSetmealId())
                    .dishFlavor(orderDetail.getDishFlavor())
                    .number(orderDetail.getNumber())
                    .amount(orderDetail.getAmount())
                    .createTime(LocalDateTime.now())
                    .build();
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    @Override
    public void cancel(Long id) {
//        //根据订单id删除订单表
//        Orders order = Orders.builder().id(id).build();
//        orderMapper.deleteById(id);
//        //根据订单id删除对应的所有订单明细表
//        orderDetailMapper.deleteByOrderId(id);
        //取消订单：只要修改其status即可
        Orders order = Orders.builder().id(id).status(Orders.CANCELLED).cancelTime(LocalDateTime.now()).build();
        orderMapper.update(order);

    }

    /**
     * 查看订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        //查询此订单的信息（id）
        Orders orders = Orders.builder().id(id).build();
        Orders order = orderMapper.get(orders).get(0);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        //查询此订单包含的订单详细信息（orderId）
        List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(order.getId());
        //封装成OrdersVO
        orderVO.setOrderDetailList(byOrderId);
        return orderVO;
    }

    /**
     * 催单
     *
     * @param id
     * @return
     */
    @Override
    public void reminder(Long id) {
        // 根据订单id查询订单
        Orders orders = Orders.builder().id(id).build();

        Orders ordersDB = orderMapper.get(orders).get(0);
        //查询该订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map map = new HashMap<>();
        map.put("type", 2);  //  1表示来单提醒，2表示客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + ordersDB.getNumber());

        String jsonString = JSON.toJSONString(map);

        webSocketServer.sendToAllClient(jsonString);
    }

    //TODO 用户端与管理端分界线
///////////////////////////////////////////////////////////////////////////////
    //管理端

    /**
     * 分页条件查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        //查询对应的订单信息(除开orderDish未封装)
        List<OrderVO> orderVOS = orderMapper.getPageQuery(ordersPageQueryDTO);
        //封装orderDish（利用orderId获取订单明细表的name）
        Page<OrderVO> page = new Page<>();

        for (OrderVO orderVO : orderVOS) {
            Long orderId = orderVO.getId();
            //查找一个订单表所对应的多个订单详情表
            List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(orderId);
            //循环设定orderDish
            for (OrderDetail oD : byOrderId) {
                orderVO.setOrderDishes(oD.getName());
            }
            //封装page
            page.add(orderVO);
        }
        //返回结果
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 各个状态的订单数量状态
     *
     * @return
     */
    @Override
    public OrderStatisticsVO getStatusNumbers() {
        //查询各个状态的订单数量
        Orders ordersStatus = Orders.builder().status(Orders.TO_BE_CONFIRMED).build();  //待接单
        List<Orders> ordersList = orderMapper.get(ordersStatus);
        int TO_BE_CONFIRMED_number = ordersList.size();

        ordersStatus.setStatus(Orders.CONFIRMED);       //待派送
        List<Orders> ordersList1 = orderMapper.get(ordersStatus);
        int confirmedNumber = ordersList1.size();

        ordersStatus.setStatus(Orders.DELIVERY_IN_PROGRESS);        //派送中
        List<Orders> ordersList2 = orderMapper.get(ordersStatus);
        int delivery_in_progress_number = ordersList2.size();
        //封装到OrderStatisticsVO
        OrderStatisticsVO orderStatisticsVO = OrderStatisticsVO.builder()
                .confirmed(confirmedNumber)
                .toBeConfirmed(TO_BE_CONFIRMED_number)
                .deliveryInProgress(delivery_in_progress_number)
                .build();

        return orderStatisticsVO;


    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getDetail(Long id) {
        //利用订单id查询对应的订单信息(一条记录)
        Orders orders = Orders.builder().id(id).build();
        List<Orders> ordersList = orderMapper.get(orders);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(ordersList.get(0), orderVO);
        //再利用订单id查询其对应的订单明细表的信息
        List<OrderDetail> byOrderId = orderDetailMapper.getByOrderId(ordersList.get(0).getId());
        //封装返回
        orderVO.setOrderDetailList(byOrderId);
        return orderVO;
    }

    /**
     * 接单
     *
     * @return
     */
    @Override
    public void confirm(OrdersDTO ordersDTO) {
        //修改其订单状态即可，改为CONFIRMED即可
        Orders orders = Orders.builder()
                .status(Orders.CONFIRMED)
                .id(ordersDTO.getId())
                .build();
        orderMapper.update(orders);

    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //将该订单的拒绝原因存储即可,并将订单状态改为--已取消
        Orders orders = new Orders();
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setId(ordersRejectionDTO.getId());

        orderMapper.update(orders);
    }

    /**
     * 取消订单
     *
     * @param ordersDTO
     * @return
     */
    @Override
    public void cancelToAdmin(OrdersDTO ordersDTO) {
        //修改订单状态，并存储取消原因以及取消时间
        Orders orders = new Orders();
        orders.setId(ordersDTO.getId());
        orders.setCancelReason(ordersDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setStatus(Orders.CANCELLED);

        orderMapper.update(orders);
    }

    /**
     * 派送订单
     *
     * @param id
     * @return
     */
    @Override
    public void delivery(Long id) {
        //修改订单状态，改为派送即可
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
//                .deliveryStatus()
                .build();
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     *
     * @return
     */
    @Override
    public void complete(Long id) {
        //修改其订单状态即可,修改送达时间
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

}
