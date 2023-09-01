package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    //向订单表插入数据一条
    void insert(Orders orders);
    //条件查询
    List<Orders> get(Orders orders);
}
