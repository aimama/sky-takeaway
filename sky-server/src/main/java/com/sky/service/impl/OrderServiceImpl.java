package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
//    @Transactional
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
        if (shoppingCarts == null || shoppingCarts.size() != 1) {
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
}
