package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("userShopController")
@Api(tags = "用户店铺管理相关接口")
@RequestMapping("/user/shop")
public class ShopController {
    
    @Autowired
    private ShopService shopService;

    @GetMapping("/status")
    @ApiOperation("用户查询营业状态接口")
    public Result<Integer> getStatus() {
        Integer status = shopService.getStatus();
        log.info("用户正在查询店铺状态，其状态为:{}", status == 1 ? "营业中" : "打样中");
        return Result.success(status);
    }
}
