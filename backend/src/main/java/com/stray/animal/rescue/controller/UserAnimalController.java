package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.dto.AdoptionApplicationDTO;
import com.stray.animal.rescue.entity.AdoptionApplication;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.AnimalCategory;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.service.AdoptionApplicationService;
import com.stray.animal.rescue.service.AnimalCategoryService;
import com.stray.animal.rescue.service.AnimalService;
import com.stray.animal.rescue.service.UserService;
import com.stray.animal.rescue.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户端流浪动物控制器
 */
@Slf4j
@Tag(name = "用户端流浪动物接口", description = "用户端流浪动物查询和领养相关接口")
@RestController
@RequestMapping("/user/animal")
public class UserAnimalController {
    
    @Resource
    private AnimalService animalService;
    
    @Resource
    private AnimalCategoryService animalCategoryService;
    
    @Resource
    private AdoptionApplicationService adoptionApplicationService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private FileUploadUtil fileUploadUtil;
    
    @Resource
    private com.stray.animal.rescue.service.RecommendationService recommendationService;
    
    /**
     * 获取待领养动物列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @return 分页结果
     */
    @Operation(summary = "获取待领养动物列表")
    @GetMapping("/adoptable")
    public Result<Page<Animal>> getAdoptableAnimals(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        
        // 获取所有有待审核申请的动物ID列表
        List<Long> animalIdsWithPendingApp = adoptionApplicationService.getAnimalIdsWithPendingApplications();
        
        // 创建查询条件
        LambdaQueryWrapper<Animal> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询待领养的动物，且排除掉已有待审核申请的动物
        queryWrapper.eq(Animal::getStatus, 0);
        if (!animalIdsWithPendingApp.isEmpty()) {
            queryWrapper.notIn(Animal::getId, animalIdsWithPendingApp);
        }
        
        // 按关键字查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(Animal::getName, keyword)
                        .or()
                        .like(Animal::getFeatures, keyword)
                        .or()
                        .like(Animal::getDescription, keyword)
                        .or()
                        .like(Animal::getBreed, keyword)
            );
        }
        
        // 按分类查询
        if (categoryId != null) {
            queryWrapper.eq(Animal::getCategoryId, categoryId);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Animal::getCreateTime);
        
        // 分页查询
        Page<Animal> page = animalService.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 处理图片URL和分类名称
        page.getRecords().forEach(animal -> {
            // 处理图片URL
            if (StringUtils.hasText(animal.getImageUrl())) {
                animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
            }
            
            // 获取分类名称
            if (animal.getCategoryId() != null) {
                AnimalCategory category = animalCategoryService.getById(animal.getCategoryId());
                if (category != null) {
                    // 使用临时字段存储分类名称供前端显示
                    animal.getParams().put("categoryName", category.getName());
                }
            }
        });
        
        return Result.success(page);
    }
    
    /**
     * 获取动物详情
     * @param id 动物ID
     * @return 动物详情
     */
    @Operation(summary = "获取动物详情")
    @GetMapping("/{id}")
    public Result<Animal> getAnimalDetail(
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) {
        
        Animal animal = animalService.getById(id);
        
        if (animal == null) {
            return Result.error("动物不存在");
        }
        
        // 记录浏览行为（如果用户已登录）
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId != null) {
                // 记录浏览行为
                recommendationService.recordBehavior(userId, id, 
                    com.stray.animal.rescue.entity.UserBehavior.BehaviorType.VIEW);
                
                // 更新浏览数
                animal.setViewCount(animal.getViewCount() + 1);
                animalService.updateById(animal);
            }
        } catch (Exception e) {
            log.warn("记录浏览行为失败", e);
        }
        
        // 处理图片URL
        if (StringUtils.hasText(animal.getImageUrl())) {
            animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
        }
        
        // 获取分类名称
        if (animal.getCategoryId() != null) {
            AnimalCategory category = animalCategoryService.getById(animal.getCategoryId());
            if (category != null) {
                // 使用临时字段存储分类名称供前端显示
                animal.getParams().put("categoryName", category.getName());
            }
        }
        
        return Result.success(animal);
    }
    
    /**
     * 获取动物分类列表
     * @return 分类列表
     */
    @Operation(summary = "获取动物分类列表")
    @GetMapping("/categories")
    public Result<List<AnimalCategory>> getAnimalCategories() {
        List<AnimalCategory> categories = animalCategoryService.list();
        return Result.success(categories);
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        // 从Session或Redis中获取当前用户信息
        // 这里实现用户认证逻辑，或者从拦截器中获取用户信息
        // 示例代码，实际项目中需要根据认证机制获取用户
        // 假设用户ID为1
        return userService.getById(1L);
    }
} 