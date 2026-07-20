package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.KnowledgeArticle;
import com.stray.animal.rescue.entity.KnowledgeCategory;
import com.stray.animal.rescue.service.KnowledgeArticleService;
import com.stray.animal.rescue.service.KnowledgeCategoryService;
import com.stray.animal.rescue.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端知识相关接口
 */
@Slf4j
@Tag(name = "知识管理接口", description = "用户端知识分类和文章接口")
@RestController
@RequestMapping("/user/knowledge")
public class UserKnowledgeController {

    @Autowired
    private KnowledgeCategoryService knowledgeCategoryService;

    @Autowired
    private KnowledgeArticleService knowledgeArticleService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 获取所有知识分类
     */
    @Operation(summary = "获取所有知识分类")
    @GetMapping("/category/all")
    public Result<List<KnowledgeCategory>> getAllCategories() {
        List<KnowledgeCategory> categories = knowledgeCategoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 分页查询知识文章（只返回状态正常的文章）
     */
    @Operation(summary = "分页查询知识文章")
    @GetMapping("/article/page")
    public Result<Page<KnowledgeArticle>> getArticlePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        
        Page<KnowledgeArticle> result = knowledgeArticleService.getArticlePage(pageNum, pageSize, keyword, categoryId);
        
        // 处理图片URL
        result.getRecords().forEach(article -> {
            // 在这里无法直接设置额外字段categoryName，因为KnowledgeArticle没有该字段
            // 在实际前端展示时，可以使用关联查询或者返回DTO对象
            
            // 处理图片URL
            if (StringUtils.hasText(article.getImageUrl())) {
                article.setImageUrl(fileUploadUtil.getFileUrl(article.getImageUrl()));
            }
            
            if (StringUtils.hasText(article.getVideoUrl())) {
                article.setVideoUrl(fileUploadUtil.getFileUrl(article.getVideoUrl()));
            }
            
            if (StringUtils.hasText(article.getVideoCoverUrl())) {
                article.setVideoCoverUrl(fileUploadUtil.getFileUrl(article.getVideoCoverUrl()));
            }
        });
        
        return Result.success(result);
    }

