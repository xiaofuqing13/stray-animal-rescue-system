package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.KnowledgeArticleDTO;
import com.stray.animal.rescue.entity.KnowledgeArticle;
import com.stray.animal.rescue.entity.KnowledgeCategory;
import com.stray.animal.rescue.mapper.KnowledgeArticleMapper;
import com.stray.animal.rescue.mapper.KnowledgeCategoryMapper;
import com.stray.animal.rescue.service.KnowledgeArticleService;
import com.stray.animal.rescue.util.FileUploadUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 知识文章服务实现类
 */
@Service
public class KnowledgeArticleServiceImpl extends ServiceImpl<KnowledgeArticleMapper, KnowledgeArticle> implements KnowledgeArticleService {

    private final KnowledgeCategoryMapper knowledgeCategoryMapper;
    private final FileUploadUtil fileUploadUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public KnowledgeArticleServiceImpl(KnowledgeCategoryMapper knowledgeCategoryMapper, FileUploadUtil fileUploadUtil) {
        this.knowledgeCategoryMapper = knowledgeCategoryMapper;
        this.fileUploadUtil = fileUploadUtil;
    }

    @Override
    public Page<KnowledgeArticle> getArticlePage(int pageNum, int pageSize, String keyword, Long categoryId) {
        Page<KnowledgeArticle> page = new Page<>(pageNum, pageSize);
        IPage<KnowledgeArticle> iPage = baseMapper.pageArticles(page, keyword, categoryId);
        
        // 获取文章列表并处理分类名称
        List<KnowledgeArticle> records = iPage.getRecords();
        for (KnowledgeArticle article : records) {
            if (article.getCategoryId() != null && (article.getCategoryName() == null || article.getCategoryName().isEmpty())) {
                KnowledgeCategory category = knowledgeCategoryMapper.selectById(article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
            }
        }
        
        page.setRecords(records);
        page.setTotal(iPage.getTotal());
        return page;
    }

    @Override
    public KnowledgeArticle getArticleById(Long id) {
        KnowledgeArticle article = getById(id);
        if (article != null) {
            // 获取分类名称
            KnowledgeCategory category = knowledgeCategoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addArticle(KnowledgeArticleDTO articleDTO) {
        // 检查分类是否存在
        KnowledgeCategory category = knowledgeCategoryMapper.selectById(articleDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        KnowledgeArticle article = new KnowledgeArticle();
        BeanUtils.copyProperties(articleDTO, article);
        
        // 设置默认状态
        if (article.getStatus() == null) {
            article.setStatus(1);
        }
        
        // 设置默认浏览数
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        
        save(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(KnowledgeArticleDTO articleDTO) {
        // 检查文章是否存在
        KnowledgeArticle existArticle = getById(articleDTO.getId());
        if (existArticle == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 检查分类是否存在
        KnowledgeCategory category = knowledgeCategoryMapper.selectById(articleDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        KnowledgeArticle article = new KnowledgeArticle();
        BeanUtils.copyProperties(articleDTO, article);
        article.setUpdateTime(LocalDateTime.now());
        
        updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        // 检查文章是否存在
        KnowledgeArticle article = getById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        
        // 删除文章
        removeById(id);
        
        // 删除相关的文件（图片和视频）
        if (StringUtils.hasText(article.getImageUrl())) {
            fileUploadUtil.deleteFile(article.getImageUrl());
        }
        
        if (StringUtils.hasText(article.getVideoUrl())) {
            fileUploadUtil.deleteFile(article.getVideoUrl());
        }
        
        if (StringUtils.hasText(article.getVideoCoverUrl())) {
            fileUploadUtil.deleteFile(article.getVideoCoverUrl());
        }
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        return fileUploadUtil.uploadFile(file, "knowledge/images");
    }

    @Override
    public String uploadVideo(MultipartFile file) throws IOException {
        return fileUploadUtil.uploadFile(file, "knowledge/videos");
    }

    @Override
    public String uploadVideoCover(MultipartFile file) throws IOException {
        return fileUploadUtil.uploadFile(file, "knowledge/covers");
    }
} 