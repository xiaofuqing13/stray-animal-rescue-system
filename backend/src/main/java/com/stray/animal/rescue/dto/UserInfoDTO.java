package com.stray.animal.rescue.dto;

import lombok.Data;

/**
 * 用户信息DTO
 */
@Data
public class UserInfoDTO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 角色 0-普通用户 1-管理员
     */
    private Integer role;
} 