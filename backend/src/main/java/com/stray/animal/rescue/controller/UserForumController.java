package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.dto.CommentDTO;
import com.stray.animal.rescue.dto.TopicDTO;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.service.ForumCommentService;
import com.stray.animal.rescue.service.ForumTopicService;
import com.stray.animal.rescue.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 用户端论坛控制器
 */
@Tag(name = "用户端论坛接口", description = "提供用户论坛相关的接口")
@RestController
@RequestMapping("/user/forum")
@Slf4j
public class UserForumController {

    @Resource
    private ForumTopicService forumTopicService;
    
    @Resource
    private ForumCommentService forumCommentService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private HttpServletRequest request;
    
    /**
     * 分页查询话题列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    @Operation(summary = "分页查询话题列表")
    @GetMapping("/topic/page")
    public Result<Page<TopicDTO>> getTopicPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<TopicDTO> page = forumTopicService.getTopicPage(pageNum, pageSize, keyword);
        return Result.success(page);
    }
    
    /**
     * 获取话题详情
     *
     * @param id 话题ID
     * @return 话题详情
     */
    @Operation(summary = "获取话题详情")
    @GetMapping("/topic/{id}")
    public Result<TopicDTO> getTopicDetail(@PathVariable Long id) {
        // 增加浏览数
        forumTopicService.incrementViewCount(id);
        
        TopicDTO topicDTO = forumTopicService.getTopicDetail(id);
        if (topicDTO == null) {
            return Result.error("话题不存在或已被删除");
        }
        
        return Result.success(topicDTO);
    }
    
    /**
     * 添加话题
     *
     * @param topicDTO 话题DTO
     * @return 话题ID
     */
    @Operation(summary = "添加话题")
    @PostMapping("/topic")
    public Result<Long> addTopic(@RequestBody @Valid TopicDTO topicDTO) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 设置用户ID
        topicDTO.setUserId(currentUser.getId());
        
        Long topicId = forumTopicService.addTopic(topicDTO);
        return Result.success(topicId);
    }
    
    /**
     * 上传话题图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    @Operation(summary = "上传话题图片")
    @PostMapping("/topic/upload")
    public Result<String> uploadTopicImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        
        try {
            String imageUrl = forumTopicService.uploadTopicImage(file);
            return Result.success(imageUrl);
        } catch (IOException e) {
            log.error("上传话题图片失败", e);
            return Result.error("上传图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除话题
     *
     * @param id 话题ID
     * @return 结果
     */
    @Operation(summary = "删除话题")
    @DeleteMapping("/topic/{id}")
    public Result<Void> deleteTopic(@PathVariable Long id) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        boolean result = forumTopicService.deleteTopic(id, currentUser.getId());
        if (!result) {
            return Result.error("删除失败，可能没有权限或话题不存在");
        }
        
        return Result.success();
    }
    
    /**
     * 获取话题评论列表
     *
     * @param topicId 话题ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    @Operation(summary = "获取话题评论列表")
    @GetMapping("/comment/page")
    public Result<Page<CommentDTO>> getCommentPage(
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<CommentDTO> page = forumCommentService.getCommentPage(topicId, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取评论回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    @Operation(summary = "获取评论回复列表")
    @GetMapping("/comment/replies/{commentId}")
    public Result<List<CommentDTO>> getCommentReplies(@PathVariable Long commentId) {
        List<CommentDTO> replies = forumCommentService.getCommentReplies(commentId);
        return Result.success(replies);
    }
    
    /**
     * 添加评论
     *
     * @param commentDTO 评论DTO
     * @return 评论ID
     */
    @Operation(summary = "添加评论")
    @PostMapping("/comment")
    public Result<Long> addComment(@RequestBody @Valid CommentDTO commentDTO) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        // 设置用户ID
        commentDTO.setUserId(currentUser.getId());
        
        Long commentId = forumCommentService.addComment(commentDTO);
        return Result.success(commentId);
    }
    
    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 结果
     */
    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        
        boolean result = forumCommentService.deleteComment(id, currentUser.getId());
        if (!result) {
            return Result.error("删除失败，可能没有权限或评论不存在");
        }
        
        return Result.success();
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        // 从请求属性中获取当前用户ID
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            log.error("获取当前用户ID失败，请检查用户是否已登录或JWT拦截器是否正常工作");
            // 默认返回ID为1的用户（测试用）
            return userService.getById(1L);
        }
        
        // 根据ID获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            log.error("获取用户信息失败，用户ID: {}", userId);
        }
        
        return user;
    }
} 