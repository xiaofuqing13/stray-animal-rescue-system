package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.AnimalCategoryDTO;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.AnimalCategory;
import com.stray.animal.rescue.mapper.AnimalCategoryMapper;
import com.stray.animal.rescue.mapper.AnimalMapper;
import com.stray.animal.rescue.service.AnimalCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 动物分类服务实现类
 */
@Service
public class AnimalCategoryServiceImpl extends ServiceImpl<AnimalCategoryMapper, AnimalCategory> implements AnimalCategoryService {

    @Resource
    private AnimalMapper animalMapper;

    @Override
    public List<AnimalCategory> listAllCategories() {
        // 按创建时间排序
        LambdaQueryWrapper<AnimalCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(AnimalCategory::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(AnimalCategoryDTO categoryDTO) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<AnimalCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnimalCategory::getName, categoryDTO.getName());
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("分类名称已存在");
        }
        
        // 保存分类
        AnimalCategory category = new AnimalCategory();
        BeanUtils.copyProperties(categoryDTO, category);
        this.save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(AnimalCategoryDTO categoryDTO) {
        // 检查分类是否存在
        AnimalCategory category = this.getById(categoryDTO.getId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查分类名称是否已存在（排除自身）
        LambdaQueryWrapper<AnimalCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnimalCategory::getName, categoryDTO.getName())
                    .ne(AnimalCategory::getId, categoryDTO.getId());
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("分类名称已存在");
        }
        
        // 更新分类
        BeanUtils.copyProperties(categoryDTO, category);
        this.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        // 检查分类是否存在
        AnimalCategory category = this.getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查分类是否被使用
        LambdaQueryWrapper<Animal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Animal::getCategoryId, id);
        if (animalMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("该分类下有动物，无法删除");
        }
        
        // 删除分类
        this.removeById(id);
    }

    @Override
    public AnimalCategory getCategoryById(Long id) {
        return this.getById(id);
    }
} 