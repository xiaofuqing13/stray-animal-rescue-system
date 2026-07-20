package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.Announcement;
import com.stray.animal.rescue.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 公告控制器
 */
@Tag(name = "公告接口", description = "公告查询接口")
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    
    @Resource
    private AnnouncementService announcementService;
    
    /**
     * 获取公告列表
     */
    @Operation(summary = "获取公告列表")
    @GetMapping("/list")
    public Result<List<Announcement>> getAnnouncementList(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        // 构建查询条件
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        // 只查询状态正常的公告
        queryWrapper.eq(Announcement::getStatus, 1);
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Announcement::getCreateTime);
        // 限制返回数量
        queryWrapper.last("limit " + limit);
        
        // 查询公告列表
        List<Announcement> list = announcementService.list(queryWrapper);
        return Result.success(list);
    }
    
    /**
     * 获取公告详情
     */
    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null || announcement.getStatus() != 1) {
            return Result.error("公告不存在或已禁用");
        }
        return Result.success(announcement);
    }
} 