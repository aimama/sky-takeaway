package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理相关类")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐管理的分类查询，其参数为：{}", setmealPageQueryDTO);

        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result insert(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐，其参数为：{}", setmealDTO);
        setmealService.insert(setmealDTO);

        return Result.success();
    }

    /**
     * 起售停售
     *
     * @return
     */
    @ApiOperation("套餐的起售停售")
    @PostMapping("/status/{status}")
    public Result startAndStop(@PathVariable Integer status, Long id) {
        log.info("套餐的起售停售，其参数为：{}，{}", status, id);

        setmealService.startAndStop(status, id);

        return Result.success();
    }

    /**
     * 回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("修改套餐回显")
    public Result<SetmealVO> getBySetmealId(@PathVariable Long id) {
        log.info("修改套餐回显，其参数为：{}", id);

        SetmealVO setmealVO = setmealService.getBySetmealId(id);

        log.info("回显参数：{}", setmealVO);

        return Result.success(setmealVO);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除")
    public Result deleteByIds(@RequestParam List<Long> ids) {
        log.info("批量删除，其参数为{}",ids);

        setmealService.deleteByIds(ids);

        return Result.success();
    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     * @return
     */
    @ApiOperation("修改套餐信息")
    @PutMapping
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐信息，其参数如下：{}",setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

}
