package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 菜品分类查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {

        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //使用数据库得出来得数据
        Page<Category> results = categoryMapper.pageQuery(categoryPageQueryDTO);

        long total = results.getTotal();
        List<Category> results2 = results.getResult();

        return new PageResult(total, results2);


    }

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    @Override
    public void insert(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
        category.setStatus(0);

//        category.setCreateUser(Thread.currentThread().getId());
//        category.setUpdateUser(Thread.currentThread().getId());

        categoryMapper.insert(category);
    }

    /**
     * 根据Id删除分类信息
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        //先判断分类表下是否有菜品信息，没有才可删除，否者不可删除
        Integer countByCategoryId = dishMapper.countByCategoryId(id);
        if (countByCategoryId > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //先判断分类表下是否有套餐信息，没有才可删除，否者不可删除
        Integer countByCategoryId1 = setmealMapper.countByCategoryId(id);
        if (countByCategoryId1 > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }

    /**
     * 根据类型查询分类信息
     *
     * @param type
     * @return
     */
    @Override
    public List<Category> getByType(Integer type) {
        List<Category> categoryList = categoryMapper.getByType(type);
        return categoryList;
    }

    /**
     * 启用禁用状态
     *
     * @param status
     */
    @Override
    public void startOrStop(Long id, Integer status) {
        Category category = Category.builder()
                .status(status)
                .id(id)
//                .updateTime(LocalDateTime.now())
//                .updateUser(Thread.currentThread().getId())
                .build();
        categoryMapper.update(category);
    }

    /**
     * 回显
     *
     * @param id
     * @return
     */
    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.getById(id);
        return category;
    }

    /**
     * 修改分类信息
     *
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = Category.builder()
//                .updateUser(Thread.currentThread().getId())
//                .updateTime(LocalDateTime.now())
                .build();
        BeanUtils.copyProperties(categoryDTO,category);

        categoryMapper.update(category);
    }


}
