package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 领养申请数据传输对象
 */
@Data
public class AdoptionApplicationDTO {
    
    /**
     * 申请ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 用户名称（显示用）
     */
    private String userName;
    
    /**
     * 用户联系电话（显示用）
     */
    private String userPhone;
    
    /**
     * 用户邮箱（显示用）
     */
    private String userEmail;
    
    /**
     * 用户地址（显示用）
     */
    private String userAddress;
    
    /**
     * 动物ID
     */
    @NotNull(message = "动物ID不能为空")
    private Long animalId;
    
    /**
     * 动物名称（显示用）
     */
    private String animalName;
    
    /**
     * 动物类别（显示用）
     */
    private String animalCategory;
    
    /**
     * 动物图片（显示用）
     */
    private String animalImage;
    
    /**
     * 申请理由
     */
    @NotBlank(message = "申请理由不能为空")
    @Size(min = 10, max = 500, message = "申请理由长度应在10-500个字符之间")
    private String reason;
    
    /**
     * 申请状态 0-待审核 1-通过 2-拒绝
     */
    private Integer status;
    
    /**
     * 备注信息（审核意见）
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private String createTime;
} 