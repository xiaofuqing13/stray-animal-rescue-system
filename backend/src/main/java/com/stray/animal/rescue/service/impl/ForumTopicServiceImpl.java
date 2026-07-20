package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.TopicDTO;
import com.stray.animal.rescue.entity.ForumTopic;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.mapper.ForumTopicMapper;
import com.stray.animal.rescue.service.ForumTopicService;
import com.stray.animal.rescue.service.UserService;
import com.stray.animal.rescue.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 论坛话题服务实现类
 */
@Service
@Slf4j
public class ForumTopicServiceImpl extends ServiceImpl<ForumTopicMapper, ForumTopic> implements ForumTopicService {

    @Resource
    private UserService userService;
    
    @Resource
    private FileUploadUtil fileUploadUtil;
    
    @Override
    public Page<TopicDTO> getTopicPage(Integer pageNum, Integer pageSize, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<ForumTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ForumTopic::getStatus, 1); // 只查询正常状态的话题
        
        // 添加关键字搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(ForumTopic::getTitle, keyword)
                    .or()
                    .like(ForumTopic::getContent, keyword);
        }
        
        // 按照创建时间降序排序
        queryWrapper.orderByDesc(ForumTopic::getCreateTime);
        
        // 执行分页查询
        Page<ForumTopic> page = new Page<>(pageNum, pageSize);
        Page<ForumTopic> topicPage = this.page(page, queryWrapper);
        
        // 转换为DTO对象
        List<TopicDTO> records = new ArrayList<>();
        for (ForumTopic topic : topicPage.getRecords()) {
            TopicDTO topicDTO = new TopicDTO();
            BeanUtils.copyProperties(topic, topicDTO);
            
            // 设置创建时间格式化字符串
            if (topic.getCreateTime() != null) {
                topicDTO.setCreateTime(topic.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            
            // 获取用户信息
            if (topic.getUserId() != null) {
                User user = userService.getById(topic.getUserId());
                topicDTO.setUser(user);
            }
            
            // 处理图片URL
            if (StringUtils.hasText(topic.getImageUrl())) {
                topicDTO.setImageUrl(fileUploadUtil.getFileUrl(topic.getImageUrl()));
            }
            
            records.add(topicDTO);
        }
        
        // 构造返回结果
        Page<TopicDTO> result = new Page<>();
        BeanUtils.copyProperties(topicPage, result, "records");
        result.setRecords(records);
        
        return result;
    }

    @Override
    public TopicDTO getTopicDetail(Long id) {
        ForumTopic topic = this.getById(id);
        if (topic == null || topic.getStatus() != 1) {
            return null;
        }
        
        TopicDTO topicDTO = new TopicDTO();
        BeanUtils.copyProperties(topic, topicDTO);
        
        // 设置创建时间格式化字符串
        if (topic.getCreateTime() != null) {
            topicDTO.setCreateTime(topic.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // 获取用户信息
        if (topic.getUserId() != null) {
            User user = userService.getById(topic.getUserId());
            topicDTO.setUser(user);
        }
        
        // 处理图片URL
        if (StringUtils.hasText(topic.getImageUrl())) {
            topicDTO.setImageUrl(fileUploadUtil.getFileUrl(topic.getImageUrl()));
        }
        
        return topicDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTopic(TopicDTO topicDTO) {
        // 构建实体对象
        ForumTopic topic = new ForumTopic();
        BeanUtils.copyProperties(topicDTO, topic);
        
        // 设置初始值
        topic.setViewCount(0);
        topic.setCommentCount(0);
        topic.setLikeCount(0);
        topic.setStatus(1);
        
        // 保存话题
        this.save(topic);
        
        return topic.getId();
    }

    @Override
    public String uploadTopicImage(MultipartFile file) throws IOException {
        // 上传图片
        String imageUrl = fileUploadUtil.uploadFile(file, "forum/topic");
        
        // 返回图片URL
        return fileUploadUtil.getFileUrl(imageUrl);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTopic(Long id, Long userId) {
        // 查询话题
        ForumTopic topic = this.getById(id);
        if (topic == null || topic.getStatus() != 1) {
            return false;
        }
        
        // 判断是否为发布者
        if (!topic.getUserId().equals(userId)) {
            return false;
        }
        
        // 逻辑删除
        topic.setStatus(0);
        
        return this.updateById(topic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        // 获取话题
        ForumTopic topic = this.getById(id);
        if (topic == null || topic.getStatus() != 1) {
            return;
        }
        
        // 增加浏览数
        topic.setViewCount(topic.getViewCount() + 1);
        
        this.updateById(topic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommentCount(Long topicId, Integer increment) {
        // 获取话题
        ForumTopic topic = this.getById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            return;
        }
        
        // 更新评论数
        topic.setCommentCount(topic.getCommentCount() + increment);
        if (topic.getCommentCount() < 0) {
            topic.setCommentCount(0);
        }
        
        this.updateById(topic);
    }
} 