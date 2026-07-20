package com.stray.animal.rescue.service;

import com.stray.animal.rescue.entity.UserProfile;

/**
 * 用户画像服务接口
 */
public interface UserProfileService {
    
    /**
     * 保存或更新用户画像
     */
    void saveOrUpdateProfile(UserProfile profile);
    
    /**
     * 获取用户画像
     */
    UserProfile getUserProfile(Long userId);
    
    /**
     * 初始化默认画像
     */
    void initDefaultProfile(Long userId);
}
