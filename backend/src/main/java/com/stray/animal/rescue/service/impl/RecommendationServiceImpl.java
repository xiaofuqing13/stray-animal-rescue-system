package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.UserBehavior;
import com.stray.animal.rescue.mapper.AnimalMapper;
import com.stray.animal.rescue.mapper.UserBehaviorMapper;
import com.stray.animal.rescue.service.RecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类 - 基于改进的协同过滤算法
 */
@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Autowired
    private AnimalMapper animalMapper;
    
    // 行为权重配置
    private static final double WEIGHT_VIEW = 0.3;      // 浏览权重
    private static final double WEIGHT_LIKE = 0.7;      // 点赞权重
    private static final double WEIGHT_FAVORITE = 1.0;  // 收藏权重
    
    // 相似用户数量
    private static final int TOP_K_USERS = 10;
    
    @Override
    public List<Animal> recommendAnimals(Long userId, int limit) {
        try {
            // 1. 获取所有用户的行为数据
            List<Map<String, Object>> allBehaviors = userBehaviorMapper.getAllRecentBehaviors();
            
            if (allBehaviors.isEmpty()) {
                // 如果没有行为数据，返回热门动物
                return getHotAnimals(limit);
            }
            
            // 2. 构建用户-动物行为矩阵
            Map<Long, Map<Long, Double>> userAnimalMatrix = buildUserAnimalMatrix(allBehaviors);
            
            // 3. 获取目标用户的行为向量
            Map<Long, Double> targetUserVector = userAnimalMatrix.get(userId);
            
            if (targetUserVector == null || targetUserVector.isEmpty()) {
                // 如果用户没有行为记录，返回热门动物
                return getHotAnimals(limit);
            }
            
            // 4. 计算与其他用户的相似度
            List<UserSimilarity> similarities = calculateUserSimilarities(userId, targetUserVector, userAnimalMatrix);
            
            // 5. 基于Top-K相似用户预测推荐
            List<Long> recommendedAnimalIds = predictRecommendations(targetUserVector, similarities, userAnimalMatrix, limit);
            
            // 6. 查询推荐的动物详情
            if (recommendedAnimalIds.isEmpty()) {
                return getHotAnimals(limit);
            }
            
            List<Animal> animals = animalMapper.selectBatchIds(recommendedAnimalIds);
            
            // 按推荐顺序排序
            Map<Long, Integer> orderMap = new HashMap<>();
            for (int i = 0; i < recommendedAnimalIds.size(); i++) {
                orderMap.put(recommendedAnimalIds.get(i), i);
            }
            animals.sort(Comparator.comparingInt(a -> orderMap.getOrDefault(a.getId(), Integer.MAX_VALUE)));
            
            return animals;
            
        } catch (Exception e) {
            log.error("推荐算法执行失败", e);
            return getHotAnimals(limit);
        }
    }
    
    @Override
    public List<Animal> getHotAnimals(int limit) {
        // 综合评分 = 浏览数 * 0.3 + 点赞数 * 0.7 + 收藏数 * 1.0
        // 使用SQL计算综合得分并排序
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Animal::getStatus, 0) // 只推荐待领养的动物
               .last("ORDER BY (view_count * 0.3 + like_count * 0.7 + favorite_count * 1.0) DESC LIMIT " + limit);
        
        return animalMapper.selectList(wrapper);
    }
    
    @Override
    public void recordBehavior(Long userId, Long animalId, Integer behaviorType) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setAnimalId(animalId);
        behavior.setBehaviorType(behaviorType);
        behavior.setCreateTime(LocalDateTime.now());
        behavior.setUpdateTime(LocalDateTime.now());
        
        userBehaviorMapper.insert(behavior);
    }
    
    /**
     * 构建用户-动物行为矩阵（加权）
     */
    private Map<Long, Map<Long, Double>> buildUserAnimalMatrix(List<Map<String, Object>> behaviors) {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();
        
        for (Map<String, Object> behavior : behaviors) {
            Long userId = ((Number) behavior.get("user_id")).longValue();
            Long animalId = ((Number) behavior.get("animal_id")).longValue();
            Integer behaviorType = ((Number) behavior.get("behavior_type")).intValue();
            
            // 根据行为类型分配权重
            double weight = switch (behaviorType) {
                case UserBehavior.BehaviorType.VIEW -> WEIGHT_VIEW;
                case UserBehavior.BehaviorType.LIKE -> WEIGHT_LIKE;
                case UserBehavior.BehaviorType.FAVORITE -> WEIGHT_FAVORITE;
                default -> 0.0;
            };
            
            matrix.computeIfAbsent(userId, k -> new HashMap<>())
                  .merge(animalId, weight, Double::max); // 取最大权重
        }
        
        return matrix;
    }
    
    /**
     * 计算用户相似度（改进的Jaccard相似度）
     */
    private List<UserSimilarity> calculateUserSimilarities(
            Long targetUserId, 
            Map<Long, Double> targetVector,
            Map<Long, Map<Long, Double>> matrix) {
        
        List<UserSimilarity> similarities = new ArrayList<>();
        
        for (Map.Entry<Long, Map<Long, Double>> entry : matrix.entrySet()) {
            Long otherUserId = entry.getKey();
            
            // 跳过自己
            if (otherUserId.equals(targetUserId)) {
                continue;
            }
            
            Map<Long, Double> otherVector = entry.getValue();
            
            // 计算改进的Jaccard相似度（考虑权重）
            double similarity = calculateWeightedJaccardSimilarity(targetVector, otherVector);
            
            if (similarity > 0) {
                similarities.add(new UserSimilarity(otherUserId, similarity));
            }
        }
        
        // 按相似度降序排序
        similarities.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        
        return similarities;
    }
    
    /**
     * 计算加权Jaccard相似度
     */
    private double calculateWeightedJaccardSimilarity(Map<Long, Double> vector1, Map<Long, Double> vector2) {
        Set<Long> allAnimals = new HashSet<>();
        allAnimals.addAll(vector1.keySet());
        allAnimals.addAll(vector2.keySet());
        
        if (allAnimals.isEmpty()) {
            return 0.0;
        }
        
        double minSum = 0.0;
        double maxSum = 0.0;
        
        for (Long animalId : allAnimals) {
            double weight1 = vector1.getOrDefault(animalId, 0.0);
            double weight2 = vector2.getOrDefault(animalId, 0.0);
            
            minSum += Math.min(weight1, weight2);
            maxSum += Math.max(weight1, weight2);
        }
        
        return maxSum > 0 ? minSum / maxSum : 0.0;
    }
    
    /**
     * 基于Top-K相似用户预测推荐
     */
    private List<Long> predictRecommendations(
            Map<Long, Double> targetVector,
            List<UserSimilarity> similarities,
            Map<Long, Map<Long, Double>> matrix,
            int limit) {
        
        // 获取Top-K相似用户
        List<UserSimilarity> topKUsers = similarities.stream()
                .limit(TOP_K_USERS)
                .collect(Collectors.toList());
        
        // 计算候选动物的预测得分
        Map<Long, Double> candidateScores = new HashMap<>();
        
        for (UserSimilarity userSim : topKUsers) {
            Map<Long, Double> similarUserVector = matrix.get(userSim.userId);
            
            for (Map.Entry<Long, Double> entry : similarUserVector.entrySet()) {
                Long animalId = entry.getKey();
                Double weight = entry.getValue();
                
                // 排除用户已经交互过的动物
                if (!targetVector.containsKey(animalId)) {
                    // 预测得分 = 相似度 * 行为权重
                    candidateScores.merge(animalId, userSim.similarity * weight, Double::sum);
                }
            }
        }
        
        // 按预测得分排序并返回Top-N
        return candidateScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * 用户相似度内部类
     */
    private static class UserSimilarity {
        Long userId;
        double similarity;
        
        UserSimilarity(Long userId, double similarity) {
            this.userId = userId;
            this.similarity = similarity;
        }
    }
}
