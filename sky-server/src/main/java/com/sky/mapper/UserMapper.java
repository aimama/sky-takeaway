package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 查询该Openid是否是新用户信息
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 新增
     * @param user
     */
    void insert(User user);
    @Select("select * from user where id = #{id}")
    User getById(Long userId);

    /**
     * 统计总用户数量
     * @param map
     * @return
     */
    Integer countByCreateTime(Map map);
}
