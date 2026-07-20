package com.stray.animal.rescue.service;

import com.stray.animal.rescue.entity.AnimalComment;

import java.util.List;

/**
 * 动物评论服务接口
 */
public interface AnimalCommentService {
    
    /**
     * 获取动物评论列表
     */
    List<AnimalComment> getCommentsByAnimalId(Long animalId);
    
    /**
     * 添加评论
     */
    void addComment(AnimalComment comment);
    
    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);
}
