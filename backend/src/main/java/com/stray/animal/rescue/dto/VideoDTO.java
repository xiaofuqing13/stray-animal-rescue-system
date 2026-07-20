package com.stray.animal.rescue.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 视频数据传输对象
 */
@Data
public class VideoDTO {
    /**
     * 视频ID
     */
    private Long id;
    
    /**
     * 视频标题
     */
    @NotBlank(message = "视频标题不能为空")
    private String title;
    
    /**
     * 视频描述
     */
    private String description;
    
    /**
     * 视频URL
     */
    @NotBlank(message = "视频URL不能为空")
    private String videoUrl;
    
    /**
     * 封面URL
     */
    private String coverUrl;
    
    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    
    /**
     * 状态：0-禁用 1-正常
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
} 