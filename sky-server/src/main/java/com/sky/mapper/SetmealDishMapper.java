package com.sky.mapper;

import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealDishMapper {
    //Id查询套餐所含菜品的数量
    @Select("select count(*) from setmeal where id = #{id}")
    Integer getCounts(Long id);
}
