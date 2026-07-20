package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识文章实体类
 */
@Data
@TableName("knowledge_article")
public class KnowledgeArticle {
    
    /**
     * 文章ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
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
    private Long categoryId;
    
    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
    
    /**
     * 浏览数
     */
    private Integer viewCount;
    
    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 