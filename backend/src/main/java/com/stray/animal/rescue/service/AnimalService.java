package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.AnimalDTO;
import com.stray.animal.rescue.entity.Animal;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 动物服务接口
 */
public interface AnimalService extends IService<Animal> {
    
    /**
     * 分页查询动物
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @param status 状态
     * @return 分页结果
     */
    Page<Animal> getAnimalPage(Integer pageNum, Integer pageSize, String keyword, Long categoryId, Integer status);
    
    /**
     * 添加动物
     * @param animalDTO 动物信息
     */
    void addAnimal(AnimalDTO animalDTO);
    
    /**
     * 更新动物
     * @param animalDTO 动物信息
     */
    void updateAnimal(AnimalDTO animalDTO);
    
    /**
     * 删除动物
     * @param id 动物ID
     */
    void deleteAnimal(Long id);
    
    /**
     * 根据ID查询动物
     * @param id 动物ID
     * @return 动物信息
     */
    Animal getAnimalById(Long id);
    
    /**
     * 上传动物图片
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadImage(MultipartFile file) throws IOException;
    
    /**
     * 获取动物分类名称
     * @param categoryId 分类ID
     * @return 分类名称
     */
    String getCategoryName(Long categoryId);
    
    /**
     * 获取动物种类分布统计
     * @return 各种类动物数量统计
     */
    List<Map<String, Object>> getAnimalCategoryStats();
    
    /**
     * 获取动物领养状态统计
     * @return 各领养状态动物数量统计
     */
    List<Map<String, Object>> getAnimalAdoptionStats();
    
    /**
     * 获取动物健康状况统计
     * @return 各健康状况动物数量统计
     */
    List<Map<String, Object>> getAnimalHealthStats();
} 