package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.AnimalCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动物分类Mapper接口
 */
@Mapper
public interface AnimalCategoryMapper extends BaseMapper<AnimalCategory> {
} 