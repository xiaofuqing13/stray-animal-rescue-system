package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stray.animal.rescue.entity.AnimalExtended;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动物扩展属性Mapper
 */
@Mapper
public interface AnimalExtendedMapper extends BaseMapper<AnimalExtended> {
}
