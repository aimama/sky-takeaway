package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        //向Dish表中保存数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.DISABLE);

        dishMapper.insert(dish);

        //获取insert语句生成的主键ID，用于与DishFlavor表的逻辑连接
        //Mapper对应xml处必须有useGeneratedKeys = 'true' keyProperty='id',否则不能返回主键
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
        }
        //向DishFlavor表中存数据
        dishFlavorMapper.insert(flavors);

    }

    /**
     * 菜品的分类查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<Dish> pageResult = dishMapper.pageQuery(dishPageQueryDTO);

        long total = pageResult.getTotal();
        List<Dish> result = pageResult.getResult();

        return new PageResult(total, result);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        //涉及三张表的修改，菜品表（category），口味表（dishFlavor），菜品分类表（category）
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.update(dish);

        //TODO 直接删除口味，不修改口味，直接添加口味即可
        //删除原有口味，增加新修改的口味
        dishFlavorMapper.delete(dishDTO);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            //循环设置要修改口味表的id
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //执行修改操作
            dishFlavorMapper.insert(flavors);
        }


    }

    /**
     * 回显
     *
     * @param id
     * @return
     */
    @Override
    public DishDTO getById(Long id) {
        DishDTO dishDto = dishMapper.getById(id);

        return dishDto;
    }
}
