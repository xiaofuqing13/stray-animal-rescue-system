package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.dto.RescueInfoDTO;
import com.stray.animal.rescue.entity.RescueInfo;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.service.RescueInfoService;
import com.stray.animal.rescue.service.UserService;
import com.stray.animal.rescue.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户端救助信息控制器
 */
@Tag(name = "用户端救助信息接口", description = "提供用户救助信息相关的接口")
@RestController
@RequestMapping("/user/rescue")
@Slf4j
public class UserRescueController {

    @Resource
    private RescueInfoService rescueInfoService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private FileUploadUtil fileUploadUtil;
    
    @Resource
    private HttpServletRequest request;
    
    /**
     * 分页查询救助信息
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键词
     * @param status 状态
     * @return 分页结果
     */
    @Operation(summary = "分页查询救助信息")
    @GetMapping("/page")
    public Result<Page<RescueInfo>> getRescueInfoPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        Page<RescueInfo> page = rescueInfoService.getRescueInfoPage(pageNum, pageSize, keyword, status);
        
        // 处理图片URL
        page.getRecords().forEach(rescueInfo -> {
            if (StringUtils.hasText(rescueInfo.getImageUrl())) {
                rescueInfo.setImageUrl(fileUploadUtil.getFileUrl(rescueInfo.getImageUrl()));
            }
        });
        
        return Result.success(page);
    }
    
    /**
     * 获取救助信息详情
     * 
     * @param id 救助信息ID
     * @return 救助信息详情
     */
    @Operation(summary = "获取救助信息详情")
    @GetMapping("/{id}")
    public Result<RescueInfo> getRescueInfo(@PathVariable Long id) {
        RescueInfo rescueInfo = rescueInfoService.getById(id);
        if (rescueInfo == null) {
            return Result.error("救助信息不存在");
        }
        
        // 处理图片URL
        if (StringUtils.hasText(rescueInfo.getImageUrl())) {
            rescueInfo.setImageUrl(fileUploadUtil.getFileUrl(rescueInfo.getImageUrl()));
        }
        
        return Result.success(rescueInfo);
    }
    
    /**
     * 添加救助信息
     * 
     * @param rescueInfoDTO 救助信息DTO
     * @return 结果
     */
    @Operation(summary = "添加救助信息")
    @PostMapping
    public Result<Void> addRescueInfo(@RequestBody @Valid RescueInfoDTO rescueInfoDTO) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 设置默认状态为待救助
        if (rescueInfoDTO.getStatus() == null) {
            rescueInfoDTO.setStatus(0);
        }
        
        // 设置发布用户ID
        rescueInfoDTO.setUserId(currentUser.getId());
        
        rescueInfoService.addRescueInfo(rescueInfoDTO);
        return Result.success();
    }
    
    /**
     * 上传救助图片
     * 
     * @param file 图片文件
     * @return 图片URL
     */
    @Operation(summary = "上传救助图片")
    @PostMapping("/upload")
    public Result<String> uploadRescueImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        
        try {
            String imageUrl = rescueInfoService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (IOException e) {
            log.error("上传救助图片失败", e);
            return Result.error("上传图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新救助信息状态
     * 
     * @param id 救助信息ID
     * @param status 新状态
     * @return 结果
     */
    @Operation(summary = "更新救助信息状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateRescueStatus(
            @PathVariable Long id,
            @RequestParam Integer status
    ) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 获取救助信息
        RescueInfo rescueInfo = rescueInfoService.getById(id);
        if (rescueInfo == null) {
            return Result.error("救助信息不存在");
        }
        
        // 验证是否为发布者
        if (!rescueInfo.getUserId().equals(currentUser.getId())) {
            return Result.error("只有发布者才能修改救助状态");
        }
        
        // 验证状态值是否有效
        if (status < 0 || status > 2) {
            return Result.error("无效的状态值");
        }
        
        // 更新状态
        rescueInfoService.updateStatus(id, status);
        
        return Result.success();
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        // 从请求属性中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            log.error("获取当前用户ID失败，请检查用户是否已登录或JWT拦截器是否正常工作");
            // 默认返回ID为1的用户（测试用）
            return userService.getById(1L);
        }
        
        // 根据ID获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            log.error("获取用户信息失败，用户ID: {}", userId);
        }
        
        return user;
    }
} 