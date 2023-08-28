package com.sky.service;

public interface ShopService {
    /**
     * 设置营业状态
     * @param status
     * @return
     */
    void setStatus(Integer status);

    /**
     * 查询营业状态
     * @return
     */
    Integer getStatus();
}