    /**
     * 获取文章详情并更新浏览量
     */
    @Operation(summary = "获取文章详情")
    @GetMapping("/article/{id}")
    public Result<Map<String, Object>> getArticleDetail(@PathVariable Long id) {
        // 获取文章详情
        KnowledgeArticle article = knowledgeArticleService.getArticleById(id);
        if (article == null || article.getStatus() != 1) {
            return Result.error("文章不存在或已被删除");
        }
        
        // 获取分类名称
        KnowledgeCategory category = knowledgeCategoryService.getCategoryById(article.getCategoryId());
        // 创建一个Map来存储额外信息
        Map<String, Object> articleMap = new HashMap<>();
        articleMap.put("id", article.getId());
        articleMap.put("title", article.getTitle());
        articleMap.put("content", article.getContent());
        articleMap.put("imageUrl", article.getImageUrl());
        articleMap.put("videoUrl", article.getVideoUrl());
        articleMap.put("videoCoverUrl", article.getVideoCoverUrl());
        articleMap.put("categoryId", article.getCategoryId());
        articleMap.put("viewCount", article.getViewCount());
        articleMap.put("status", article.getStatus());
        articleMap.put("createTime", article.getCreateTime());
        articleMap.put("updateTime", article.getUpdateTime());
        
        if (category != null) {
            articleMap.put("categoryName", category.getName());
        }
        
        // 处理图片URL
        if (StringUtils.hasText(article.getImageUrl())) {
            articleMap.put("imageUrl", fileUploadUtil.getFileUrl(article.getImageUrl()));
        }
        
        if (StringUtils.hasText(article.getVideoUrl())) {
            articleMap.put("videoUrl", fileUploadUtil.getFileUrl(article.getVideoUrl()));
        }
        
        if (StringUtils.hasText(article.getVideoCoverUrl())) {
            articleMap.put("videoCoverUrl", fileUploadUtil.getFileUrl(article.getVideoCoverUrl()));
        }
        
        // 更新浏览量
        article.setViewCount(article.getViewCount() + 1);
        knowledgeArticleService.updateArticle(convertToDTO(article));
        
        // 获取上一篇和下一篇文章
        KnowledgeArticle prevArticle = getPrevArticle(id, article.getCategoryId());
        KnowledgeArticle nextArticle = getNextArticle(id, article.getCategoryId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("article", articleMap);
        result.put("prevArticle", prevArticle);
        result.put("nextArticle", nextArticle);
        
        return Result.success(result);
    }

    /**
     * 获取相关文章（同分类的其他文章）
     */
    @Operation(summary = "获取相关文章")
    @GetMapping("/article/related")
    public Result<List<KnowledgeArticle>> getRelatedArticles(
            @RequestParam Long categoryId,
            @RequestParam Long articleId,
            @RequestParam(defaultValue = "4") Integer limit) {
        
        // 手动查询相关文章，状态为1、相同分类但不是当前文章
        List<KnowledgeArticle> articles = new ArrayList<>();
        
        // 查询最多limit条记录
        for (int i = 0; i < limit; i++) {
            KnowledgeArticle article = knowledgeArticleService.getArticleById(articleId + i + 1);
            if (article != null && article.getStatus() == 1 
                && article.getCategoryId().equals(categoryId) 
                && !article.getId().equals(articleId)) {
                // 处理图片/视频URL（与 page 接口保持一致）
                if (StringUtils.hasText(article.getImageUrl())) {
                    article.setImageUrl(fileUploadUtil.getFileUrl(article.getImageUrl()));
                }
                if (StringUtils.hasText(article.getVideoUrl())) {
                    article.setVideoUrl(fileUploadUtil.getFileUrl(article.getVideoUrl()));
                }
                if (StringUtils.hasText(article.getVideoCoverUrl())) {
                    article.setVideoCoverUrl(fileUploadUtil.getFileUrl(article.getVideoCoverUrl()));
                }
                articles.add(article);
            }
            
            // 如果已经找到足够的文章，就退出循环
            if (articles.size() >= limit) {
                break;
            }
        }
        
        return Result.success(articles);
    }

    /**
     * 获取上一篇文章
     */
    private KnowledgeArticle getPrevArticle(Long currentId, Long categoryId) {
        // 查找当前文章之前的文章
        for (long i = currentId - 1; i > 0; i--) {
            KnowledgeArticle article = knowledgeArticleService.getArticleById(i);
            if (article != null && article.getStatus() == 1 
                && article.getCategoryId().equals(categoryId)) {
                // 只返回ID和标题
                KnowledgeArticle result = new KnowledgeArticle();
                result.setId(article.getId());
                result.setTitle(article.getTitle());
                return result;
            }
        }
        return null;
    }

    /**
     * 获取下一篇文章
     */
    private KnowledgeArticle getNextArticle(Long currentId, Long categoryId) {
        // 查找当前文章之后的文章
        for (long i = currentId + 1; i < currentId + 100; i++) {
            KnowledgeArticle article = knowledgeArticleService.getArticleById(i);
            if (article != null && article.getStatus() == 1 
                && article.getCategoryId().equals(categoryId)) {
                // 只返回ID和标题
                KnowledgeArticle result = new KnowledgeArticle();
                result.setId(article.getId());
                result.setTitle(article.getTitle());
                return result;
            }
        }
        return null;
    }
    
    /**
     * 将KnowledgeArticle转换为KnowledgeArticleDTO
     */
    private com.stray.animal.rescue.dto.KnowledgeArticleDTO convertToDTO(KnowledgeArticle article) {
        com.stray.animal.rescue.dto.KnowledgeArticleDTO dto = new com.stray.animal.rescue.dto.KnowledgeArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setImageUrl(article.getImageUrl());
        dto.setVideoUrl(article.getVideoUrl());
        dto.setVideoCoverUrl(article.getVideoCoverUrl());
        dto.setCategoryId(article.getCategoryId());
        dto.setViewCount(article.getViewCount());
        dto.setStatus(article.getStatus());
        return dto;
    }
} 