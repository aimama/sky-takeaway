package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO setOrder(OrdersSubmitDTO ordersSubmitDTO);
    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查看历史订单
     *
     * @return
     */
    PageResult getHistory(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 再来一单
     * @param id
     * @return
     */
    void OrderAgain(Long id);

    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    void cancel(Long id);

    /**
     * 查看订单详情
     * @param id
     * @return
     */
    OrderVO getOrderDetail(Long id);

    /**
     * 分页条件查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量状态
     * @return
     */
    OrderStatisticsVO getStatusNumbers();

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    OrderVO getDetail(Long id);

    /**
     * 接单
     *
     * @return
     */
    void confirm(OrdersDTO ordersDTO);
    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     *
     * @param ordersDTO
     * @return
     */
    void cancelToAdmin(OrdersDTO ordersDTO);

    /**
     * 派送订单
     * @param id
     * @return
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @return
     */
    void complete(Long id);

    /**
     * 催单
     *
     * @param id
     * @return
     */
    void reminder(Long id);
}
