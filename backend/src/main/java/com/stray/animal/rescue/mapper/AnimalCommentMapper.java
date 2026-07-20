package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.AnimalComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 动物评论Mapper
 */
@Mapper
public interface AnimalCommentMapper extends BaseMapper<AnimalComment> {
    
    /**
     * 获取动物评论列表（包含用户信息）
     */
    @Select("SELECT ac.*, u.username, u.avatar " +
            "FROM animal_comment ac " +
            "LEFT JOIN user u ON ac.user_id = u.id " +
            "WHERE ac.animal_id = #{animalId} " +
            "ORDER BY ac.create_time DESC")
    List<AnimalComment> selectCommentsByAnimalId(Long animalId);
}
