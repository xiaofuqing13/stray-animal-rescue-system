package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.AdoptionApplicationDTO;
import com.stray.animal.rescue.dto.AdoptionAuditDTO;
import com.stray.animal.rescue.entity.AdoptionApplication;

import java.util.List;
import java.util.Map;

/**
 * 领养申请服务接口
 */
public interface AdoptionApplicationService extends IService<AdoptionApplication> {
    
    /**
     * 分页查询领养申请列表
     * @param page 分页参数
     * @param status 申请状态
     * @return 领养申请列表
     */
    IPage<AdoptionApplicationDTO> pageApplications(Page<AdoptionApplicationDTO> page, Integer status);
    
    /**
     * 审核领养申请
     * @param auditDTO 审核信息
     * @return 是否成功
     */
    boolean auditApplication(AdoptionAuditDTO auditDTO);
    
    /**
     * 根据ID获取申请详情
     * @param id 申请ID
     * @return 申请详情
     */
    AdoptionApplicationDTO getApplicationById(Long id);
    
    /**
     * 查询用户的所有领养申请
     * @param userId 用户ID
     * @return 申请列表
     */
    List<AdoptionApplicationDTO> getUserApplications(Long userId);
    
    /**
     * 提交领养申请
     * @param application 申请信息
     * @return 是否成功
     */
    boolean submitApplication(AdoptionApplication application);
    
    /**
     * 获取所有有待审核申请的动物ID列表
     * @return 动物ID列表
     */
    List<Long> getAnimalIdsWithPendingApplications();
    
    /**
     * 获取领养申请状态统计
     * @return 各状态领养申请数量统计
     */
    List<Map<String, Object>> getAdoptionApplicationStats();
    
    /**
     * 分页查询领养申请（管理员）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 状态筛选
     * @return 分页结果
     */
    Page<AdoptionApplicationDTO> getAdoptionPage(Integer pageNum, Integer pageSize, Integer status);
    
    /**
     * 审核领养申请（管理员）
     * @param auditDTO 审核信息
     */
    void auditAdoption(AdoptionAuditDTO auditDTO);
    
    /**
     * 添加/修改备注
     * @param id 申请ID
     * @param remark 备注内容
     */
    void addRemark(Long id, String remark);
} 