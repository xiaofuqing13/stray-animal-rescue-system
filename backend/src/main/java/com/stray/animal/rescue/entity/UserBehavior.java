package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户行为记录实体类
 */
@Data
@TableName("user_behavior")
public class UserBehavior {
    
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
     * 行为类型 1-浏览 2-点赞 3-收藏
     */
    private Integer behaviorType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 行为类型常量
     */
    public static class BehaviorType {
        public static final int VIEW = 1;      // 浏览
        public static final int LIKE = 2;      // 点赞
        public static final int FAVORITE = 3;  // 收藏
    }
}
