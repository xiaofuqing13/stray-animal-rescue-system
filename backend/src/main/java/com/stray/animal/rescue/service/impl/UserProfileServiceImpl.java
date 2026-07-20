package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stray.animal.rescue.entity.UserProfile;
import com.stray.animal.rescue.mapper.UserProfileMapper;
import com.stray.animal.rescue.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户画像服务实现
 */
@Slf4j
@Service
public class UserProfileServiceImpl implements UserProfileService {
    
    @Autowired
    private UserProfileMapper userProfileMapper;
    
    @Override
    public void saveOrUpdateProfile(UserProfile profile) {
        UserProfile existing = userProfileMapper.selectOne(
            new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, profile.getUserId()));
        
        if (existing == null) {
            userProfileMapper.insert(profile);
            log.info("创建用户画像，用户ID：{}", profile.getUserId());
        } else {
            profile.setId(existing.getId());
            userProfileMapper.updateById(profile);
            log.info("更新用户画像，用户ID：{}", profile.getUserId());
        }
    }
    
    @Override
    public UserProfile getUserProfile(Long userId) {
        UserProfile profile = userProfileMapper.selectOne(
            new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId));
        
        if (profile == null) {
            // 如果不存在，初始化默认画像
            initDefaultProfile(userId);
            profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>()
                    .eq(UserProfile::getUserId, userId));
        }
        
        return profile;
    }
    
    @Override
    public void initDefaultProfile(Long userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setPreferredCategories("[\"猫\",\"狗\"]");
        profile.setPreferredSize("中型");
        profile.setPreferredAgeRange("成年");
        profile.setPreferredGender("无偏好");
        profile.setLivingEnvironment("公寓");
        profile.setExperience("有经验");
        profile.setPreferredLocation("北京市朝阳区");
        
        userProfileMapper.insert(profile);
        log.info("初始化默认用户画像，用户ID：{}", userId);
    }
}
