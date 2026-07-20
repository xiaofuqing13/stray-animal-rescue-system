package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.AnimalLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动物点赞Mapper
 */
@Mapper
public interface AnimalLikeMapper extends BaseMapper<AnimalLike> {
}
