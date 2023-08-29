package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    @Autowired
    private ShopService shopService;

    /**
     * 设置营业状态
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置营业状态，其参数为{}", status == 1 ? "营业中" : "打样中");
        shopService.setStatus(status);
        return Result.success();
    }

    /**
     * 查询营业状态
     *
     * @return
     */
    @ApiOperation("查询营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = shopService.getStatus();
        log.info("当前查询营业状态为{}", status == 1 ? "营业中" : "打样中");
        return Result.success(status);
    }


}
