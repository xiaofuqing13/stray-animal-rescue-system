package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.dto.KnowledgeCategoryDTO;
import com.stray.animal.rescue.entity.KnowledgeCategory;

import java.util.List;

/**
 * 知识分类服务接口
 */
public interface KnowledgeCategoryService {
    
    /**
     * 分页查询知识分类
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    Page<KnowledgeCategory> getCategoryPage(int pageNum, int pageSize, String keyword);
    
    /**
     * 获取所有知识分类
     * @return 分类列表
     */
    List<KnowledgeCategory> getAllCategories();
    
    /**
     * 根据ID获取知识分类
     * @param id 分类ID
     * @return 分类信息
     */
    KnowledgeCategory getCategoryById(Long id);
    
    /**
     * 添加知识分类
     * @param categoryDTO 分类信息
     */
    void addCategory(KnowledgeCategoryDTO categoryDTO);
    
    /**
     * 更新知识分类
     * @param categoryDTO 分类信息
     */
    void updateCategory(KnowledgeCategoryDTO categoryDTO);
    
    /**
     * 删除知识分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
} 