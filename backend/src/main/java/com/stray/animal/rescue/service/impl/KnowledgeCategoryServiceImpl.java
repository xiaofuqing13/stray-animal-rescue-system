package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.KnowledgeCategoryDTO;
import com.stray.animal.rescue.entity.KnowledgeArticle;
import com.stray.animal.rescue.entity.KnowledgeCategory;
import com.stray.animal.rescue.mapper.KnowledgeArticleMapper;
import com.stray.animal.rescue.mapper.KnowledgeCategoryMapper;
import com.stray.animal.rescue.service.KnowledgeCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识分类服务实现类
 */
@Service
public class KnowledgeCategoryServiceImpl extends ServiceImpl<KnowledgeCategoryMapper, KnowledgeCategory> implements KnowledgeCategoryService {

    private final KnowledgeArticleMapper knowledgeArticleMapper;

    public KnowledgeCategoryServiceImpl(KnowledgeArticleMapper knowledgeArticleMapper) {
        this.knowledgeArticleMapper = knowledgeArticleMapper;
    }

    @Override
    public Page<KnowledgeCategory> getCategoryPage(int pageNum, int pageSize, String keyword) {
        Page<KnowledgeCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<KnowledgeCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(KnowledgeCategory::getName, keyword)
                        .or()
                        .like(KnowledgeCategory::getDescription, keyword);
        }
        
        queryWrapper.orderByDesc(KnowledgeCategory::getCreateTime);
        
        return page(page, queryWrapper);
    }

    @Override
    public List<KnowledgeCategory> getAllCategories() {
        LambdaQueryWrapper<KnowledgeCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(KnowledgeCategory::getName);
        return list(queryWrapper);
    }

    @Override
    public KnowledgeCategory getCategoryById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(KnowledgeCategoryDTO categoryDTO) {
        // 检查分类名称是否存在
        LambdaQueryWrapper<KnowledgeCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeCategory::getName, categoryDTO.getName());
        long count = count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("分类名称已存在");
        }
        
        KnowledgeCategory category = new KnowledgeCategory();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(KnowledgeCategoryDTO categoryDTO) {
        // 检查分类是否存在
        KnowledgeCategory existCategory = getById(categoryDTO.getId());
        if (existCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查分类名称是否重复
        LambdaQueryWrapper<KnowledgeCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeCategory::getName, categoryDTO.getName())
                    .ne(KnowledgeCategory::getId, categoryDTO.getId());
        long count = count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("分类名称已存在");
        }
        
        KnowledgeCategory category = new KnowledgeCategory();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateTime(LocalDateTime.now());
        
        updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        // 检查分类是否存在
        KnowledgeCategory category = getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查是否有文章使用该分类
        LambdaQueryWrapper<KnowledgeArticle> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(KnowledgeArticle::getCategoryId, id);
        long count = knowledgeArticleMapper.selectCount(articleWrapper);
        if (count > 0) {
            throw new RuntimeException("该分类下存在文章，不能删除");
        }
        
        // 删除分类
        removeById(id);
    }
} 