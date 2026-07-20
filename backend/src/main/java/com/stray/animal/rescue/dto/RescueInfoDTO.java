package com.stray.animal.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 救助信息数据传输对象
 */
@Data
public class RescueInfoDTO {
    
    /**
     * 救助ID
     */
    private Long id;
    
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
    
    /**
     * 位置
     */
    @NotBlank(message = "位置不能为空")
    private String location;
    
    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 发布用户ID
     */
    private Long userId;
    
    /**
     * 状态 0-待救助 1-救助中 2-已解决
     */
    @NotNull(message = "请选择状态")
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 