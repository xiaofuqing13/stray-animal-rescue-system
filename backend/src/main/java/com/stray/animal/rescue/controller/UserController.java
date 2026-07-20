package com.stray.animal.rescue.controller;

import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.dto.UserInfoDTO;
import com.stray.animal.rescue.dto.UserLoginDTO;
import com.stray.animal.rescue.dto.UserRegisterDTO;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户接口", description = "用户登录注册等接口")
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Resource
    private UserService userService;
    
    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid UserRegisterDTO registerDTO, 
                                 @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws IOException {
        Long userId = userService.register(registerDTO, avatar);
        return Result.success(userId);
    }
    
    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            Map<String, Object> loginResult = userService.login(loginDTO);
            
            if (loginResult == null) {
                return Result.error("用户名或密码错误");
            }
            
            return Result.success(loginResult);
        } catch (RuntimeException e) {
            // 捕获运行时异常，返回友好的错误消息
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfoDTO> getUserInfo(HttpServletRequest request) {
        // 从请求属性中获取userId
        Long userId = (Long) request.getAttribute("userId");
        
        UserInfoDTO userInfo = userService.getUserInfo(userId);
        return Result.success(userInfo);
    }
    
    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public Result<UserInfoDTO> updateUserInfo(HttpServletRequest request,
                                              @RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "phone", required = false) String phone,
                                              @RequestParam(value = "email", required = false) String email,
                                              @RequestParam(value = "address", required = false) String address,
                                              @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws IOException {
        // 从请求属性中获取userId
        Long userId = (Long) request.getAttribute("userId");
        
        // 更新用户信息
        UserInfoDTO userInfo = userService.updateUserInfo(userId, name, phone, email, address, avatar);
        return Result.success(userInfo);
    }
    
    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Boolean> changePassword(HttpServletRequest request, @RequestBody Map<String, String> passwordMap) {
        // 从请求属性中获取userId
        Long userId = (Long) request.getAttribute("userId");
        
        try {
            String oldPassword = passwordMap.get("oldPassword");
            String newPassword = passwordMap.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return Result.error("密码参数不能为空");
            }
            
            // 修改密码
            boolean result = userService.changePassword(userId, oldPassword, newPassword);
            
            if (result) {
                return Result.success(true, "密码修改成功");
            } else {
                return Result.error("原密码错误");
            }
        } catch (Exception e) {
            // 捕获并记录异常
            e.printStackTrace();
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户收藏列表
     */
    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/favorites")
    public Result<?> getUserFavorites(HttpServletRequest request) {
        // 从请求属性中获取userId
        Long userId = (Long) request.getAttribute("userId");
        
        try {
            // 获取收藏列表
            java.util.List<?> favorites = userService.getUserFavorites(userId);
            return Result.success(favorites);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取收藏列表失败: " + e.getMessage());
        }
    }
} 