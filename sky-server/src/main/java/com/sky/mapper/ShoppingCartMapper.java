package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    //动态查询当前user_id是否已经存在
    List<ShoppingCart> getList(ShoppingCart shoppingCart);
    //只更新该购物车的数量(依据name与dish_flavor)
    void updateNumber(ShoppingCart shoppingCart1);
    //存储购物车的信息
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) " +
            "VALUE(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime}) ")
    void insert(ShoppingCart shoppingCart);

    /**
     * 查看购物车接口
     *
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> getShoppingCart(Long userId);

    /**
     * 清空购物车
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteAll(Long userId);

    /**
     * 获取单条查询信息（动态查询）
     * @param shoppingCartDTO
     * @return
     */
    ShoppingCart getOne(ShoppingCartDTO shoppingCartDTO);
}
