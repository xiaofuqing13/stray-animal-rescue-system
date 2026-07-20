package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.dto.KnowledgeArticleDTO;
import com.stray.animal.rescue.entity.KnowledgeArticle;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 知识文章服务接口
 */
public interface KnowledgeArticleService {
    
    /**
     * 分页查询知识文章
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @return 分页结果
     */
    Page<KnowledgeArticle> getArticlePage(int pageNum, int pageSize, String keyword, Long categoryId);
    
    /**
     * 根据ID获取知识文章
     * @param id 文章ID
     * @return 文章信息
     */
    KnowledgeArticle getArticleById(Long id);
    
    /**
     * 添加知识文章
     * @param articleDTO 文章信息
     */
    void addArticle(KnowledgeArticleDTO articleDTO);
    
    /**
     * 更新知识文章
     * @param articleDTO 文章信息
     */
    void updateArticle(KnowledgeArticleDTO articleDTO);
    
    /**
     * 删除知识文章
     * @param id 文章ID
     */
    void deleteArticle(Long id);
    
    /**
     * 上传文章封面图片
     * @param file 图片文件
     * @return 图片URL
     * @throws IOException IO异常
     */
    String uploadImage(MultipartFile file) throws IOException;
    
    /**
     * 上传视频
     * @param file 视频文件
     * @return 视频URL
     * @throws IOException IO异常
     */
    String uploadVideo(MultipartFile file) throws IOException;
    
    /**
     * 上传视频封面
     * @param file 图片文件
     * @return 封面URL
     * @throws IOException IO异常
     */
    String uploadVideoCover(MultipartFile file) throws IOException;
} 