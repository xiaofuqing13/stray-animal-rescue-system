package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.CommentDTO;
import com.stray.animal.rescue.entity.ForumComment;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.mapper.ForumCommentMapper;
import com.stray.animal.rescue.service.ForumCommentService;
import com.stray.animal.rescue.service.ForumTopicService;
import com.stray.animal.rescue.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 论坛评论服务实现类
 */
@Service
@Slf4j
public class ForumCommentServiceImpl extends ServiceImpl<ForumCommentMapper, ForumComment> implements ForumCommentService {

    @Resource
    private UserService userService;
    
    @Resource
    private ForumTopicService forumTopicService;
    
    @Override
    public Page<CommentDTO> getCommentPage(Long topicId, Integer pageNum, Integer pageSize) {
        // 查询顶级评论（不带回复）
        LambdaQueryWrapper<ForumComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ForumComment::getTopicId, topicId)
                .isNull(ForumComment::getParentId)
                .eq(ForumComment::getStatus, 1)
                .orderByDesc(ForumComment::getCreateTime);
        
        // 分页查询
        Page<ForumComment> page = new Page<>(pageNum, pageSize);
        Page<ForumComment> commentPage = this.page(page, queryWrapper);
        
        // 获取所有评论ID
        List<Long> commentIds = commentPage.getRecords().stream()
                .map(ForumComment::getId)
                .collect(Collectors.toList());
        
        // 获取回复
        Map<Long, List<CommentDTO>> repliesMap = new HashMap<>();
        if (!commentIds.isEmpty()) {
            // 查询所有回复
            LambdaQueryWrapper<ForumComment> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.in(ForumComment::getParentId, commentIds)
                    .eq(ForumComment::getStatus, 1)
                    .orderByAsc(ForumComment::getCreateTime);
            
            List<ForumComment> replies = this.list(replyWrapper);
            
            // 按父评论ID分组
            Map<Long, List<ForumComment>> repliesGrouped = replies.stream()
                    .collect(Collectors.groupingBy(ForumComment::getParentId));
            
            // 转换为DTO对象
            for (Map.Entry<Long, List<ForumComment>> entry : repliesGrouped.entrySet()) {
                List<CommentDTO> replyDtos = new ArrayList<>();
                for (ForumComment reply : entry.getValue()) {
                    replyDtos.add(convertToDto(reply));
                }
                repliesMap.put(entry.getKey(), replyDtos);
            }
        }
        
        // 转换为DTO对象
        List<CommentDTO> records = new ArrayList<>();
        for (ForumComment comment : commentPage.getRecords()) {
            CommentDTO commentDTO = convertToDto(comment);
            
            // 设置回复列表
            commentDTO.setReplies(repliesMap.getOrDefault(comment.getId(), new ArrayList<>()));
            
            records.add(commentDTO);
        }
        
        // 构造返回结果
        Page<CommentDTO> result = new Page<>();
        BeanUtils.copyProperties(commentPage, result, "records");
        result.setRecords(records);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(CommentDTO commentDTO) {
        // 构建实体对象
        ForumComment comment = new ForumComment();
        BeanUtils.copyProperties(commentDTO, comment);
        
        // 设置状态
        comment.setStatus(1);
        
        // 保存评论
        this.save(comment);
        
        // 更新话题评论数
        forumTopicService.updateCommentCount(comment.getTopicId(), 1);
        
        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long id, Long userId) {
        // 查询评论
        ForumComment comment = this.getById(id);
        if (comment == null || comment.getStatus() != 1) {
            return false;
        }
        
        // 判断是否为发布者
        if (!comment.getUserId().equals(userId)) {
            return false;
        }
        
        // 逻辑删除
        comment.setStatus(0);
        boolean result = this.updateById(comment);
        
        if (result) {
            // 更新话题评论数
            forumTopicService.updateCommentCount(comment.getTopicId(), -1);
            
            // 如果是顶级评论，还需要删除其下所有回复
            if (comment.getParentId() == null) {
                // 查询所有回复
                LambdaQueryWrapper<ForumComment> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ForumComment::getParentId, id)
                        .eq(ForumComment::getStatus, 1);
                
                List<ForumComment> replies = this.list(queryWrapper);
                
                // 批量逻辑删除
                if (!replies.isEmpty()) {
                    replies.forEach(reply -> reply.setStatus(0));
                    this.updateBatchById(replies);
                    
                    // 更新话题评论数
                    forumTopicService.updateCommentCount(comment.getTopicId(), -replies.size());
                }
            }
        }
        
        return result;
    }

    @Override
    public List<CommentDTO> getCommentReplies(Long commentId) {
        // 查询回复列表
        LambdaQueryWrapper<ForumComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ForumComment::getParentId, commentId)
                .eq(ForumComment::getStatus, 1)
                .orderByAsc(ForumComment::getCreateTime);
        
        List<ForumComment> replies = this.list(queryWrapper);
        
        // 转换为DTO对象
        List<CommentDTO> result = new ArrayList<>();
        for (ForumComment reply : replies) {
            result.add(convertToDto(reply));
        }
        
        return result;
    }
    
    /**
     * 将评论实体转换为DTO对象
     *
     * @param comment 评论实体
     * @return DTO对象
     */
    private CommentDTO convertToDto(ForumComment comment) {
        CommentDTO dto = new CommentDTO();
        BeanUtils.copyProperties(comment, dto);
        
        // 设置创建时间格式化字符串
        if (comment.getCreateTime() != null) {
            dto.setCreateTime(comment.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // 获取用户信息
        if (comment.getUserId() != null) {
            User user = userService.getById(comment.getUserId());
            dto.setUser(user);
        }
        
        // 如果有父评论，获取父评论信息
        if (comment.getParentId() != null) {
            ForumComment parentComment = this.getById(comment.getParentId());
            if (parentComment != null && parentComment.getStatus() == 1) {
                CommentDTO parentDto = new CommentDTO();
                BeanUtils.copyProperties(parentComment, parentDto);
                
                // 获取父评论用户信息
                if (parentComment.getUserId() != null) {
                    User parentUser = userService.getById(parentComment.getUserId());
                    parentDto.setUser(parentUser);
                }
                
                dto.setParentComment(parentDto);
            }
        }
        
        return dto;
    }
} 