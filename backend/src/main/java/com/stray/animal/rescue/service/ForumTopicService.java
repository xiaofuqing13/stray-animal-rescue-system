package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.TopicDTO;
import com.stray.animal.rescue.entity.ForumTopic;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 论坛话题服务接口
 */
public interface ForumTopicService extends IService<ForumTopic> {
    
    /**
     * 分页查询话题列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    Page<TopicDTO> getTopicPage(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 获取话题详情
     *
     * @param id 话题ID
     * @return 话题详情
     */
    TopicDTO getTopicDetail(Long id);
    
    /**
     * 添加话题
     *
     * @param topicDTO 话题DTO
     * @return 话题ID
     */
    Long addTopic(TopicDTO topicDTO);
    
    /**
     * 上传话题图片
     *
     * @param file 图片文件
     * @return 图片URL
     * @throws IOException IO异常
     */
    String uploadTopicImage(MultipartFile file) throws IOException;
    
    /**
     * 删除话题
     *
     * @param id 话题ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteTopic(Long id, Long userId);
    
    /**
     * 增加话题浏览数
     *
     * @param id 话题ID
     */
    void incrementViewCount(Long id);
    
    /**
     * 更新话题评论数
     *
     * @param topicId 话题ID
     * @param increment 增量(可为负数)
     */
    void updateCommentCount(Long topicId, Integer increment);
} 