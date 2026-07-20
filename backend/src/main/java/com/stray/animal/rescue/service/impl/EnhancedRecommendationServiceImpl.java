package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stray.animal.rescue.entity.*;
import com.stray.animal.rescue.mapper.*;
import com.stray.animal.rescue.service.EnhancedRecommendationService;
import com.stray.animal.rescue.util.FeatureVectorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增强版推荐服务实现 - 多维特征协同过滤
 */
@Slf4j
@Service
public class EnhancedRecommendationServiceImpl implements EnhancedRecommendationService {
    
    @Autowired
    private UserProfileMapper userProfileMapper;
    
    @Autowired
    private UserDynamicPreferenceMapper userDynamicPreferenceMapper;
    
    @Autowired
    private AnimalExtendedMapper animalExtendedMapper;
    
    @Autowired
    private UserAnimalInteractionMapper userAnimalInteractionMapper;
    
    @Autowired
    private AnimalMapper animalMapper;
    
    @Autowired
    private AnimalCategoryMapper animalCategoryMapper;
    
    @Autowired
    private RecommendationLogMapper recommendationLogMapper;
    
    // 特征维度常量
    private static final int USER_FEATURE_DIM = 20;
    private static final int ANIMAL_FEATURE_DIM = 15;
    
    // 推荐策略权重
    private static final double WEIGHT_CF = 0.6;  // 协同过滤
    private static final double WEIGHT_CB = 0.3;  // 内容推荐
    private static final double WEIGHT_HOT = 0.1; // 热度推荐
    
    // 相似用户数量
    private static final int TOP_K_USERS = 10;
    
    // 所有可能的类别
    private static final List<String> ALL_CATEGORIES = Arrays.asList("猫", "狗", "兔", "鼠", "其他");
    private static final List<String> ALL_SIZES = Arrays.asList("小型", "中型", "大型");
    private static final List<String> ALL_AGE_RANGES = Arrays.asList("幼年", "成年", "老年");
    private static final List<String> ALL_GENDERS = Arrays.asList("雄性", "雌性");
    
    @Override
    public List<Animal> getPersonalizedRecommendations(Long userId, int limit) {
        try {
            log.info("开始为用户{}生成个性化推荐，数量：{}", userId, limit);
            
            // 1. 协同过滤推荐（60%）
            List<Animal> cfAnimals = getCollaborativeFilteringRecommendations(userId, limit * 2);
            
            // 2. 内容推荐（30%）
            List<Animal> cbAnimals = getContentBasedRecommendations(userId, limit * 2);
            
            // 3. 热度推荐（10%）
            List<Animal> hotAnimals = getHotAnimals(limit);
            
            // 4. 融合推荐结果
            List<Animal> result = mergeRecommendations(cfAnimals, cbAnimals, hotAnimals, limit);
            
            // 5. 记录推荐日志
            logRecommendations(userId, result, "HYBRID");
            
            log.info("为用户{}生成了{}条推荐", userId, result.size());
            return result;
            
        } catch (Exception e) {
            log.error("生成个性化推荐失败", e);
            return getHotAnimals(limit);
        }
    }
    
    @Override
    public List<Animal> getCollaborativeFilteringRecommendations(Long userId, int limit) {
        try {
            log.info("开始协同过滤推荐，用户：{}", userId);
            
            // 1. 获取所有用户的交互数据
            List<Map<String, Object>> allInteractions = userAnimalInteractionMapper.getAllInteractions();
            
            if (allInteractions.isEmpty()) {
                return getHotAnimals(limit);
            }
            
            // 2. 构建用户-动物交互矩阵
            Map<Long, Map<Long, Double>> userAnimalMatrix = buildInteractionMatrix(allInteractions);
            
            // 3. 获取目标用户的交互向量
            Map<Long, Double> targetUserVector = userAnimalMatrix.get(userId);
            
            if (targetUserVector == null || targetUserVector.isEmpty()) {
                return getContentBasedRecommendations(userId, limit);
            }
            
            // 4. 计算用户相似度
            List<UserSimilarity> similarities = calculateUserSimilarities(
                userId, targetUserVector, userAnimalMatrix);
            
            // 5. 基于相似用户预测推荐
            List<Long> recommendedAnimalIds = predictFromSimilarUsers(
                targetUserVector, similarities, userAnimalMatrix, limit);
            
            // 6. 查询动物详情
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
            log.error("协同过滤推荐失败", e);
            return getHotAnimals(limit);
        }
    }
    
