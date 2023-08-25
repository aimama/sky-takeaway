package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 新增中，通过分类名称获取对应菜品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setmeal_Dish_getByCategoryVO {
    private Long id;
    //菜品名称
    private String name;
    //顺序
    private Integer sort;
    //0 停售 1 起售
    private Integer status;
    //更新时间
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private Integer createUser;
    private Integer updateUser;
    //类型
    private Integer type;

}
