package com.sky.mapper;

import com.sky.dto.DishDTO;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    //动态新增口味数组
    void insert(List<DishFlavor> flavors);

    //    //修改口味表
//    @AutoFill(OperationType.UPDATE)
//    void update(DishDTO dishDTO);
    //删除口味
    @Delete("delete from dish_flavor where id = #{id}")
    void delete(DishDTO dishDTO);

    //根据id删除口味
    @Delete("delete from dish_flavor where id = #{id}")
    void deleteById(Long id);
    //回显
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getById(Long id);
    //根据ID查询对应的口味
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long id);
}
