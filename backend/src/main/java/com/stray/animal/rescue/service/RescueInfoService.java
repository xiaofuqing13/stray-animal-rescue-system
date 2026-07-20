package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.RescueInfoDTO;
import com.stray.animal.rescue.entity.RescueInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 救助信息服务接口
 */
public interface RescueInfoService extends IService<RescueInfo> {
    
    /**
     * 分页查询救助信息
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词
     * @param status 状态
     * @return 分页结果
     */
    Page<RescueInfo> getRescueInfoPage(Integer pageNum, Integer pageSize, String keyword, Integer status);
    
    /**
     * 添加救助信息
     * 
     * @param rescueInfoDTO 救助信息DTO
     */
    void addRescueInfo(RescueInfoDTO rescueInfoDTO);
    
    /**
     * 更新救助信息
     * 
     * @param rescueInfoDTO 救助信息DTO
     */
    void updateRescueInfo(RescueInfoDTO rescueInfoDTO);
    
    /**
     * 更新救助信息状态
     * 
     * @param id 救助信息ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 上传救助图片
     * 
     * @param file 图片文件
     * @return 图片URL
     * @throws IOException 异常
     */
    String uploadImage(MultipartFile file) throws IOException;
    
    /**
     * 获取救助信息状态统计
     * @return 各状态救助信息数量统计
     */
    List<Map<String, Object>> getRescueInfoStats();
} 