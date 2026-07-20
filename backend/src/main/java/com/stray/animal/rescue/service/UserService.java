package com.stray.animal.rescue.service;

import com.stray.animal.rescue.dto.UserInfoDTO;
import com.stray.animal.rescue.dto.UserLoginDTO;
import com.stray.animal.rescue.dto.UserRegisterDTO;
import com.stray.animal.rescue.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @param avatar 头像文件（可选）
     * @return 用户ID
     */
    Long register(UserRegisterDTO registerDTO, MultipartFile avatar) throws IOException;
    
    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录成功返回包含token和用户信息的Map，失败返回null
     */
    Map<String, Object> login(UserLoginDTO loginDTO);
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
    
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(Long id);
    
    /**
     * 根据ID查询用户信息（脱敏）
     *
     * @param id 用户ID
     * @return 用户信息DTO
     */
    UserInfoDTO getUserInfo(Long id);
    
    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param name 姓名
     * @param phone 电话
     * @param email 邮箱
     * @param address 地址
     * @param avatar 头像
     * @return 更新后的用户信息
     */
    UserInfoDTO updateUserInfo(Long userId, String name, String phone, String email, String address, MultipartFile avatar) throws IOException;
    
    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    long count();
    
    /**
     * 获取用户收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    java.util.List<?> getUserFavorites(Long userId);
} 