package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 领养申请审核数据传输对象
 */
@Data
public class AdoptionAuditDTO {
    
    /**
     * 申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long id;
    
    /**
     * 审核结果: 1-通过 2-拒绝
     */
    @NotNull(message = "审核结果不能为空")
    private Integer status;
    
    /**
     * 审核意见
     */
    @Size(max = 200, message = "审核意见不能超过200个字符")
    private String remark;
} 