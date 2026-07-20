package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 轮播图实体
 */
@Data
@TableName("carousel")
public class Carousel {
    
    /**
     * 轮播图ID
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 排序
     */
    private Integer sort;
    
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