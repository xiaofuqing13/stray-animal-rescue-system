package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.VideoDTO;
import com.stray.animal.rescue.entity.Video;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频服务接口
 */
public interface VideoService extends IService<Video> {
    
    /**
     * 分页查询视频
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @return 分页结果
     */
    Page<Video> getVideoPage(Integer pageNum, Integer pageSize, String keyword, Long categoryId);
    
    /**
     * 添加视频
     * @param videoDTO 视频信息
     */
    void addVideo(VideoDTO videoDTO);
    
    /**
     * 更新视频
     * @param videoDTO 视频信息
     */
    void updateVideo(VideoDTO videoDTO);
    
    /**
     * 删除视频
     * @param id 视频ID
     */
    void deleteVideo(Long id);
    
    /**
     * 根据ID查询视频
     * @param id 视频ID
     * @return 视频信息
     */
    Video getVideoById(Long id);
    
    /**
     * 上传视频文件
     * @param videoFile 视频文件
     * @return 视频URL
     */
    String uploadVideo(MultipartFile videoFile);
    
    /**
     * 上传视频封面
     * @param coverFile 封面文件
     * @return 封面URL
     */
    String uploadCover(MultipartFile coverFile);
} 