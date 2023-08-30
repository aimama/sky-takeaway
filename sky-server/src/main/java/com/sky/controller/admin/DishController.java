package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@Api(tags = "菜品接口")
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品");
        dishService.save(dishDTO);

        //清除缓存(目的：保持数据的一致性)
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        log.info("为保持数据一致性，已清除缓存");

        return Result.success();
    }

    /**
     * 菜品的分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品的分类查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("正在请求分页查询:{}", dishPageQueryDTO);

        PageResult results = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(results);
    }

    /**
     * 根据Id查询信息(回显)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("回显菜品信息")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据Id显示菜品信息（回显）:{}", id);
        DishVO dishVO = dishService.getById(id);

        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);

        dishService.update(dishDTO);

        //清除缓存(目的：保持数据的一致性)
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        log.info("为保持数据一致性，已清除缓存");

        return Result.success();
    }

    /**
     * 起售停售
     *
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售")
    public Result startAndStop(@PathVariable Integer status, Long id) {
        log.info("修改起售停售信息：status = {},id = {}", status, id);
        dishService.startAndStop(status, id);

        //清除缓存(目的：保持数据的一致性)
        String key = "dish_*";
        cleanCache(key);
        log.info("为保持数据一致性，已清除缓存");

        return Result.success();
    }

    /**
     * 根据Id批量删除菜品信息
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品信息")
    public Result deleteByIds(@RequestParam List<Long> ids) {
        log.info("批量删除菜品信息");
        dishService.deleteByIds(ids);

        //清除缓存(目的：保持数据的一致性),全部清除缓存
        String key = "dish_*";
        cleanCache(key);
        log.info("为保持数据一致性，已清除缓存");

        return Result.success();
    }

    /**
     * 根据分类id获取对应菜品信息
     *
     * @return
     */
    //TODO 用于新增套餐中选择菜品接口
    @GetMapping("/list")
    @ApiOperation("根据要求获取对应菜品信息(套餐管理查询菜品)")
    public Result<List<Dish>> getByCategoryId(String name, Long categoryId) {
        log.info("根据分类要求获取对应菜品信息,其参数如下：{},name = {}", categoryId, name);

        List<Dish> Dishes = dishService.getByCategoryId(name, categoryId);

        return Result.success(Dishes);
    }

    /**
     * 清除缓存数据
     * @param pattern
     */
    public void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }


}
