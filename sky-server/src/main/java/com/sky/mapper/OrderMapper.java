package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
}
