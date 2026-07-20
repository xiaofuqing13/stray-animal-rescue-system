package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.UserAnimalInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户动物交互Mapper
 */
@Mapper
public interface UserAnimalInteractionMapper extends BaseMapper<UserAnimalInteraction> {
    
    /**
     * 获取所有用户的交互数据（用于协同过滤）
     */
    @Select("SELECT user_id, animal_id, interaction_score, last_interaction_time " +
            "FROM user_animal_interaction " +
            "WHERE interaction_score > 0 " +
            "ORDER BY last_interaction_time DESC")
    List<Map<String, Object>> getAllInteractions();
    
    /**
     * 获取用户的交互动物列表
     */
    @Select("SELECT animal_id FROM user_animal_interaction " +
            "WHERE user_id = #{userId} AND interaction_score > 0")
    List<Long> getUserInteractedAnimals(@Param("userId") Long userId);
}
