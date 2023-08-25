package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    //新增菜品
    void save(DishDTO dishDTO);

    //分页查询
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    //修改菜品
    void update(DishDTO dishDTO);

    //回显
    DishVO getById(Long id);

    //起售停售
    void startAndStop(Integer status,Long id);
    //批量删除菜品信息
    void deleteByIds(List<Long> ids);
    /**
     * 根据分类要求获取对应菜品信息
     *
     * @return
     */
    List<Dish> getByCategoryId(String name,Long categoryId);
//    /**
//     * 根据名称获取对应信息（用于套餐管理的快速查询）
//     *
//     * @param name
//     * @return
//     */
//    List<Dish> getByName(String name);
}
