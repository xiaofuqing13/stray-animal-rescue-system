package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户动物交互详情实体类
 */
@Data
@TableName("user_animal_interaction")
public class UserAnimalInteraction {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 动物ID
     */
    private Long animalId;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 总浏览时长（秒）
     */
    private Long totalViewDuration;
    
    /**
     * 平均浏览时长（秒）
     */
    private Double avgViewDuration;
    
    /**
     * 是否点赞：0-否 1-是
     */
    private Integer isLiked;
    
    /**
     * 是否收藏：0-否 1-是
     */
    private Integer isFavorited;
    
    /**
     * 是否评论：0-否 1-是
     */
    private Integer isCommented;
    
    /**
     * 是否申请领养：0-否 1-是
     */
    private Integer hasApplied;
    
    /**
     * 交互得分
     */
    private Double interactionScore;
    
    /**
     * 最后交互时间
     */
    private LocalDateTime lastInteractionTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
