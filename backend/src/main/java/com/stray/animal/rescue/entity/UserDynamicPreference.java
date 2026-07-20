package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户动态偏好实体类
 */
@Data
@TableName("user_dynamic_preference")
public class UserDynamicPreference {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 类别浏览统计（JSON）{"猫":15,"狗":5}
     */
    private String categoryViews;
    
    /**
     * 品种浏览统计（JSON）{"英短":8,"橘猫":7}
     */
    private String breedViews;
    
    /**
     * 体型浏览统计（JSON）{"小型":10,"中型":5}
     */
    private String sizeViews;
    
    /**
     * 性格浏览统计（JSON）{"温顺":12,"活泼":8}
     */
    private String personalityViews;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
}
