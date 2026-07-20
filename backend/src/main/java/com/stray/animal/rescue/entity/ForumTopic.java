package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 论坛话题实体类
 */
@Data
@TableName("forum_topic")
public class ForumTopic {
    
    /**
     * 话题ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 浏览数
     */
    private Integer viewCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
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