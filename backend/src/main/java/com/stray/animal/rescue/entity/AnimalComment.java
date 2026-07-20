package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动物评论实体类
 */
@Data
@TableName("animal_comment")
public class AnimalComment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 动物ID
     */
    private Long animalId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID（回复）
     */
    private Long parentId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String username;
    
    /**
     * 用户头像（非数据库字段）
     */
    @TableField(exist = false)
    private String avatar;
}
