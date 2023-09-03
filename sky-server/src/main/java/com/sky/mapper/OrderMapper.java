package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    //向订单表插入数据一条
    void insert(Orders orders);
    //条件查询
    List<Orders> get(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据userId查询订单以及订单状态
     * @return
     */
    List<Orders> getByUserId(Orders orders);

    /**
     * 删除订单
     * @param id
     */
    @Delete("delete from orders where id = #{id}")
    void deleteById(Long id);
    /**
     * 分页条件查询
     *
     * @return
     */
    List<OrderVO> getPageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询支付超时的订单
     * @return
     */
    @Select("select * from orders where pay_status = #{payStatus} and order_time < #{orderTime}")
    List<Orders> getTimeOutOrder(Orders orders);

    /**
     * 查询当天所有处于派送未完成订单
     * @param orders
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getProcessDeliveryOrder(Orders orders);

    /**
     * 统计营业额
     * @param map
     * @return
     */
    Double sumByTurnover(Map map);
}
