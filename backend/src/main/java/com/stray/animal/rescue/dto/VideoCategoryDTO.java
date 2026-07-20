package com.stray.animal.rescue.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 视频分类数据传输对象
 */
@Data
public class VideoCategoryDTO {
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    /**
     * 分类描述
     */
    private String description;
} 