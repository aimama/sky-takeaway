package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {
    //新增菜品
    void save(DishDTO dishDTO);

    //分页查询
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //修改菜品
    void update(DishDTO dishDTO);

    //回显
    DishDTO getById(Long id);

    //起售停售
    void startAndStop(Integer status,Long id);
    //批量删除菜品信息
    void deleteByIds(List<Long> ids);
}
