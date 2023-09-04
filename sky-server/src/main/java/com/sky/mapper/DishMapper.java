package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
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
    DishVO getById(Long id);

    //删除菜品
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    //根据菜品Id获取所有包含的信息
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> getByCategoryId(Long categoryId);

    //根据分类Id获取主键
    @Select("select id from dish where name = #{dishName}")
    Long getByCategoryId_To_setmealDish(String dishName);

    //根据菜品获取状态
    @Select("select status from dish where name = #{dishName}")
    Integer getByName(String dishName);

    //根据name获取对应信息（用于套餐管理的菜品查询）
    //TODO 传入单个字符，需要在mybatis接口中写入@Param(value = "name") String name，实现动态传参
    @Select("select * from dish where name like concat('%',#{name},'%')")
    List<Dish> getByName_to_setmeal(@Param(value = "name") String name);

    //根据菜品名查询
    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> list(Dish dish);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

}
