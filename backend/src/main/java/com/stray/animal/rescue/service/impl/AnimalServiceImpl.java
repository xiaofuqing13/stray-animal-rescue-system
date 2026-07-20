package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.AnimalDTO;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.AnimalCategory;
import com.stray.animal.rescue.mapper.AnimalMapper;
import com.stray.animal.rescue.service.AnimalCategoryService;
import com.stray.animal.rescue.service.AnimalService;
import com.stray.animal.rescue.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动物服务实现类
 */
@Service
@Slf4j
public class AnimalServiceImpl extends ServiceImpl<AnimalMapper, Animal> implements AnimalService {

    @Resource
    private AnimalCategoryService animalCategoryService;
    
    @Resource
    private FileUploadUtil fileUploadUtil;

    @Override
    public Page<Animal> getAnimalPage(Integer pageNum, Integer pageSize, String keyword, Long categoryId, Integer status) {
        LambdaQueryWrapper<Animal> queryWrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Animal::getName, keyword)
                        .or()
                        .like(Animal::getFeatures, keyword)
                        .or()
                        .like(Animal::getDescription, keyword)
                        .or()
                        .like(Animal::getHealthStatus, keyword);
        }
        
        if (categoryId != null) {
            queryWrapper.eq(Animal::getCategoryId, categoryId);
        }
        
        if (status != null) {
            queryWrapper.eq(Animal::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Animal::getCreateTime);
        
        // 分页查询
        Page<Animal> page = new Page<>(pageNum, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAnimal(AnimalDTO animalDTO) {
        // 验证分类是否存在
        AnimalCategory category = animalCategoryService.getById(animalDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 保存动物信息
        Animal animal = new Animal();
        BeanUtils.copyProperties(animalDTO, animal);
        this.save(animal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnimal(AnimalDTO animalDTO) {
        // 验证动物是否存在
        Animal animal = this.getById(animalDTO.getId());
        if (animal == null) {
            throw new RuntimeException("动物不存在");
        }
        
        // 验证分类是否存在
        AnimalCategory category = animalCategoryService.getById(animalDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 更新动物信息
        BeanUtils.copyProperties(animalDTO, animal);
        this.updateById(animal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnimal(Long id) {
        // 验证动物是否存在
        Animal animal = this.getById(id);
        if (animal == null) {
            throw new RuntimeException("动物不存在");
        }
        
        // 删除动物信息
        this.removeById(id);
        
        // 如果有图片，尝试删除图片文件
        if (StringUtils.hasText(animal.getImageUrl())) {
            try {
                fileUploadUtil.deleteFile(animal.getImageUrl());
            } catch (Exception e) {
                // 图片删除失败不影响动物信息删除
                log.error("删除动物图片失败：{}", e.getMessage());
            }
        }
    }

    @Override
    public Animal getAnimalById(Long id) {
        return this.getById(id);
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择图片文件");
        }
        
        // 上传图片
        String imageUrl = fileUploadUtil.uploadFile(file, "animal");
        
        // 返回图片URL
        return fileUploadUtil.getFileUrl(imageUrl);
    }
    
    /**
     * 获取动物分类名称
     * @param categoryId 分类ID
     * @return 分类名称
     */
    @Override
    public String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return "";
        }
        
        AnimalCategory category = animalCategoryService.getById(categoryId);
        return category != null ? category.getName() : "";
    }

    @Override
    public List<Map<String, Object>> getAnimalCategoryStats() {
        // 查询各种类动物数量
        // 由于MyBatis-Plus不支持直接使用leftJoin，这里使用自定义SQL或多表连接查询
        // 简化实现：先获取所有动物的种类ID，然后统计每个种类的数量
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取所有动物种类
        List<AnimalCategory> categories = animalCategoryService.list();
        for (AnimalCategory category : categories) {
            long count = count(new LambdaQueryWrapper<Animal>()
                    .eq(Animal::getCategoryId, category.getId()));
            
            if (count > 0) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("name", category.getName());
                categoryMap.put("value", count);
                result.add(categoryMap);
            }
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getAnimalAdoptionStats() {
        // 查询各领养状态动物数量
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询待领养动物数量
        long waitingCount = count(new LambdaQueryWrapper<Animal>().eq(Animal::getStatus, 0));
        Map<String, Object> waitingMap = new HashMap<>();
        waitingMap.put("name", "待领养");
        waitingMap.put("value", waitingCount);
        result.add(waitingMap);
        
        // 查询已领养动物数量
        long adoptedCount = count(new LambdaQueryWrapper<Animal>().eq(Animal::getStatus, 1));
        Map<String, Object> adoptedMap = new HashMap<>();
        adoptedMap.put("name", "已领养");
        adoptedMap.put("value", adoptedCount);
        result.add(adoptedMap);
        
        // 查询救助中动物数量
        long rescuingCount = count(new LambdaQueryWrapper<Animal>().eq(Animal::getStatus, 2));
        Map<String, Object> rescuingMap = new HashMap<>();
        rescuingMap.put("name", "救助中");
        rescuingMap.put("value", rescuingCount);
        result.add(rescuingMap);
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getAnimalHealthStats() {
        // 查询各健康状况动物数量
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 健康状况：健康、亚健康、生病、重病
        String[] healthStatuses = {"健康", "亚健康", "生病", "重病"};
        for (String status : healthStatuses) {
            long count = count(new LambdaQueryWrapper<Animal>().eq(Animal::getHealthStatus, status));
            Map<String, Object> healthMap = new HashMap<>();
            healthMap.put("name", status);
            healthMap.put("value", count);
            result.add(healthMap);
        }
        
        return result;
    }
} 