    @Override
    public List<Animal> getContentBasedRecommendations(Long userId, int limit) {
        try {
            log.info("开始内容推荐，用户：{}", userId);
            
            // 1. 获取用户画像
            UserProfile profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId));
            
            if (profile == null) {
                return getHotAnimals(limit);
            }
            
            // 2. 获取用户动态偏好
            UserDynamicPreference preference = userDynamicPreferenceMapper.selectOne(
                new LambdaQueryWrapper<UserDynamicPreference>().eq(UserDynamicPreference::getUserId, userId));
            
            // 3. 获取所有待领养动物
            List<Animal> allAnimals = animalMapper.selectList(
                new LambdaQueryWrapper<Animal>().eq(Animal::getStatus, 0));
            
            // 4. 获取用户已交互的动物（排除）
            List<Long> interactedAnimalIds = userAnimalInteractionMapper.getUserInteractedAnimals(userId);
            
            // 5. 计算匹配度并排序
            List<AnimalMatchScore> matchScores = new ArrayList<>();
            
            for (Animal animal : allAnimals) {
                // 排除已交互的动物
                if (interactedAnimalIds.contains(animal.getId())) {
                    continue;
                }
                
                double matchScore = calculateContentMatchScore(animal, profile, preference);
                if (matchScore > 0) {
                    matchScores.add(new AnimalMatchScore(animal, matchScore));
                }
            }
            
