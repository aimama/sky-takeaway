package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/shoppingCart")
@Api(tags = "C 端-购物车接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车操作，其参数为：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);

        return Result.success();
    }

    /**
     * 查看购物车接口
     *
     * @return
     */
    @ApiOperation("查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> getShoppingCart() {
        log.info("请求查看购物车信息");
        List<ShoppingCart> shoppingCart = shoppingCartService.getShoppingCart();
        return Result.success(shoppingCart);
    }

    /**
     * 清空购物车
     * @return
     */
    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result clean(){
        log.info("清空购物车");
        shoppingCartService.clean();
        return Result.success();
    }

    /**
     *  删除购物车中一个商品
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("删除购物车的一个商品")
    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中一个商品信息，其参数为{}",shoppingCartDTO);
        shoppingCartService.sub(shoppingCartDTO);

        return Result.success();
    }
}
