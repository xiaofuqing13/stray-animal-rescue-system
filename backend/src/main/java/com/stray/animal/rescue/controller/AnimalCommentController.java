package com.stray.animal.rescue.controller;

import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.entity.AnimalComment;
import com.stray.animal.rescue.service.AnimalCommentService;
import com.stray.animal.rescue.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 动物评论控制器
 */
@RestController
@RequestMapping("/user/animal/comment")
public class AnimalCommentController {
    
    @Autowired
    private AnimalCommentService commentService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 获取动物评论列表
     */
    @GetMapping("/{animalId}")
    public Result<List<AnimalComment>> getComments(@PathVariable Long animalId) {
        try {
            List<AnimalComment> comments = commentService.getCommentsByAnimalId(animalId);
            // 规范化评论作者头像 URL，避免历史脏数据导致前端破图
            if (comments != null) {
                comments.forEach(c -> {
                    if (StringUtils.hasText(c.getAvatar())) {
                        c.setAvatar(fileUploadUtil.getFileUrl(c.getAvatar()));
                    }
                });
            }
            return Result.success(comments);
        } catch (Exception e) {
            return Result.error("获取评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加评论
     */
    @PostMapping
    public Result<String> addComment(@RequestBody AnimalComment comment, HttpServletRequest request) {
        try {
            // 从请求属性中获取userId（由拦截器设置）
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            comment.setUserId(userId);
            
            commentService.addComment(comment);
            return Result.success("评论成功");
        } catch (Exception e) {
            return Result.error("评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<String> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        try {
            // 从请求属性中获取userId（由拦截器设置）
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            commentService.deleteComment(commentId, userId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
