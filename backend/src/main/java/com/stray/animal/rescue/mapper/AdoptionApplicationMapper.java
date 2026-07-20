package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.dto.AdoptionApplicationDTO;
import com.stray.animal.rescue.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 领养申请Mapper
 */
@Mapper
public interface AdoptionApplicationMapper extends BaseMapper<AdoptionApplication> {
    
    /**
     * 分页查询领养申请列表（包含用户和动物信息）
     * @param page 分页参数
     * @param status 申请状态
     * @return 领养申请列表
     */
    @Select({
        "<script>",
        "SELECT a.id, a.user_id, a.animal_id, a.reason, a.status, a.remark, a.create_time, ",
        "u.name AS user_name, u.phone AS user_phone, u.email AS user_email, u.address AS user_address, ",
        "an.name AS animal_name, ac.name AS animal_category, an.image_url AS animal_image ",
        "FROM adoption_application a ",
        "LEFT JOIN user u ON a.user_id = u.id ",
        "LEFT JOIN animal an ON a.animal_id = an.id ",
        "LEFT JOIN animal_category ac ON an.category_id = ac.id ",
        "<where>",
        "<if test='status != null'>AND a.status = #{status}</if>",
        "</where>",
        "ORDER BY a.create_time DESC",
        "</script>"
    })
    IPage<AdoptionApplicationDTO> pageApplications(Page<AdoptionApplicationDTO> page, @Param("status") Integer status);
} 