package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "菜品接口")
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

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
    public Result<DishDTO> getById(@PathVariable Long id) {
        log.info("根据Id显示菜品信息（回显）:{}", id);
        DishDTO dishDTO = dishService.getById(id);

        return Result.success(dishDTO);
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

        return Result.success();
    }

    /**
     * 起售停售
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售")
    public Result startAndStop(@PathVariable Integer status,Long id) {
        log.info("修改起售停售信息：status = {},id = {}",status,id);
        dishService.startAndStop(status,id);
        return Result.success();
    }

    /**
     * 根据Id批量删除菜品信息
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品信息")
    public Result deleteByIds(@RequestParam List<Long> ids){
        log.info("批量删除菜品信息");
        dishService.deleteByIds(ids);
        return Result.success();
    }

}
