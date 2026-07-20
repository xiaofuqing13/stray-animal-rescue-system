package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.AnimalCategory;
import com.stray.animal.rescue.service.AnimalCategoryService;
import com.stray.animal.rescue.service.AnimalService;
import com.stray.animal.rescue.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 动物控制器
 */
@Slf4j
@Tag(name = "动物接口", description = "动物查询相关接口")
@RestController
@RequestMapping("/animal")
public class AnimalController {
    
    @Resource
    private AnimalService animalService;
    
    @Resource
    private AnimalCategoryService animalCategoryService;
    
    @Resource
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 获取最新上线的动物列表
     */
    @Operation(summary = "获取最新上线的动物")
    @GetMapping("/latest")
    public Result<List<Animal>> getLatestAnimals(
            @RequestParam(required = false, defaultValue = "8") Integer limit) {
        
        // 创建查询条件
        LambdaQueryWrapper<Animal> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询待领养的动物
        queryWrapper.eq(Animal::getStatus, 0);
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Animal::getCreateTime);
        
        // 限制返回数量
        queryWrapper.last("limit " + limit);
        
        // 查询最新动物列表
        List<Animal> animals = animalService.list(queryWrapper);
        
        // 处理图片URL
        animals.forEach(animal -> {
            if (StringUtils.hasText(animal.getImageUrl())) {
                animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
            }
        });
        
        return Result.success(animals);
    }
    
    /**
     * 获取动物详情
     */
    @Operation(summary = "获取动物详情")
    @GetMapping("/{id}")
    public Result<Animal> getAnimalById(@PathVariable Long id) {
        Animal animal = animalService.getById(id);
        
        if (animal == null) {
            return Result.error("动物不存在");
        }
        
        // 处理图片URL
        if (StringUtils.hasText(animal.getImageUrl())) {
            animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
        }
        
        return Result.success(animal);
    }
    
    /**
     * 获取所有动物分类
     */
    @Operation(summary = "获取所有动物分类")
    @GetMapping("/category/list")
    public Result<List<AnimalCategory>> getAllAnimalCategories() {
        return Result.success(animalCategoryService.listAllCategories());
    }
    
    /**
     * 分页查询动物
     */
    @Operation(summary = "分页查询动物")
    @GetMapping("/page")
    public Result<Page<Animal>> getAnimalPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        
        // 默认查询待领养的动物
        status = status != null ? status : 0;
        
        Page<Animal> page = animalService.getAnimalPage(pageNum, pageSize, keyword, categoryId, status);
        
        // 处理图片URL
        page.getRecords().forEach(animal -> {
            if (StringUtils.hasText(animal.getImageUrl())) {
                animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
            }
        });
        
        return Result.success(page);
    }
} 