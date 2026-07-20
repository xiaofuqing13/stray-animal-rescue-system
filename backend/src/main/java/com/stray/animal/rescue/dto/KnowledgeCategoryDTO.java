package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 知识分类数据传输对象
 */
@Data
public class KnowledgeCategoryDTO {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    private String name;
    
    /**
     * 描述
     */
    @Size(max = 255, message = "描述长度不能超过255个字符")
    private String description;
} 