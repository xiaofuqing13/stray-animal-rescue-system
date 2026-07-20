package com.stray.animal.rescue.controller;

import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.service.AnimalInteractionService;
import com.stray.animal.rescue.service.RecommendationService;
import com.stray.animal.rescue.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐系统控制器
 */
@Tag(name = "推荐系统")
@RestController
@RequestMapping("/user/recommendation")
public class RecommendationController {
    
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("recommendationServiceImpl")
    private RecommendationService recommendationService;
    
    @Autowired
    private com.stray.animal.rescue.service.EnhancedRecommendationService enhancedRecommendationService;
    
    @Autowired
    private AnimalInteractionService animalInteractionService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /** 统一规范化动物图片 URL，避免数据库脏数据导致前端破图 */
    private void normalizeImageUrls(List<Animal> animals) {
        if (animals == null) return;
        animals.forEach(a -> {
            if (StringUtils.hasText(a.getImageUrl())) {
                a.setImageUrl(fileUploadUtil.getFileUrl(a.getImageUrl()));
            }
        });
    }

    @Operation(summary = "获取个性化推荐")
    @GetMapping("/personalized")
    public Result<List<Animal>> getPersonalizedRecommendations(
            @RequestParam(defaultValue = "8") Integer limit,
            HttpServletRequest request) {
        
        // 从请求属性中获取userId（由拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        
        // 如果未登录，返回热门动物
        if (userId == null) {
            List<Animal> animals = recommendationService.getHotAnimals(limit);
            normalizeImageUrls(animals);
            return Result.success(animals);
        }
        
        // 使用增强版推荐服务，基于用户画像和行为数据
        List<Animal> animals = enhancedRecommendationService.getPersonalizedRecommendations(userId, limit);
        normalizeImageUrls(animals);
        return Result.success(animals);
    }
    
    @Operation(summary = "获取热门动物")
    @GetMapping("/hot")
    public Result<List<Animal>> getHotAnimals(@RequestParam(defaultValue = "8") Integer limit) {
        List<Animal> animals = recommendationService.getHotAnimals(limit);
        normalizeImageUrls(animals);
        return Result.success(animals);
    }
    
    @Operation(summary = "点赞动物")
    @PostMapping("/like/{animalId}")
    public Result<Void> likeAnimal(@PathVariable Long animalId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        animalInteractionService.likeAnimal(userId, animalId);
        return Result.success();
    }
    
    @Operation(summary = "取消点赞")
    @DeleteMapping("/like/{animalId}")
    public Result<Void> unlikeAnimal(@PathVariable Long animalId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        animalInteractionService.unlikeAnimal(userId, animalId);
        return Result.success();
    }
    
    @Operation(summary = "收藏动物")
    @PostMapping("/favorite/{animalId}")
    public Result<Void> favoriteAnimal(@PathVariable Long animalId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        animalInteractionService.favoriteAnimal(userId, animalId);
        return Result.success();
    }
    
    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorite/{animalId}")
    public Result<Void> unfavoriteAnimal(@PathVariable Long animalId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        animalInteractionService.unfavoriteAnimal(userId, animalId);
        return Result.success();
    }
    
    @Operation(summary = "获取动物互动状态")
    @GetMapping("/interaction/{animalId}")
    public Result<Map<String, Boolean>> getInteractionStatus(
            @PathVariable Long animalId, 
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        
        Map<String, Boolean> status = new HashMap<>();
        
        // 如果未登录，返回默认状态
        if (userId == null) {
            status.put("isLiked", false);
            status.put("isFavorited", false);
        } else {
            status.put("isLiked", animalInteractionService.isLiked(userId, animalId));
            status.put("isFavorited", animalInteractionService.isFavorited(userId, animalId));
        }
        
        return Result.success(status);
    }
}
