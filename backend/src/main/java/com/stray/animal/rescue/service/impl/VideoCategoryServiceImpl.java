package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.VideoCategoryDTO;
import com.stray.animal.rescue.entity.Video;
import com.stray.animal.rescue.entity.VideoCategory;
import com.stray.animal.rescue.mapper.VideoCategoryMapper;
import com.stray.animal.rescue.mapper.VideoMapper;
import com.stray.animal.rescue.service.VideoCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 视频分类服务实现类
 */
@Service
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryMapper, VideoCategory> implements VideoCategoryService {

    @Resource
    private VideoMapper videoMapper;

    @Override
    public Page<VideoCategory> getCategoryPage(Integer pageNum, Integer pageSize, String keyword) {
        // 创建分页对象
        Page<VideoCategory> page = new Page<>(pageNum, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<VideoCategory> wrapper = new LambdaQueryWrapper<>();
        
        // 关键字搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(VideoCategory::getName, keyword)
                   .or()
                   .like(VideoCategory::getDescription, keyword);
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(VideoCategory::getCreateTime);
        
        // 执行分页查询
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(VideoCategoryDTO categoryDTO) {
        // 创建实体对象
        VideoCategory category = new VideoCategory();
        // 复制属性
        BeanUtils.copyProperties(categoryDTO, category);
        // 保存分类
        save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(VideoCategoryDTO categoryDTO) {
        // 创建实体对象
        VideoCategory category = new VideoCategory();
        // 复制属性
        BeanUtils.copyProperties(categoryDTO, category);
        // 更新分类
        updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        // 检查分类下是否有视频
        LambdaQueryWrapper<Video> videoWrapper = new LambdaQueryWrapper<>();
        videoWrapper.eq(Video::getCategoryId, id);
        long count = videoMapper.selectCount(videoWrapper);
        
        if (count > 0) {
            throw new RuntimeException("该分类下有关联的视频，无法删除");
        }
        
        // 删除分类
        removeById(id);
    }

    @Override
    public VideoCategory getCategoryById(Long id) {
        return getById(id);
    }

    @Override
    public List<VideoCategory> getAllCategories() {
        // 创建查询条件
        LambdaQueryWrapper<VideoCategory> wrapper = new LambdaQueryWrapper<>();
        // 按创建时间降序排序
        wrapper.orderByDesc(VideoCategory::getCreateTime);
        // 执行查询
        return list(wrapper);
    }
} 