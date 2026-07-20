package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.AnimalCategoryDTO;
import com.stray.animal.rescue.entity.AnimalCategory;

import java.util.List;

/**
 * 动物分类服务接口
 */
public interface AnimalCategoryService extends IService<AnimalCategory> {
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<AnimalCategory> listAllCategories();
    
    /**
     * 添加分类
     * @param categoryDTO 分类信息
     */
    void addCategory(AnimalCategoryDTO categoryDTO);
    
    /**
     * 更新分类
     * @param categoryDTO 分类信息
     */
    void updateCategory(AnimalCategoryDTO categoryDTO);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
    
    /**
     * 获取分类详情
     * @param id 分类ID
     * @return 分类信息
     */
    AnimalCategory getCategoryById(Long id);
} 