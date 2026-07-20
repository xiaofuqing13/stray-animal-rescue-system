package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 动物数据传输对象
 */
@Data
public class AnimalDTO {
    
    /**
     * 动物ID
     */
    private Long id;
    
    /**
     * 名称
     */
    @NotBlank(message = "动物名称不能为空")
    private String name;
    
    /**
     * 类别ID
     */
    @NotNull(message = "请选择动物分类")
    private Long categoryId;
    
    /**
     * 品种
     */
    private String breed;
    
    /**
     * 性别 0-雄性 1-雌性
     */
    @NotNull(message = "请选择性别")
    private Integer gender;
    
    /**
     * 年龄(月)
     */
    private Integer age;
    
    /**
     * 体重(kg)
     */
    private BigDecimal weight;
    
    /**
     * 健康状态
     */
    private String healthStatus;
    
    /**
     * 特征
     */
    private String features;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 状态 0-待领养 1-已领养 2-救助中
     */
    @NotNull(message = "请选择状态")
    private Integer status;
    
    /**
     * 救助时间
     */
    private LocalDateTime rescueTime;
} 