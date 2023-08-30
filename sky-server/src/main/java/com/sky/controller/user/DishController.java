package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类ID查询信息：{}", categoryId);

        /**
         * 实现缓存技术（Redis做缓存数据库）
         * 约定使用String类型，k为dish_+categoryId，V为查询到的信息
         */
        String key = "dish_" + categoryId;
        List<DishVO> dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //不为空，则代表缓存中存在数据，可直接返回给前端,无需走Service层与Mapper层
        if (dishes != null && dishes.size() > 0) {
            log.info("已从缓存中查询到信息，并且返回");
            return Result.success(dishes);
        }
        //否则为空，则代表缓存无数据，需从Service，Mapper获取数据，再存入缓存中
        log.info("缓存中不包含该数据，从数据库端取数据并存储到缓存中");
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        dishes = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key, dishes);

        return Result.success(dishes);
    }

}
