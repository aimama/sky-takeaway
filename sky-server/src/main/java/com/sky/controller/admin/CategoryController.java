package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "菜品分类相关类")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired(required = false)
    private CategoryService categoryService;

    /**
     * 菜品的分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品的分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询：{}", categoryPageQueryDTO);

        PageResult pageResult = categoryService.page(categoryPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 新增菜品分类
     *
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result insert(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类，其参数为:{}", categoryDTO);
        categoryService.insert(categoryDTO);
        return Result.success();
    }

    /**
     * 根据ID删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据ID删除分类")
    public Result deleteById(Long id) {
        log.info("根据ID删除分类，其参数为：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据类型查询分类信息
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类信息")
    public Result<List<Category>> getByType(Integer type) {
        log.info("根据类型查询分类信息,其参数为：{}", type);
        List<Category> categoryList = categoryService.getByType(type);
        return Result.success(categoryList);
    }

    /**
     * 启用禁用分类
     *
     * @param id
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用分类")
    public Result startOrStop(Long id, @PathVariable Integer status) {
        log.info("启用禁用分类，其参数为：{},{}", status, id);
        categoryService.startOrStop(id, status);
        return Result.success();
    }

    /**
     * 分类回显
     *
     * @param id
     * @return
     */
    @GetMapping
    @ApiOperation("回显")
    public Result<Category> getById(Long id) {
        log.info("分类信息回显，其参数为：{}", id);
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 修改分类信息
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类信息")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类信息，其参数为：{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }
}
