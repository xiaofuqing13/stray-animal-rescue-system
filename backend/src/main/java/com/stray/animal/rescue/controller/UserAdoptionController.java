package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.dto.AdoptionApplicationDTO;
import com.stray.animal.rescue.entity.AdoptionApplication;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.service.AdoptionApplicationService;
import com.stray.animal.rescue.service.AnimalService;
import com.stray.animal.rescue.service.UserService;
import com.stray.animal.rescue.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户端领养申请控制器
 */
@Slf4j
@Tag(name = "用户端领养申请接口", description = "用户领养申请相关接口")
@RestController
@RequestMapping("/user/adoption")
public class UserAdoptionController {
    
    @Resource
    private AdoptionApplicationService adoptionApplicationService;
    
    @Resource
    private AnimalService animalService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private FileUploadUtil fileUploadUtil;
    
    @Resource
    private HttpServletRequest request;
    
    @Resource
    private com.stray.animal.rescue.service.AnimalCategoryService animalCategoryService;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 提交领养申请
     * @param applicationDTO 申请信息
     * @return 操作结果
     */
    @Operation(summary = "提交领养申请")
    @PostMapping("/apply")
    public Result<Void> applyForAdoption(@Valid @RequestBody AdoptionApplicationDTO applicationDTO) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 验证动物是否存在且为待领养状态
        Animal animal = animalService.getById(applicationDTO.getAnimalId());
        if (animal == null) {
            return Result.error("动物不存在");
        }
        
        if (animal.getStatus() != 0) {
            return Result.error("该动物不是待领养状态，无法提交领养申请");
        }
        
        // 检查是否已经提交过申请
        LambdaQueryWrapper<AdoptionApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdoptionApplication::getUserId, currentUser.getId())
                  .eq(AdoptionApplication::getAnimalId, applicationDTO.getAnimalId())
                  .eq(AdoptionApplication::getStatus, 0); // 状态为待审核
        
        long count = adoptionApplicationService.count(queryWrapper);
        if (count > 0) {
            return Result.error("您已经提交过该动物的领养申请，请勿重复提交");
        }
        
        // 创建领养申请
        AdoptionApplication application = new AdoptionApplication();
        application.setUserId(currentUser.getId());
        application.setAnimalId(applicationDTO.getAnimalId());
        application.setReason(applicationDTO.getReason());
        
        // 保存申请
        boolean success = adoptionApplicationService.submitApplication(application);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("提交申请失败，请重试");
        }
    }
    
    /**
     * 获取我的领养申请列表
     * @return 申请列表
     */
    @Operation(summary = "获取我的领养申请列表")
    @GetMapping("/my")
    public Result<List<AdoptionApplicationDTO>> getMyAdoptions() {
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 获取用户领养申请列表
        List<AdoptionApplicationDTO> applications = adoptionApplicationService.getUserApplications(currentUser.getId());
        
        // 补充动物信息
        for (AdoptionApplicationDTO dto : applications) {
            // 获取动物信息
            Animal animal = animalService.getById(dto.getAnimalId());
            if (animal != null) {
                dto.setAnimalName(animal.getName());
                
                // 处理图片URL
                if (StringUtils.hasText(animal.getImageUrl())) {
                    dto.setAnimalImage(fileUploadUtil.getFileUrl(animal.getImageUrl()));
                }
                
                // 获取动物分类名称
                if (animal.getCategoryId() != null) {
                    com.stray.animal.rescue.entity.AnimalCategory category = animalCategoryService.getById(animal.getCategoryId());
                    if (category != null) {
                        dto.setAnimalCategory(category.getName());
                    } else {
                        dto.setAnimalCategory("未分类");
                    }
                } else {
                    dto.setAnimalCategory("未分类");
                }
            }
        }
        
        return Result.success(applications);
    }
    
    /**
     * 获取领养申请详情
     * @param id 申请ID
     * @return 申请详情
     */
    @Operation(summary = "获取领养申请详情")
    @GetMapping("/{id}")
    public Result<AdoptionApplicationDTO> getAdoptionDetail(@PathVariable Long id) {
        // 获取当前用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 获取申请详情
        AdoptionApplication application = adoptionApplicationService.getById(id);
        if (application == null) {
            return Result.error("申请不存在");
        }
        
        // 验证是否是当前用户的申请
        if (!application.getUserId().equals(currentUser.getId())) {
            return Result.error("无权查看其他用户的申请");
        }
        
        // 转换为DTO
        AdoptionApplicationDTO dto = adoptionApplicationService.getApplicationById(id);
        
        return Result.success(dto);
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        // 从请求属性中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            log.error("获取当前用户ID失败，请检查用户是否已登录或JWT拦截器是否正常工作");
            return null;
        }
        
        // 根据ID获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            log.error("获取用户信息失败，用户ID: {}", userId);
        }
        
        return user;
    }
} 