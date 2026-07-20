package com.stray.animal.rescue.service;

import com.stray.animal.rescue.entity.Animal;

import java.util.List;

/**
 * 增强版推荐服务接口
 */
public interface EnhancedRecommendationService {
    
    /**
     * 获取个性化推荐（混合策略）
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的动物列表
     */
    List<Animal> getPersonalizedRecommendations(Long userId, int limit);
    
    /**
     * 基于协同过滤的推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的动物列表
     */
    List<Animal> getCollaborativeFilteringRecommendations(Long userId, int limit);
    
    /**
     * 基于内容的推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的动物列表
     */
    List<Animal> getContentBasedRecommendations(Long userId, int limit);
    
    /**
     * 获取相似动物推荐
     * @param animalId 动物ID
     * @param limit 推荐数量
     * @return 相似的动物列表
     */
    List<Animal> getSimilarAnimals(Long animalId, int limit);
    
    /**
     * 记录用户交互行为
     * @param userId 用户ID
     * @param animalId 动物ID
     * @param behaviorType 行为类型
     * @param duration 持续时间（秒）
     */
    void recordInteraction(Long userId, Long animalId, Integer behaviorType, Long duration);
    
    /**
     * 更新用户动态偏好
     * @param userId 用户ID
     */
    void updateUserDynamicPreference(Long userId);
}
