package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 动物实体类
 */
@Data
@TableName("animal")
public class Animal {
    
    /**
     * 动物ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 名称
     */
    private String name;
    
    /**
     * 类别ID
     */
    private Long categoryId;
    
    /**
     * 品种
     */
    private String breed;
    
    /**
     * 性别 0-雄性 1-雌性
     */
    private Integer gender;
    
    /**
     * 年龄(月)
     */
    private Integer age;
    
    /**
     * 体重(kg)
     */
    private BigDecimal weight;
    
    /**
     * 健康状态
     */
    private String healthStatus;
    
    /**
     * 特征
     */
    private String features;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 状态 0-待领养 1-已领养 2-救助中
     */
    private Integer status;
    
    /**
     * 浏览数
     */
    private Integer viewCount = 0;
    
    /**
     * 点赞数
     */
    private Integer likeCount = 0;
    
    /**
     * 收藏数
     */
    private Integer favoriteCount = 0;
    
    /**
     * 评论数
     */
    private Integer commentCount = 0;
    
    /**
     * 救助时间
     */
    private LocalDateTime rescueTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 非持久化属性，用于存储临时数据
     */
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
} 