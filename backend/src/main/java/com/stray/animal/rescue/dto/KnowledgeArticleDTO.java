package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 知识文章数据传输对象
 */
@Data
public class KnowledgeArticleDTO {
    
    /**
     * 文章ID
     */
    private Long id;
    
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;
    
    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
    
    /**
     * 封面图片
     */
    private String imageUrl;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 视频封面URL
     */
    private String videoCoverUrl;
    
    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    /**
     * 分类名称（显示用）
     */
    private String categoryName;
    
    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 浏览数
     */
    private Integer viewCount;
    
    /**
     * 创建时间
     */
    private String createTime;
} 