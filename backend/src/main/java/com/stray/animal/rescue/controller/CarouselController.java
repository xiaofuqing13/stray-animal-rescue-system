package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.Carousel;
import com.stray.animal.rescue.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 轮播图控制器
 */
@Slf4j
@Tag(name = "轮播图接口", description = "轮播图相关接口")
@RestController
@RequestMapping("/carousel")
public class CarouselController {
    
    @Resource
    private CarouselService carouselService;
    
    /**
     * 获取激活的轮播图
     */
    @Operation(summary = "获取激活的轮播图")
    @GetMapping("/active")
    public Result<List<Carousel>> getActiveCarousels() {
        List<Carousel> carousels = carouselService.getActiveCarousels();
        return Result.success(carousels);
    }
} 