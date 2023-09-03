package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "C端-订单接口")
@RequestMapping("/user/order")
@RestController("userOrderController")
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation("订单提交接口")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> setOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("执行订单提交接口，其参数为：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.setOrder(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        //模拟交易完成，修改数据库订单状态
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        log.info("模拟交易成功：{}", ordersPaymentDTO.getOrderNumber());
        return Result.success(orderPaymentVO);
    }

    /**
     * 查看历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @ApiOperation("查看历史订单接口")
    @GetMapping("/historyOrders")
    public Result<PageResult> getHistory(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("查看历史订单，其参数为：{}", ordersPageQueryDTO);
        PageResult page = orderService.getHistory(ordersPageQueryDTO);

        return Result.success(page);
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @ApiOperation("再来一单")
    @PostMapping("/repetition/{id}")
    public Result OrderAgain(@PathVariable Long id) {
        log.info("再来一单，其参数为：{}", id);
        orderService.OrderAgain(id);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id) {
        //传输的是订单id
        log.info("取消订单，其订单参数为：{}", id);
        orderService.cancel(id);
        return Result.success();
    }

    /**
     * 查看订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        log.info("查看订单详情，其订单参数为：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 催单
     *
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单提醒")
    public Result reminder(@PathVariable Long id) {
        log.info("接单提醒，其参数为：{}",id);
        orderService.reminder(id);

        return Result.success();
    }
}
