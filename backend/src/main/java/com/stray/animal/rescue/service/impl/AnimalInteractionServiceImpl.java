package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.AnimalFavorite;
import com.stray.animal.rescue.entity.AnimalLike;
import com.stray.animal.rescue.entity.UserBehavior;
import com.stray.animal.rescue.mapper.AnimalFavoriteMapper;
import com.stray.animal.rescue.mapper.AnimalLikeMapper;
import com.stray.animal.rescue.mapper.AnimalMapper;
import com.stray.animal.rescue.service.AnimalInteractionService;
import com.stray.animal.rescue.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 动物互动服务实现类
 */
@Service
public class AnimalInteractionServiceImpl implements AnimalInteractionService {
    
    @Autowired
    private AnimalLikeMapper animalLikeMapper;
    
    @Autowired
    private AnimalFavoriteMapper animalFavoriteMapper;
    
    @Autowired
    private AnimalMapper animalMapper;
    
    @Autowired
    private RecommendationService recommendationService;
    
    @Override
    @Transactional
    public void likeAnimal(Long userId, Long animalId) {
        // 检查是否已点赞
        LambdaQueryWrapper<AnimalLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimalLike::getUserId, userId)
               .eq(AnimalLike::getAnimalId, animalId);
        
        if (animalLikeMapper.selectCount(wrapper) > 0) {
            return; // 已点赞，不重复操作
        }
        
        // 添加点赞记录
        AnimalLike like = new AnimalLike();
        like.setUserId(userId);
        like.setAnimalId(animalId);
        like.setCreateTime(LocalDateTime.now());
        animalLikeMapper.insert(like);
        
        // 更新动物点赞数
        Animal animal = animalMapper.selectById(animalId);
        if (animal != null) {
            animal.setLikeCount(animal.getLikeCount() + 1);
            animalMapper.updateById(animal);
        }
        
        // 记录用户行为
        recommendationService.recordBehavior(userId, animalId, UserBehavior.BehaviorType.LIKE);
    }
    
    @Override
    @Transactional
    public void unlikeAnimal(Long userId, Long animalId) {
        LambdaQueryWrapper<AnimalLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimalLike::getUserId, userId)
               .eq(AnimalLike::getAnimalId, animalId);
        
        int deleted = animalLikeMapper.delete(wrapper);
        
        if (deleted > 0) {
            // 更新动物点赞数
            Animal animal = animalMapper.selectById(animalId);
            if (animal != null && animal.getLikeCount() > 0) {
                animal.setLikeCount(animal.getLikeCount() - 1);
                animalMapper.updateById(animal);
            }
        }
    }
    
    @Override
    @Transactional
    public void favoriteAnimal(Long userId, Long animalId) {
        // 检查是否已收藏
        LambdaQueryWrapper<AnimalFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimalFavorite::getUserId, userId)
               .eq(AnimalFavorite::getAnimalId, animalId);
        
        if (animalFavoriteMapper.selectCount(wrapper) > 0) {
            return; // 已收藏，不重复操作
        }
        
        // 添加收藏记录
        AnimalFavorite favorite = new AnimalFavorite();
        favorite.setUserId(userId);
        favorite.setAnimalId(animalId);
        favorite.setCreateTime(LocalDateTime.now());
        animalFavoriteMapper.insert(favorite);
        
        // 更新动物收藏数
        Animal animal = animalMapper.selectById(animalId);
        if (animal != null) {
            animal.setFavoriteCount(animal.getFavoriteCount() + 1);
            animalMapper.updateById(animal);
        }
        
        // 记录用户行为
        recommendationService.recordBehavior(userId, animalId, UserBehavior.BehaviorType.FAVORITE);
    }
    
    @Override
    @Transactional
    public void unfavoriteAnimal(Long userId, Long animalId) {
        LambdaQueryWrapper<AnimalFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimalFavorite::getUserId, userId)
               .eq(AnimalFavorite::getAnimalId, animalId);
        
        int deleted = animalFavoriteMapper.delete(wrapper);
        
        if (deleted > 0) {
            // 更新动物收藏数
            Animal animal = animalMapper.selectById(animalId);
            if (animal != null && animal.getFavoriteCount() > 0) {
                animal.setFavoriteCount(animal.getFavoriteCount() - 1);
                animalMapper.updateById(animal);
            }
        }
    }
    
    @Override
    public boolean isLiked(Long userId, Long animalId) {
        LambdaQueryWrapper<AnimalLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimalLike::getUserId, userId)
               .eq(AnimalLike::getAnimalId, animalId);
        return animalLikeMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean isFavorited(Long userId, Long animalId) {
        LambdaQueryWrapper<AnimalFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnimalFavorite::getUserId, userId)
               .eq(AnimalFavorite::getAnimalId, animalId);
        return animalFavoriteMapper.selectCount(wrapper) > 0;
    }
}
