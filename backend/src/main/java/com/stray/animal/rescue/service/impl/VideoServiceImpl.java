package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.VideoDTO;
import com.stray.animal.rescue.entity.Video;
import com.stray.animal.rescue.mapper.VideoMapper;
import com.stray.animal.rescue.service.VideoService;
import com.stray.animal.rescue.util.FileUploadUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;

/**
 * 视频服务实现类
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Resource
    private FileUploadUtil fileUploadUtil;

    @Override
    public Page<Video> getVideoPage(Integer pageNum, Integer pageSize, String keyword, Long categoryId) {
        // 创建分页对象
        Page<Video> page = new Page<>(pageNum, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        
        // 关键字搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Video::getTitle, keyword)
                   .or()
                   .like(Video::getDescription, keyword);
        }
        
        // 按分类ID查询
        if (categoryId != null) {
            wrapper.eq(Video::getCategoryId, categoryId);
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(Video::getCreateTime);
        
        // 执行分页查询
        Page<Video> videoPage = page(page, wrapper);
        
        // 处理视频URL和封面URL
        videoPage.getRecords().forEach(video -> {
            if (StringUtils.hasText(video.getVideoUrl())) {
                video.setVideoUrl(fileUploadUtil.getFileUrl(video.getVideoUrl()));
            }
            if (StringUtils.hasText(video.getCoverUrl())) {
                video.setCoverUrl(fileUploadUtil.getFileUrl(video.getCoverUrl()));
            }
        });
        
        return videoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addVideo(VideoDTO videoDTO) {
        // 创建实体对象
        Video video = new Video();
        // 复制属性
        BeanUtils.copyProperties(videoDTO, video);
        // 设置初始播放数
        video.setViewCount(0);
        // 保存视频
        save(video);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVideo(VideoDTO videoDTO) {
        // 创建实体对象
        Video video = new Video();
        // 复制属性
        BeanUtils.copyProperties(videoDTO, video);
        // 更新视频
        updateById(video);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(Long id) {
        // 获取视频信息
        Video video = getById(id);
        if (video != null) {
            // 删除视频文件
            if (StringUtils.hasText(video.getVideoUrl())) {
                fileUploadUtil.deleteFile(video.getVideoUrl());
            }
            // 删除封面文件
            if (StringUtils.hasText(video.getCoverUrl())) {
                fileUploadUtil.deleteFile(video.getCoverUrl());
            }
            // 删除数据库记录
            removeById(id);
        }
    }

    @Override
    public Video getVideoById(Long id) {
        Video video = getById(id);
        if (video != null) {
            // 处理视频URL和封面URL
            if (StringUtils.hasText(video.getVideoUrl())) {
                video.setVideoUrl(fileUploadUtil.getFileUrl(video.getVideoUrl()));
            }
            if (StringUtils.hasText(video.getCoverUrl())) {
                video.setCoverUrl(fileUploadUtil.getFileUrl(video.getCoverUrl()));
            }
        }
        return video;
    }

    @Override
    public String uploadVideo(MultipartFile videoFile) {
        try {
            return fileUploadUtil.uploadFile(videoFile, "video");
        } catch (IOException e) {
            throw new RuntimeException("上传视频失败", e);
        }
    }

    @Override
    public String uploadCover(MultipartFile coverFile) {
        try {
            return fileUploadUtil.uploadFile(coverFile, "cover");
        } catch (IOException e) {
            throw new RuntimeException("上传封面失败", e);
        }
    }
} 