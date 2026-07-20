package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 动物分类数据传输对象
 */
@Data
public class AnimalCategoryDTO {
    
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
     * 描述
     */
    private String description;
} 