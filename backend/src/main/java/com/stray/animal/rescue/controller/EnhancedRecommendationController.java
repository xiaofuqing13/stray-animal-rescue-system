package com.stray.animal.rescue.controller;

import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.service.EnhancedRecommendationService;
import com.stray.animal.rescue.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 增强版推荐控制器
 */
@Slf4j
@Tag(name = "增强版推荐系统", description = "多维特征协同过滤推荐")
@RestController
@RequestMapping("/user/recommendation/enhanced")
public class EnhancedRecommendationController {
    
    @Autowired
    private EnhancedRecommendationService recommendationService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 获取个性化推荐（混合策略）
     */
    @Operation(summary = "获取个性化推荐")
    @GetMapping("/personalized")
    public Result<List<Animal>> getPersonalizedRecommendations(
            @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            List<Animal> animals = recommendationService.getPersonalizedRecommendations(userId, limit);
            
            // 处理图片URL
            animals.forEach(animal -> {
                if (StringUtils.hasText(animal.getImageUrl())) {
                    animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
                }
            });
            
            return Result.success(animals);
            
        } catch (Exception e) {
            log.error("获取个性化推荐失败", e);
            return Result.error("获取推荐失败");
        }
    }
    
    /**
     * 获取协同过滤推荐
     */
    @Operation(summary = "获取协同过滤推荐")
    @GetMapping("/collaborative")
    public Result<List<Animal>> getCollaborativeFilteringRecommendations(
            @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            List<Animal> animals = recommendationService.getCollaborativeFilteringRecommendations(userId, limit);
            
            // 处理图片URL
            animals.forEach(animal -> {
                if (StringUtils.hasText(animal.getImageUrl())) {
                    animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
                }
            });
            
            return Result.success(animals);
            
        } catch (Exception e) {
            log.error("获取协同过滤推荐失败", e);
            return Result.error("获取推荐失败");
        }
    }
    
    /**
     * 获取基于内容的推荐
     */
    @Operation(summary = "获取基于内容的推荐")
    @GetMapping("/content-based")
    public Result<List<Animal>> getContentBasedRecommendations(
            @RequestParam(defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            List<Animal> animals = recommendationService.getContentBasedRecommendations(userId, limit);
            
            // 处理图片URL
            animals.forEach(animal -> {
                if (StringUtils.hasText(animal.getImageUrl())) {
                    animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
                }
            });
            
            return Result.success(animals);
            
        } catch (Exception e) {
            log.error("获取内容推荐失败", e);
            return Result.error("获取推荐失败");
        }
    }
    
    /**
     * 获取相似动物推荐
     */
    @Operation(summary = "获取相似动物推荐")
    @GetMapping("/similar/{animalId}")
    public Result<List<Animal>> getSimilarAnimals(
            @PathVariable Long animalId,
            @RequestParam(defaultValue = "6") Integer limit) {
        
        try {
            List<Animal> animals = recommendationService.getSimilarAnimals(animalId, limit);
            
            // 处理图片URL
            animals.forEach(animal -> {
                if (StringUtils.hasText(animal.getImageUrl())) {
                    animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
                }
            });
            
            return Result.success(animals);
            
        } catch (Exception e) {
            log.error("获取相似动物推荐失败", e);
            return Result.error("获取推荐失败");
        }
    }
    
    /**
     * 记录浏览行为
     */
    @Operation(summary = "记录浏览行为")
    @PostMapping("/record/view")
    public Result<Void> recordView(
            @RequestParam Long animalId,
            @RequestParam(defaultValue = "0") Long duration,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            recommendationService.recordInteraction(userId, animalId, 1, duration);
            return Result.success();
            
        } catch (Exception e) {
            log.error("记录浏览行为失败", e);
            return Result.error("记录失败");
        }
    }
    
    /**
     * 记录点赞行为
     */
    @Operation(summary = "记录点赞行为")
    @PostMapping("/record/like")
    public Result<Void> recordLike(
            @RequestParam Long animalId,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            recommendationService.recordInteraction(userId, animalId, 2, 0L);
            return Result.success();
            
        } catch (Exception e) {
            log.error("记录点赞行为失败", e);
            return Result.error("记录失败");
        }
    }
    
    /**
     * 记录收藏行为
     */
    @Operation(summary = "记录收藏行为")
    @PostMapping("/record/favorite")
    public Result<Void> recordFavorite(
            @RequestParam Long animalId,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            recommendationService.recordInteraction(userId, animalId, 3, 0L);
            return Result.success();
            
        } catch (Exception e) {
            log.error("记录收藏行为失败", e);
            return Result.error("记录失败");
        }
    }
}
