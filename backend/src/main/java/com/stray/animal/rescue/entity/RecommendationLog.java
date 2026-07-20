package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐日志实体类
 */
@Data
@TableName("recommendation_log")
public class RecommendationLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 推荐的动物ID
     */
    private Long animalId;
    
    /**
     * 推荐得分
     */
    private Double recommendationScore;
    
    /**
     * 推荐原因
     */
    private String recommendationReason;
    
    /**
     * 算法类型：CF/CB/HOT/HYBRID
     */
    private String algorithmType;
    
    /**
     * 是否点击：0-否 1-是
     */
    private Integer isClicked;
    
    /**
     * 是否最终领养：0-否 1-是
     */
    private Integer isAdopted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
