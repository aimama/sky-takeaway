package com.sky.controller.user;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "C端-订单接口")
@RequestMapping("/user/order")
@RestController("userOrderController")
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 提交订单
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
}
