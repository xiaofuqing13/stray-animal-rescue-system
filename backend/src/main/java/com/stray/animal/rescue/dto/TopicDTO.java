package com.stray.animal.rescue.dto;

import com.stray.animal.rescue.entity.User;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 论坛话题DTO
 */
@Data
public class TopicDTO {
    
    /**
     * 话题ID
     */
    private Long id;
    
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;
    
    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
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
     * 发布用户信息
     */
    private User user;
    
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
    private String createTime;
} 