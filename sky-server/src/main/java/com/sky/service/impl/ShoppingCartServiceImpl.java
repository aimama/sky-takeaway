package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        //获取用户id（利用微信的openid获取user表中的id）
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //判断当前选中信息是否已在购物车中
        List<ShoppingCart> list = shoppingCartMapper.getList(shoppingCart);
        //判断当前是菜品添加购物车，还是套餐添加购物车（两者互斥）
        Long dishId = shoppingCart.getDishId();

        if (list != null && list.size() == 1) {          //当前购物车内容已经在表中存在，则该购物车的数量自动+1，不会再新增该购物车内容
            ShoppingCart shoppingCart1 = list.get(0);   //获取当前唯一的购物车实体类的所有参数
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);

            //更新表中该购物车信息
            shoppingCartMapper.updateNumber(shoppingCart1);
        } else if (dishId != null) {//购物车表中不含该购物车信息，则添加至表中.1.判断当前是套餐还是菜品
            //当前为菜品信息的购物
            //1.根据dishId获取对应的菜品信息
            DishVO dishVO = dishMapper.getById(dishId);

            //2.将对应的信息存至shopping_cart表中(不适用BeanUtils，因为dishVO的id会覆盖shoppingCart的id)
            shoppingCart.setImage(dishVO.getImage());
            shoppingCart.setName(dishVO.getName());
            shoppingCart.setAmount(dishVO.getPrice());

        } else {
            //dishId为空，则为说明传递的setmealId，则说明当前购物车为套餐
            //1.查询出套餐信息
            Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            //2.存储套餐信息
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());

        }

        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(shoppingCart);

    }

    /**
     * 查看购物车接口
     *
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCart() {
        //用户只能查看自己的购物车，不能查看他人购物车。获取用户id作为查询条条件
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCart = shoppingCartMapper.getShoppingCart(userId);
        return shoppingCart;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();

        shoppingCartMapper.deleteAll(userId);
    }

    /**
     * 删除购物车中一个商品
     *
     * @param shoppingCartDTO
     * @return
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = shoppingCartMapper.getOne(shoppingCartDTO);

        //判断删除的是菜品还是套餐
        Long dishId = shoppingCartDTO.getDishId();

        if (dishId != null) {
            //删除的是菜品信息(表中数量减一即可)
            Integer number = shoppingCart.getNumber();
            shoppingCart.setNumber(number - 1);

            shoppingCartMapper.updateNumber(shoppingCart);
        } else {
            Integer number = shoppingCart.getNumber();
            shoppingCart.setNumber(number - 1);

            shoppingCartMapper.updateNumber(shoppingCart);
        }
    }
}
