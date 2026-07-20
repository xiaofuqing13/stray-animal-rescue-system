package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.UserInfoDTO;
import com.stray.animal.rescue.dto.UserLoginDTO;
import com.stray.animal.rescue.dto.UserRegisterDTO;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.mapper.UserMapper;
import com.stray.animal.rescue.mapper.AnimalFavoriteMapper;
import com.stray.animal.rescue.service.UserService;
import com.stray.animal.rescue.util.FileUploadUtil;
import com.stray.animal.rescue.util.JwtUtil;
import com.stray.animal.rescue.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private FileUploadUtil fileUploadUtil;
    
    @Resource
    private AnimalFavoriteMapper animalFavoriteMapper;

    /**
     * 用户注册
     */
    @Override
    public Long register(UserRegisterDTO registerDTO, MultipartFile avatar) throws IOException {
        // 判断用户名是否已存在
        User existUser = this.getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        
        // 使用明文密码，不再加密
        user.setPassword(registerDTO.getPassword());
        
        // 设置默认角色（普通用户）
        user.setRole(0);
        
        // 设置状态为正常
        user.setStatus(1);
        
        // 处理头像上传
        if (avatar != null && !avatar.isEmpty()) {
            String avatarPath = fileUploadUtil.uploadFile(avatar, "avatar");
            user.setAvatar(avatarPath);
        } else {
            // 设置默认头像
            user.setAvatar("/avatar/default.png");
        }
        
        // 保存用户
        this.save(user);
        
        return user.getId();
    }

    /**
     * 用户登录
     */
    @Override
    public Map<String, Object> login(UserLoginDTO loginDTO) {
        // 根据用户名查询用户
        User user = this.getByUsername(loginDTO.getUsername());
        
        // 用户不存在
        if (user == null) {
            return null;
        }
        
        // 状态异常
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 直接比较明文密码
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            return null;
        }
        
        // 生成token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 获取用户信息（脱敏）
        UserInfoDTO userInfoDTO = getUserInfo(user.getId());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userInfoDTO);
        
        return result;
    }

    /**
     * 根据用户名查询用户
     */
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return this.getOne(queryWrapper);
    }

    /**
     * 根据ID查询用户
     */
    @Override
    public User getById(Long id) {
        return super.getById(id);
    }

    /**
     * 根据ID查询用户信息（脱敏）
     */
    @Override
    public UserInfoDTO getUserInfo(Long id) {
        User user = this.getById(id);
        if (user == null) {
            return null;
        }
        
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user, userInfoDTO);
        
        // 处理头像URL
        if (userInfoDTO.getAvatar() != null && !userInfoDTO.getAvatar().isEmpty()) {
            userInfoDTO.setAvatar(fileUploadUtil.getFileUrl(userInfoDTO.getAvatar()));
        }
        
        return userInfoDTO;
    }
    
    /**
     * 更新用户信息
     */
    @Override
    public UserInfoDTO updateUserInfo(Long userId, String name, String phone, String email, String address, MultipartFile avatar) throws IOException {
        // 获取用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户信息
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }
        
        if (phone != null) {
            user.setPhone(phone);
        }
        
        if (email != null) {
            user.setEmail(email);
        }
        
        if (address != null) {
            user.setAddress(address);
        }
        
        // 处理头像上传
        if (avatar != null && !avatar.isEmpty()) {
            String avatarPath = fileUploadUtil.uploadFile(avatar, "avatar");
            user.setAvatar(avatarPath);
        }
        
        // 保存更新
        this.updateById(user);
        
        // 返回更新后的用户信息
        return getUserInfo(userId);
    }
    
    /**
     * 修改密码
     */
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 获取用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证原密码
        if (!user.getPassword().equals(oldPassword)) {
            return false;
        }
        
        // 更新密码
        user.setPassword(newPassword);
        
        // 保存更新
        return this.updateById(user);
    }

    @Override
    public long count() {
        return baseMapper.selectCount(null);
    }
    
    @Override
    public java.util.List<?> getUserFavorites(Long userId) {
        // 使用AnimalFavoriteMapper查询收藏列表
        return animalFavoriteMapper.getUserFavoritesWithAnimal(userId);
    }
} 