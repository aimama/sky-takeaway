package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车接口
     *
     * @return
     */
    List<ShoppingCart> getShoppingCart();

    /**
     * 清空购物车
     * @return
     */
    void clean();
    /**
     *  删除购物车中一个商品
     * @param shoppingCartDTO
     * @return
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
