package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.VideoCategoryDTO;
import com.stray.animal.rescue.entity.VideoCategory;

import java.util.List;

/**
 * 视频分类服务接口
 */
public interface VideoCategoryService extends IService<VideoCategory> {
    
    /**
     * 分页查询视频分类
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    Page<VideoCategory> getCategoryPage(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 添加视频分类
     * @param categoryDTO 分类信息
     */
    void addCategory(VideoCategoryDTO categoryDTO);
    
    /**
     * 更新视频分类
     * @param categoryDTO 分类信息
     */
    void updateCategory(VideoCategoryDTO categoryDTO);
    
    /**
     * 删除视频分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
    
    /**
     * 根据ID查询视频分类
     * @param id 分类ID
     * @return 分类信息
     */
    VideoCategory getCategoryById(Long id);
    
    /**
     * 获取所有视频分类
     * @return 分类列表
     */
    List<VideoCategory> getAllCategories();
} 