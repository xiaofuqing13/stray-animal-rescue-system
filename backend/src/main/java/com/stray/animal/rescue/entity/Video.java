package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 视频实体类
 */
@Data
@TableName("video")
public class Video {
    /**
     * 视频ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 视频标题
     */
    private String title;
    
    /**
     * 视频描述
     */
    private String description;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 封面URL
     */
    private String coverUrl;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 播放数
     */
    private Integer viewCount;
    
    /**
     * 状态：0-禁用 1-正常
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