package com.sky.mapper;

import com.sky.dto.SetmealDTO;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //Id查询套餐所含菜品的数量
    @Select("select count(*) from setmeal where id = #{id}")
    Integer getCounts(Long id);

    //新增套餐与菜品关系
    void insert(List<SetmealDish> setmealDishes);

    /**
     * 回显
     *
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id =#{id}")
    List<SetmealDish> getBySetmealId(Long id);
    //由套餐id获取菜品名字
    @Select("select name from setmeal_dish where setmeal_id = #{id}")
    List<String> getByDishName(Long id);

    /**
     * 根据套餐Id删除对应的套餐菜品信息
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 只添加一个套餐菜品信息
     * @param setmealDish
     */
    @Insert("insert into setmeal_dish(setmeal_id, dish_id, name, price, copies)" +
            "value (#{setmealId},#{dishId},#{name},#{price},#{copies})")
    void insertOne(SetmealDish setmealDish);
}
