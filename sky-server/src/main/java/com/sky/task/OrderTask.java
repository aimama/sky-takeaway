package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 自定义订单状态
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单超时，自动取消订单
     */
    @Scheduled(cron = "0 * * * * ? ")      //设置每隔1分钟查询一次
    public void processTimeOutOrder() {
        log.info("处理订单超时：{}", new Date());
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //当前时间-15分钟获取超过时间限制的订单
        LocalDateTime timeOutOrder = now.plusMinutes(-15);
        //查询所有支付未付款且超过时间限制的订单（pay_status以及timeOutOrder）
        //TODO 核心思想，查询设定超时条件，再查询
        Orders orders = Orders.builder().payStatus(Orders.UN_PAID).orderTime(timeOutOrder).build();
        List<Orders> timeOutOrderList = orderMapper.getTimeOutOrder(orders);

        if (timeOutOrderList.size() > 0 && timeOutOrderList != null) {
            //修改其订单状态，并自动取消，新增取消原因
            for (Orders order : timeOutOrderList) {
                Orders orders1 = Orders.builder()
                        .id(order.getId())
                        .cancelTime(LocalDateTime.now())
                        .cancelReason("支付超时，自动取消")
                        .status(Orders.CANCELLED)
                        .build();
                orderMapper.update(orders1);
            }
        }
    }

    /**
     * 自动完成派送
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder() {
        log.info("自动完成派送订单：{}", new Date());
        //设定自动完成时间(限制统一处理上一天的派送)
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        //查询所有处于派送状态的订单
        Orders orders = Orders.builder().status(Orders.DELIVERY_IN_PROGRESS).orderTime(time).build();

        List<Orders> ordersList = orderMapper.getProcessDeliveryOrder(orders);
        //改变订单状态为完成即可
        if (ordersList.size() > 0 && ordersList != null) {
            for (Orders od : ordersList) {
                od.setStatus(Orders.COMPLETED);
                orderMapper.update(od);
            }
        }
    }

}
