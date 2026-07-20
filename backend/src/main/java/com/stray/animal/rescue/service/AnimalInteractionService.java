package com.stray.animal.rescue.service;

/**
 * 动物互动服务接口（点赞、收藏）
 */
public interface AnimalInteractionService {
    
    /**
     * 点赞动物
     */
    void likeAnimal(Long userId, Long animalId);
    
    /**
     * 取消点赞
     */
    void unlikeAnimal(Long userId, Long animalId);
    
    /**
     * 收藏动物
     */
    void favoriteAnimal(Long userId, Long animalId);
    
    /**
     * 取消收藏
     */
    void unfavoriteAnimal(Long userId, Long animalId);
    
    /**
     * 检查是否已点赞
     */
    boolean isLiked(Long userId, Long animalId);
    
    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long animalId);
}
