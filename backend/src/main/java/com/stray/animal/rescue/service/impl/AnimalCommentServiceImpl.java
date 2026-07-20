package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.AnimalComment;
import com.stray.animal.rescue.mapper.AnimalCommentMapper;
import com.stray.animal.rescue.mapper.AnimalMapper;
import com.stray.animal.rescue.service.AnimalCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动物评论服务实现
 */
@Service
public class AnimalCommentServiceImpl implements AnimalCommentService {
    
    @Autowired
    private AnimalCommentMapper commentMapper;
    
    @Autowired
    private AnimalMapper animalMapper;
    
    @Override
    public List<AnimalComment> getCommentsByAnimalId(Long animalId) {
        return commentMapper.selectCommentsByAnimalId(animalId);
    }
    
    @Override
    @Transactional
    public void addComment(AnimalComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        
        // 更新动物评论数
        Animal animal = animalMapper.selectById(comment.getAnimalId());
        if (animal != null) {
            animal.setCommentCount((animal.getCommentCount() == null ? 0 : animal.getCommentCount()) + 1);
            animalMapper.updateById(animal);
        }
    }
    
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 查询评论
        AnimalComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 验证是否是评论作者
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        
        // 删除评论
        commentMapper.deleteById(commentId);
        
        // 更新动物评论数
        Animal animal = animalMapper.selectById(comment.getAnimalId());
        if (animal != null && animal.getCommentCount() != null && animal.getCommentCount() > 0) {
            animal.setCommentCount(animal.getCommentCount() - 1);
            animalMapper.updateById(animal);
        }
    }
}
