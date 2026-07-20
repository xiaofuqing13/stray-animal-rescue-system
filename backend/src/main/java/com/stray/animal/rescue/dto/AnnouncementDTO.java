package com.stray.animal.rescue.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 通知公告数据传输对象
 * 用于前后端交互时的数据封装
 */
@Data
public class AnnouncementDTO {
    /**
     * 公告ID
     */
    private Long id;
    
    /**
     * 公告标题
     * 不能为空
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 公告内容
     * 不能为空
     */
    @NotBlank(message = "内容不能为空")
    private String content;
    
    /**
     * 公告状态：0-禁用 1-正常
     * 不能为空
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
} 