package com.stray.animal.rescue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 领养申请实体类
 */
@Data
@TableName("adoption_application")
public class AdoptionApplication {
    
    /**
     * 申请ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 申请理由
     */
    private String reason;
    
    /**
     * 申请状态 0-待审核 1-通过 2-拒绝 3-已联系
     */
    private Integer status;
    
    /**
     * 备注信息（审核意见）
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 