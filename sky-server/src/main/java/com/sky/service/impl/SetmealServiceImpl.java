package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> result = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(result.getTotal(), result.getResult());
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @Override
    @Transactional
    public void insert(SetmealDTO setmealDTO) {
        //先将套餐信息存储到套餐表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmealMapper.insert(setmeal);
        //获取套餐表的主键，用于存储在套餐菜品表中
        Long setmealId = setmeal.getId();

        //根据setmealDTO的分类ID连接Dish表进而获取dishId
        Dish dish = new Dish();


        //封装dishId，setmealId
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish -> {
                //获取SetmealDish后进而获取菜品名称
                Long dishId = dishMapper.getByCategoryId_To_setmealDish(setmealDish.getName());
                //根据菜品名称获取菜品id
                setmealDish.setDishId(dishId);
                setmealDish.setSetmealId(setmealId);
            });
            setmealDishMapper.insert(setmealDishes);
        }


    }

    /**
     * 起售停售
     *
     * @return
     */
    @Override
    public void startAndStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();

        setmealMapper.update(setmeal);

    }

    /**
     * 回显
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealVO getBySetmealId(Long id) {
        //涉及两张表的查询，优先查询套餐表
        Setmeal setmeal = setmealMapper.getById(id);

        //再查询套餐菜品关系表
        List<SetmealDish> setmealDish = setmealDishMapper.getBySetmealId(id);

        //最后将两者结果封装在SetmealVO返回
        SetmealVO setmealVO = new SetmealVO();
        setmealVO.setSetmealDishes(setmealDish);
        BeanUtils.copyProperties(setmeal, setmealVO);
        return setmealVO;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        //在售状态不可删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);

            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //套餐内包含未起售的菜品不可删除
        for (Long id : ids) {
            //用setmealId获取套餐菜品关系表中的菜品name
            List<String> name = setmealDishMapper.getByDishName(id);

            for (String dishName : name) {
                //菜品name获取该菜品的status
                Integer status = dishMapper.getByName(dishName);

                if (status == StatusConstant.DISABLE) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        //TODO 为了操作数据库方便，使用迭代器循环迭代id后再删除
        for (Long id : ids) {
            //删除套餐表信息
            setmealMapper.deleteById(id);
            //删除套餐菜品的信息
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    /**
     * 修改套餐信息
     *
     * @param setmealDTO
     * @return
     */
    @Override
    public void updateSetmeal(SetmealDTO setmealDTO) {
        //1.删除原本信息（删除套餐表的信息）
        setmealMapper.deleteById(setmealDTO.getId());
        //删除套餐对应的套餐菜品表的信息
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        //2.添加新修改的信息(添加套餐表信息)
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        //获取套餐id，用于添加至套餐菜品关系表中
        Long setmealId = setmeal.getId();
//        //查找出对应的菜品信息（category_id）
//        List<Dish> dishes = dishMapper.getByCategoryId(setmealDTO.getCategoryId());
        //将DTO转变成实体类，再添加
//        SetmealDish setmealDish = new SetmealDish();
//        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
//        BeanUtils.copyProperties(setmealDishes,setmealDish);
//        setmealDish.setSetmealId(setmealId);
//        setmealDishMapper.insertOne(setmealDish);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealId);
                setmealDishMapper.insertOne(setmealDish);
            }
        }


    }


}
