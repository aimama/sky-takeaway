package com.sky.controller.admin;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/admin/order")
@Api(tags = "管理端订单相关接口")
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 分页条件查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @ApiOperation("分页条件查询")
    @GetMapping("/conditionSearch")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("分页查询接口，其参数为：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量状态
     *
     * @return
     */
    @ApiOperation("各个状态的订单数量")
    @GetMapping("/statistics")
    private Result<OrderStatisticsVO> getStatusNumbers() {
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.getStatusNumbers();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    private Result<OrderVO> getDetail(@PathVariable Long id) {
        //此id为订单id
        log.info("查询订单详情：其参数为{}", id);
        OrderVO orderVO = orderService.getDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     *
     * @param ordersDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersDTO ordersDTO) {
        log.info("接单，其参数为{}", ordersDTO);
        orderService.confirm(ordersDTO);
        return Result.success();
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单，其参数为：{}", ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @param ordersDTO
     * @return
     */
    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancelToAdmin(@RequestBody OrdersDTO ordersDTO) {
        log.info("取消订单，其参数为{}", ordersDTO);
        orderService.cancelToAdmin(ordersDTO);
        return Result.success();
    }

    /**
     * 派送订单
     *
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id) {
        log.info("派送订单，其参数为{}", id);
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id) {
        log.info("完成订单，其参数为：{}", id);
        orderService.complete(id);
        return Result.success();
    }
}
