package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户画像实体类
 */
@Data
@TableName("user_profile")
public class UserProfile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 偏好类别（JSON数组）["猫","狗"]
     */
    private String preferredCategories;
    
    /**
     * 偏好体型：小型/中型/大型
     */
    private String preferredSize;
    
    /**
     * 偏好年龄段：幼年/成年/老年
     */
    private String preferredAgeRange;
    
    /**
     * 偏好性别：雄性/雌性/无偏好
     */
    private String preferredGender;
    
    /**
     * 居住环境：公寓/别墅/农村
     */
    private String livingEnvironment;
    
    /**
     * 养宠经验：新手/有经验/专业
     */
    private String experience;
    
    /**
     * 偏好地点
     */
    private String preferredLocation;
    
    /**
     * 活跃时间段（JSON数组）
     */
    private String activeHours;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