            // 6. 按匹配度排序并返回Top-N
            return matchScores.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(limit)
                .map(ms -> ms.animal)
                .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("内容推荐失败", e);
            return getHotAnimals(limit);
        }
    }
    
    @Override
    public List<Animal> getSimilarAnimals(Long animalId, int limit) {
        try {
            log.info("查找相似动物，动物ID：{}", animalId);
            
            // 1. 获取目标动物
            Animal targetAnimal = animalMapper.selectById(animalId);
            if (targetAnimal == null) {
                return List.of();
            }
            
            // 2. 获取目标动物的扩展属性
            AnimalExtended targetExtended = animalExtendedMapper.selectOne(
                new LambdaQueryWrapper<AnimalExtended>().eq(AnimalExtended::getAnimalId, animalId));
            
            // 3. 获取所有待领养动物
            List<Animal> allAnimals = animalMapper.selectList(
                new LambdaQueryWrapper<Animal>()
                    .eq(Animal::getStatus, 0)
                    .ne(Animal::getId, animalId));
            
            // 4. 计算相似度
            List<AnimalMatchScore> similarities = new ArrayList<>();
            
            for (Animal animal : allAnimals) {
                AnimalExtended extended = animalExtendedMapper.selectOne(
                    new LambdaQueryWrapper<AnimalExtended>().eq(AnimalExtended::getAnimalId, animal.getId()));
                
                double similarity = calculateAnimalSimilarity(
                    targetAnimal, targetExtended, animal, extended);
                
                if (similarity > 0) {
                    similarities.add(new AnimalMatchScore(animal, similarity));
                }
            }
            
            // 5. 按相似度排序并返回Top-N
            return similarities.stream()
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(limit)
                .map(ms -> ms.animal)
                .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("查找相似动物失败", e);
            return List.of();
        }
    }
    
    @Override
    @Transactional
    public void recordInteraction(Long userId, Long animalId, Integer behaviorType, Long duration) {
        try {
            // 1. 查询或创建交互记录
            UserAnimalInteraction interaction = userAnimalInteractionMapper.selectOne(
                new LambdaQueryWrapper<UserAnimalInteraction>()
                    .eq(UserAnimalInteraction::getUserId, userId)
                    .eq(UserAnimalInteraction::getAnimalId, animalId));
            
            if (interaction == null) {
                interaction = new UserAnimalInteraction();
                interaction.setUserId(userId);
                interaction.setAnimalId(animalId);
                interaction.setViewCount(0);
                interaction.setTotalViewDuration(0L);
                interaction.setIsLiked(0);
                interaction.setIsFavorited(0);
                interaction.setIsCommented(0);
                interaction.setHasApplied(0);
            }
            
            // 2. 更新交互数据
            switch (behaviorType) {
                case 1: // 浏览
                    interaction.setViewCount(interaction.getViewCount() + 1);
                    if (duration != null && duration > 0) {
                        interaction.setTotalViewDuration(interaction.getTotalViewDuration() + duration);
                        interaction.setAvgViewDuration(
                            (double) interaction.getTotalViewDuration() / interaction.getViewCount());
                    }
                    break;
                case 2: // 点赞
                    interaction.setIsLiked(1);
                    break;
                case 3: // 收藏
                    interaction.setIsFavorited(1);
                    break;
                case 4: // 评论
                    interaction.setIsCommented(1);
                    break;
                case 5: // 申请领养
                    interaction.setHasApplied(1);
                    break;
            }
            
            // 3. 计算交互得分
            double score = calculateInteractionScore(interaction);
            interaction.setInteractionScore(score);
            interaction.setLastInteractionTime(LocalDateTime.now());
            
            // 4. 保存或更新
            if (interaction.getId() == null) {
                userAnimalInteractionMapper.insert(interaction);
            } else {
                userAnimalInteractionMapper.updateById(interaction);
            }
            
            // 5. 异步更新用户动态偏好
            updateUserDynamicPreference(userId);
            
            log.info("记录用户{}对动物{}的交互，类型：{}", userId, animalId, behaviorType);
            
        } catch (Exception e) {
            log.error("记录交互失败", e);
        }
    }
    
    @Override
    public void updateUserDynamicPreference(Long userId) {
        try {
            // 获取用户最近的交互记录
            List<UserAnimalInteraction> interactions = userAnimalInteractionMapper.selectList(
                new LambdaQueryWrapper<UserAnimalInteraction>()
                    .eq(UserAnimalInteraction::getUserId, userId)
                    .orderByDesc(UserAnimalInteraction::getLastInteractionTime)
                    .last("LIMIT 50"));
            
            if (interactions.isEmpty()) {
                return;
            }
            
            // 统计类别、品种、体型偏好
            Map<String, Integer> categoryViews = new HashMap<>();
            Map<String, Integer> breedViews = new HashMap<>();
            Map<String, Integer> sizeViews = new HashMap<>();
            
            for (UserAnimalInteraction interaction : interactions) {
                Animal animal = animalMapper.selectById(interaction.getAnimalId());
                if (animal == null) continue;
                
                // 统计类别
                AnimalCategory category = animalCategoryMapper.selectById(animal.getCategoryId());
                if (category != null) {
                    categoryViews.merge(category.getName(), 1, Integer::sum);
                }
                
                // 统计品种
                if (animal.getBreed() != null) {
                    breedViews.merge(animal.getBreed(), 1, Integer::sum);
                }
                
                // 统计体型
                AnimalExtended extended = animalExtendedMapper.selectOne(
                    new LambdaQueryWrapper<AnimalExtended>()
                        .eq(AnimalExtended::getAnimalId, animal.getId()));
                if (extended != null && extended.getSize() != null) {
                    sizeViews.merge(extended.getSize(), 1, Integer::sum);
                }
            }
            
            // 更新动态偏好
            UserDynamicPreference preference = userDynamicPreferenceMapper.selectOne(
                new LambdaQueryWrapper<UserDynamicPreference>()
                    .eq(UserDynamicPreference::getUserId, userId));
            
            if (preference == null) {
                preference = new UserDynamicPreference();
                preference.setUserId(userId);
            }
            
            // 转换为JSON
            preference.setCategoryViews(toJson(categoryViews));
            preference.setBreedViews(toJson(breedViews));
            preference.setSizeViews(toJson(sizeViews));
            preference.setLastUpdateTime(LocalDateTime.now());
            
            if (preference.getId() == null) {
                userDynamicPreferenceMapper.insert(preference);
            } else {
                userDynamicPreferenceMapper.updateById(preference);
            }
            
            log.info("更新用户{}的动态偏好", userId);
            
        } catch (Exception e) {
            log.error("更新用户动态偏好失败", e);
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 构建用户-动物交互矩阵
     */
    private Map<Long, Map<Long, Double>> buildInteractionMatrix(List<Map<String, Object>> interactions) {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();
        
        for (Map<String, Object> interaction : interactions) {
            Long userId = ((Number) interaction.get("user_id")).longValue();
            Long animalId = ((Number) interaction.get("animal_id")).longValue();
            Double score = ((Number) interaction.get("interaction_score")).doubleValue();
            
            // 应用时间衰减
            LocalDateTime lastTime = (LocalDateTime) interaction.get("last_interaction_time");
            if (lastTime != null) {
                long daysPassed = ChronoUnit.DAYS.between(lastTime, LocalDateTime.now());
                double timeDecay = FeatureVectorUtil.calculateTimeDecay(daysPassed);
                score = score * timeDecay;
            }
            
            matrix.computeIfAbsent(userId, k -> new HashMap<>()).put(animalId, score);
        }
        
        return matrix;
    }
    
    /**
     * 计算用户相似度
     */
    private List<UserSimilarity> calculateUserSimilarities(
            Long targetUserId,
            Map<Long, Double> targetVector,
            Map<Long, Map<Long, Double>> matrix) {
        
        List<UserSimilarity> similarities = new ArrayList<>();
        
        for (Map.Entry<Long, Map<Long, Double>> entry : matrix.entrySet()) {
            Long otherUserId = entry.getKey();
            
            if (otherUserId.equals(targetUserId)) {
                continue;
            }
            
            Map<Long, Double> otherVector = entry.getValue();
            
            // 计算加权Jaccard相似度
            double similarity = FeatureVectorUtil.weightedJaccardSimilarity(targetVector, otherVector);
            
            if (similarity > 0) {
                similarities.add(new UserSimilarity(otherUserId, similarity));
            }
        }
        
        // 按相似度降序排序
        similarities.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        
        return similarities;
    }
    
    /**
     * 基于相似用户预测推荐
     */
    private List<Long> predictFromSimilarUsers(
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
                Double score = entry.getValue();
                
                // 排除用户已交互的动物
                if (!targetVector.containsKey(animalId)) {
                    // 预测得分 = 相似度 * 交互得分
                    candidateScores.merge(animalId, userSim.similarity * score, Double::sum);
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
     * 计算内容匹配得分
     */
    private double calculateContentMatchScore(
            Animal animal,
            UserProfile profile,
            UserDynamicPreference preference) {
        
        double score = 0.0;
        
        // 1. 类别匹配（必要条件 + 权重0.4）
        List<String> preferredCategories = FeatureVectorUtil.parseJsonArray(profile.getPreferredCategories());
        AnimalCategory category = animalCategoryMapper.selectById(animal.getCategoryId());
        
        // 如果用户设置了偏好类别，但动物不在偏好类别中，直接返回0
        if (preferredCategories != null && !preferredCategories.isEmpty()) {
            if (category == null || !preferredCategories.contains(category.getName())) {
                return 0.0; // 不符合类别偏好，不推荐
            }
            score += 0.4; // 符合类别偏好，加分
        }
        
        // 2. 体型匹配（权重0.2）
        AnimalExtended extended = animalExtendedMapper.selectOne(
            new LambdaQueryWrapper<AnimalExtended>().eq(AnimalExtended::getAnimalId, animal.getId()));
        if (extended != null && extended.getSize() != null && 
            extended.getSize().equals(profile.getPreferredSize())) {
            score += 0.2;
        }
        
        // 3. 年龄匹配（权重0.15）
        if (animal.getAge() != null && profile.getPreferredAgeRange() != null) {
            String ageRange = getAgeRange(animal.getAge());
            if (ageRange.equals(profile.getPreferredAgeRange())) {
                score += 0.15;
            }
        }
        
        // 4. 性别匹配（权重0.1）
        if (profile.getPreferredGender() != null && !profile.getPreferredGender().equals("无偏好")) {
            String gender = animal.getGender() == 0 ? "雄性" : "雌性";
            if (gender.equals(profile.getPreferredGender())) {
                score += 0.1;
            }
        }
        
        // 5. 动态偏好匹配（权重0.15）
        if (preference != null) {
            Map<String, Integer> categoryViews = FeatureVectorUtil.parseJsonMap(preference.getCategoryViews());
            if (category != null && categoryViews.containsKey(category.getName())) {
                score += 0.15 * Math.min(categoryViews.get(category.getName()) / 100.0, 1.0);
            }
        }
        
        return Math.min(score, 1.0);
    }
    
    /**
     * 计算动物相似度
     */
    private double calculateAnimalSimilarity(
            Animal animal1, AnimalExtended extended1,
            Animal animal2, AnimalExtended extended2) {
        
        double similarity = 0.0;
        
        // 1. 类别相同（权重0.4）
        if (animal1.getCategoryId().equals(animal2.getCategoryId())) {
            similarity += 0.4;
        }
        
        // 2. 体型相同（权重0.2）
        if (extended1 != null && extended2 != null && 
            extended1.getSize() != null && extended1.getSize().equals(extended2.getSize())) {
            similarity += 0.2;
        }
        
        // 3. 年龄相近（权重0.15）
        if (animal1.getAge() != null && animal2.getAge() != null) {
            int ageDiff = Math.abs(animal1.getAge() - animal2.getAge());
            if (ageDiff <= 6) { // 6个月内
                similarity += 0.15 * (1.0 - ageDiff / 6.0);
            }
        }
        
        // 4. 性格相似（权重0.25）
        if (extended1 != null && extended2 != null) {
            List<String> personality1 = FeatureVectorUtil.parseJsonArray(extended1.getPersonality());
            List<String> personality2 = FeatureVectorUtil.parseJsonArray(extended2.getPersonality());
            
            long commonPersonality = personality1.stream()
                .filter(personality2::contains)
                .count();
            
            if (!personality1.isEmpty() && !personality2.isEmpty()) {
                similarity += 0.25 * (commonPersonality / (double) Math.max(personality1.size(), personality2.size()));
            }
        }
        
        return similarity;
    }
    
    /**
     * 融合推荐结果
     */
    private List<Animal> mergeRecommendations(
            List<Animal> cfAnimals,
            List<Animal> cbAnimals,
            List<Animal> hotAnimals,
            int limit) {
        
        Map<Long, Double> scoreMap = new HashMap<>();
        
        // 协同过滤结果（权重0.6）
        for (int i = 0; i < cfAnimals.size(); i++) {
            scoreMap.merge(cfAnimals.get(i).getId(), 
                WEIGHT_CF * (cfAnimals.size() - i), Double::sum);
        }
        
        // 内容推荐结果（权重0.3）
        for (int i = 0; i < cbAnimals.size(); i++) {
            scoreMap.merge(cbAnimals.get(i).getId(), 
                WEIGHT_CB * (cbAnimals.size() - i), Double::sum);
        }
        
        // 热度推荐结果（权重0.1）
        for (int i = 0; i < hotAnimals.size(); i++) {
            scoreMap.merge(hotAnimals.get(i).getId(), 
                WEIGHT_HOT * (hotAnimals.size() - i), Double::sum);
        }
        
        // 按综合得分排序
        List<Long> sortedIds = scoreMap.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        // 查询动物详情
        List<Animal> result = animalMapper.selectBatchIds(sortedIds);
        
        // 按排序顺序返回
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < sortedIds.size(); i++) {
            orderMap.put(sortedIds.get(i), i);
        }
        result.sort(Comparator.comparingInt(a -> orderMap.getOrDefault(a.getId(), Integer.MAX_VALUE)));
        
        return result;
    }
    
    /**
     * 获取热门动物
     */
    private List<Animal> getHotAnimals(int limit) {
        return animalMapper.selectList(
            new LambdaQueryWrapper<Animal>()
                .eq(Animal::getStatus, 0)
                .orderByDesc(animal -> 
                    animal.getViewCount() * 0.3 + 
                    animal.getLikeCount() * 0.7 + 
                    animal.getFavoriteCount() * 1.0)
                .last("LIMIT " + limit));
    }
    
    /**
     * 计算交互得分
     */
    private double calculateInteractionScore(UserAnimalInteraction interaction) {
        double score = 0.0;
        
        score += interaction.getViewCount() * 0.1;
        score += (interaction.getAvgViewDuration() != null ? interaction.getAvgViewDuration() / 60.0 : 0) * 0.2;
        score += interaction.getIsLiked() * 0.7;
        score += interaction.getIsFavorited() * 1.0;
        score += interaction.getIsCommented() * 0.5;
        score += interaction.getHasApplied() * 2.0;
        
        return score;
    }
    
    /**
     * 记录推荐日志
     */
    private void logRecommendations(Long userId, List<Animal> animals, String algorithmType) {
        try {
            for (Animal animal : animals) {
                RecommendationLog log = new RecommendationLog();
                log.setUserId(userId);
                log.setAnimalId(animal.getId());
                log.setAlgorithmType(algorithmType);
                log.setIsClicked(0);
                log.setIsAdopted(0);
                recommendationLogMapper.insert(log);
            }
        } catch (Exception e) {
            log.error("记录推荐日志失败", e);
        }
    }
    
    /**
     * 获取年龄段
     */
    private String getAgeRange(Integer ageInMonths) {
        if (ageInMonths < 12) {
            return "幼年";
        } else if (ageInMonths < 84) { // 7岁
            return "成年";
        } else {
            return "老年";
        }
    }
    
    /**
     * 转换为JSON
     */
    private String toJson(Map<String, Integer> map) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    // ==================== 内部类 ====================
    
    /**
     * 用户相似度
     */
    private static class UserSimilarity {
        Long userId;
        double similarity;
        
        UserSimilarity(Long userId, double similarity) {
            this.userId = userId;
            this.similarity = similarity;
        }
    }
    
    /**
     * 动物匹配得分
     */
    private static class AnimalMatchScore {
        Animal animal;
        double score;
        
        AnimalMatchScore(Animal animal, double score) {
            this.animal = animal;
            this.score = score;
        }
    }
}
