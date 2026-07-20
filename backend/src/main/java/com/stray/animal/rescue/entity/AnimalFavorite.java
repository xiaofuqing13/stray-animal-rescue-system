package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动物收藏实体类
 */
@Data
@TableName("animal_favorite")
public class AnimalFavorite {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 动物ID
     */
    private Long animalId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
