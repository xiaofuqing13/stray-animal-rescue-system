package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 轮播图DTO
 */
@Data
public class CarouselDTO {
    
    /**
     * 轮播图ID
     */
    private Long id;
    
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;
    
    /**
     * 状态 0-禁用 1-正常
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
} 