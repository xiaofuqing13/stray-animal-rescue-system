package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.CommentDTO;
import com.stray.animal.rescue.entity.ForumComment;

import java.util.List;

/**
 * 论坛评论服务接口
 */
public interface ForumCommentService extends IService<ForumComment> {
    
    /**
     * 获取话题评论列表
     *
     * @param topicId 话题ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    Page<CommentDTO> getCommentPage(Long topicId, Integer pageNum, Integer pageSize);
    
    /**
     * 添加评论
     *
     * @param commentDTO 评论DTO
     * @return 评论ID
     */
    Long addComment(CommentDTO commentDTO);
    
    /**
     * 删除评论
     *
     * @param id 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteComment(Long id, Long userId);
    
    /**
     * 获取评论回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    List<CommentDTO> getCommentReplies(Long commentId);
} 