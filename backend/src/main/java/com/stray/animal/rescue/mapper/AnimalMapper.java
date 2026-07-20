package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.Animal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动物Mapper接口
 */
@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
} 