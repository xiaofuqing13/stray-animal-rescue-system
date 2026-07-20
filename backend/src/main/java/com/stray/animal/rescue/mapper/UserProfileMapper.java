package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户画像Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
