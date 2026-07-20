package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 论坛评论实体类
 */
@Data
@TableName("forum_comment")
public class ForumComment {
    
    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 话题ID
     */
    private Long topicId;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 父评论ID
     */
    private Long parentId;
    
    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 