package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户行为记录Mapper
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    
    /**
     * 获取用户的所有行为记录（用于协同过滤）
     */
    @Select("SELECT user_id, animal_id, behavior_type FROM user_behavior WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)")
    List<Map<String, Object>> getAllRecentBehaviors();
    
    /**
     * 获取指定用户的行为记录
     */
    @Select("SELECT animal_id, behavior_type FROM user_behavior WHERE user_id = #{userId} AND create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)")
    List<Map<String, Object>> getUserBehaviors(@Param("userId") Long userId);
}
