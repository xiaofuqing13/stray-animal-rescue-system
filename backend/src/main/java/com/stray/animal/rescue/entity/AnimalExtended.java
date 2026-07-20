package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动物扩展属性实体类
 */
@Data
@TableName("animal_extended")
public class AnimalExtended {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 动物ID
     */
    private Long animalId;
    
    /**
     * 体型分类：小型/中型/大型
     */
    private String size;
    
    /**
     * 性格标签（JSON数组）["温顺","活泼","粘人"]
     */
    private String personality;
    
    /**
     * 领养费用（0表示免费）
     */
    private Integer adoptionFee;
    
    /**
     * 特殊需求（JSON数组）["需要大空间","适合新手"]
     */
    private String specialNeeds;
    
    /**
     * 救助天数
     */
    private Integer daysInRescue;
    
    /**
     * 热度得分
     */
    private Double popularityScore;
    
    /**
     * 活力等级：低/中/高
     */
    private String energyLevel;
    
    /**
     * 友好度：低/中/高
     */
    private String friendliness;
    
    /**
     * 可训练度：低/中/高
     */
    private String trainability;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
