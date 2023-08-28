package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置营业状态
     *
     * @param status
     * @return
     */
    @Override
    public void setStatus(Integer status) {
        //使用redis操作,存储status字符串
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("shop_status", status);
    }

    /**
     * 查询营业状态
     *
     * @return
     */
    @Override
    public Integer getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("shop_status");
        return status;
    }
}
