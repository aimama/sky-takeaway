package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    void insert(SetmealDTO setmealDTO);

    /**
     * 起售停售
     *
     * @return
     */
    void startAndStop(Integer status, Long id);

    /**
     * 回显
     *
     * @param id
     * @return
     */
    SetmealVO getBySetmealId(Long id);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    void deleteByIds(List<Long> ids);

    /**
     * 修改套餐信息
     * @param setmealDTO
     * @return
     */
    void updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 条件查询
     *
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
