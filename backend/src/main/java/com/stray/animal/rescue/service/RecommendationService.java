package com.stray.animal.rescue.service;

import com.stray.animal.rescue.entity.Animal;

import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendationService {
    
    /**
     * 为用户推荐动物（基于协同过滤）
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的动物列表
     */
    List<Animal> recommendAnimals(Long userId, int limit);
    
    /**
     * 获取热门动物（基于浏览、点赞、收藏综合评分）
     * @param limit 数量
     * @return 热门动物列表
     */
    List<Animal> getHotAnimals(int limit);
    
    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param animalId 动物ID
     * @param behaviorType 行为类型
     */
    void recordBehavior(Long userId, Long animalId, Integer behaviorType);
}
