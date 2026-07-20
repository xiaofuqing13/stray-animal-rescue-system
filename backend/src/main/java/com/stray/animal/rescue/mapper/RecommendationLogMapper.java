package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.RecommendationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推荐日志Mapper
 */
@Mapper
public interface RecommendationLogMapper extends BaseMapper<RecommendationLog> {
}
