package com.stray.animal.rescue.dto;

import com.stray.animal.rescue.entity.User;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 论坛评论DTO
 */
@Data
public class CommentDTO {
    
    /**
     * 评论ID
     */
    private Long id;
    
    /**
     * 话题ID
     */
    @NotNull(message = "话题ID不能为空")
    private Long topicId;
    
    /**
     * 内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 发布用户信息
     */
    private User user;
    
    /**
     * 父评论ID
     */
    private Long parentId;
    
    /**
     * 父评论信息
     */
    private CommentDTO parentComment;
    
    /**
     * 回复评论列表
     */
    private List<CommentDTO> replies;
    
    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private String createTime;
} 