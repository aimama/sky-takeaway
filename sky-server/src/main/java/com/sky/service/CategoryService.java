package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    /**
     * 菜品的分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void insert(CategoryDTO categoryDTO);

    /**
     * 根据Id删除信息
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据类型查询分类信息
     * @param type
     * @return
     */
    List<Category> getByType(Integer type);

    /**
     * 启用禁用状态
     * @param status
     */
    void startOrStop(Long id,Integer status);

    /**
     * 回显
     * @param id
     * @return
     */
    Category getById(Long id);

    /**
     * 修改分类信息
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);
}
