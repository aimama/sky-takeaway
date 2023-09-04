package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper {

    //插入信息
    @Insert("insert into order_detail(name, image, order_id, dish_id, setmeal_id, dish_flavor, amount) " +
            "VALUE (#{name},#{image},#{orderId},#{dishId},#{setmealId},#{dishFlavor},#{amount})")
    void insert(OrderDetail orderDetail);
    //插入多条数据
    void insertBatch(List<OrderDetail> orderDetailList);
    //查询
    List<OrderVO> getAndGetOrders(Long userId);
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> getByOrderId(Long id);

    /**
     * 根据orderId删除信息
     * @param id
     */
    @Delete("delete from order_detail where order_id = #{id}")
    void deleteByOrderId(Long id);

    /**
     * 按照数量降序查找前10
     * @param map
     * @return
     */
    List<OrderDetail> getTop10(Map map);
}
