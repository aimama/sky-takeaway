package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    //分页查询

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    //新增分类
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser} )")
    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    //删除分类
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    //根据分类查询信息
    @Select("select * from category where type = #{type} order by type,create_time")
    List<Category> getByType(Integer type);

    //修改信息
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    //回显
    @Select("select * from category where id = #{id}")
    Category getById(Long id);
}
