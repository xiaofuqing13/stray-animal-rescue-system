package com.stray.animal.rescue.dto;

import lombok.Data;

/**
 * 管理员更新用户信息DTO
 * 所有字段可选，仅传入需要更新的字段
 */
@Data
public class UserUpdateDTO {

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
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 头像相对路径（如 /avatar/xxx.jpg），由前端先调上传接口拿到后再传
     */
    private String avatar;
}
