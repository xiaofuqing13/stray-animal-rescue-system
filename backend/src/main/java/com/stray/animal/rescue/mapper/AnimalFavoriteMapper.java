package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.AnimalFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 动物收藏Mapper
 */
@Mapper
public interface AnimalFavoriteMapper extends BaseMapper<AnimalFavorite> {
    
    /**
     * 获取用户收藏列表（包含动物信息）
     */
    @Select("SELECT af.id, af.user_id, af.animal_id, af.create_time, " +
            "a.name, a.category_id, a.breed, a.gender, a.age, a.weight, " +
            "a.health_status, a.features, a.description, a.image_url, " +
            "a.status, a.view_count, a.like_count, a.favorite_count " +
            "FROM animal_favorite af " +
            "LEFT JOIN animal a ON af.animal_id = a.id " +
            "WHERE af.user_id = #{userId} " +
            "ORDER BY af.create_time DESC")
    List<Map<String, Object>> getUserFavoritesWithAnimal(Long userId);
}
