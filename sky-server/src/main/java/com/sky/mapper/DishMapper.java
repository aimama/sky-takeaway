package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    //插入菜品
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);
    //分页查询(设计三张表的查询，分别是菜品分类表，菜品表，菜品口味表)
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    //菜品修改
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
    //回显
    @Select("select * from dish where id = #{id}")
    DishDTO getById(Long id);
    //删除菜品
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);
}
