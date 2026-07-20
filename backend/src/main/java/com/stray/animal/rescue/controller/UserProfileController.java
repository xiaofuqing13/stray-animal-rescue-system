package com.stray.animal.rescue.controller;

import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.UserProfile;
import com.stray.animal.rescue.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户画像控制器
 */
@Slf4j
@Tag(name = "用户画像管理", description = "用户偏好设置")
@RestController
@RequestMapping("/user/profile")
public class UserProfileController {
    
    @Autowired
    private UserProfileService userProfileService;
    
    /**
     * 获取用户画像
     */
    @Operation(summary = "获取用户画像")
    @GetMapping
    public Result<UserProfile> getUserProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            UserProfile profile = userProfileService.getUserProfile(userId);
            return Result.success(profile);
            
        } catch (Exception e) {
            log.error("获取用户画像失败", e);
            return Result.error("获取失败");
        }
    }
    
    /**
     * 保存/更新用户画像
     */
    @Operation(summary = "保存/更新用户画像")
    @PostMapping
    public Result<Void> saveUserProfile(
            @RequestBody UserProfile profile,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            profile.setUserId(userId);
            userProfileService.saveOrUpdateProfile(profile);
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("保存用户画像失败", e);
            return Result.error("保存失败");
        }
    }
    
    /**
     * 初始化默认画像
     */
    @Operation(summary = "初始化默认画像")
    @PostMapping("/init")
    public Result<Void> initDefaultProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            userProfileService.initDefaultProfile(userId);
            return Result.success();
            
        } catch (Exception e) {
            log.error("初始化用户画像失败", e);
            return Result.error("初始化失败");
        }
    }
}
