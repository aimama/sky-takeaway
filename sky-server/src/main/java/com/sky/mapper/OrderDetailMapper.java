package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    //插入信息
    @Insert("insert into order_detail(name, image, order_id, dish_id, setmeal_id, dish_flavor, amount) " +
            "VALUE (#{name},#{image},#{orderId},#{dishId},#{setmealId},#{dishFlavor},#{amount})")
    void insert(OrderDetail orderDetail);
    //插入多条数据
    void insertBatch(List<OrderDetail> orderDetailList);
}
