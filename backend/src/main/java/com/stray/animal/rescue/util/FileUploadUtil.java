package com.stray.animal.rescue.util;

import com.stray.animal.rescue.config.FileUploadConfig;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Resource
    private FileUploadConfig fileUploadConfig;
    
    private Path rootPath;
    
    /**
     * 初始化根路径
     */
    private Path getRootPath() {
        if (rootPath == null) {
            // 获取上传根目录的绝对路径
            Path configPath = Paths.get(fileUploadConfig.getPath()).toAbsolutePath();
            // 规范化路径，解决 ../ 或 ./ 等相对路径问题
            rootPath = configPath.normalize();
            log.info("初始化文件上传根目录: {}", rootPath);
        }
        return rootPath;
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param dir  子目录，例如：animal、video、avatar等
     * @return 文件相对路径，例如：/animal/xxx.jpg
     */
    public String uploadFile(MultipartFile file, String dir) throws IOException {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            log.error("上传的文件为空");
            throw new IOException("文件为空");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "unknown";
        }
        log.info("正在上传文件: {}", originalFilename);
        
        String extension = FilenameUtils.getExtension(originalFilename);
        if (extension == null || extension.isEmpty()) {
            extension = "jpg";
        }

        // 生成新的文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        log.info("生成的新文件名: {}", newFileName);
        
        // 获取根路径
        Path root = getRootPath();
        log.info("使用根路径: {}", root);
        
        // 目标目录
        Path targetDir = root.resolve(dir);
        log.info("目标目录: {}", targetDir);
        
        // 确保目录存在
        try {
            Files.createDirectories(targetDir);
            log.info("目录已创建或已存在: {}", targetDir);
        } catch (IOException e) {
            log.error("创建目录时出错: {}", e.getMessage(), e);
            throw new IOException("创建目录失败: " + targetDir, e);
        }

        // 目标文件路径
        Path targetPath = targetDir.resolve(newFileName);
        log.info("目标文件路径: {}", targetPath);

        try {
            // 保存文件
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("文件上传成功: {}", targetPath);
            
            // 检查文件是否真的被保存了
            if (Files.exists(targetPath)) {
                log.info("文件确实已保存到: {}", targetPath);
            } else {
                log.error("文件上传后未找到: {}", targetPath);
            }
            
            // 返回文件相对路径，数据库存储用
            return "/" + dir + "/" + newFileName;
        } catch (IOException e) {
            log.error("保存文件时出错: {}", e.getMessage(), e);
            throw new IOException("保存文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件访问URL（幂等且防双斜杠）
     *
     * 输入兼容三种历史格式：
     *   ① /animal/xxx.jpg            → 不含前缀，自动拼接
     *   ② /uploads/animal/xxx.jpg    → 已含前缀，直接返回
     *   ③ /uploads//animal/xxx.jpg   → 历史脏数据双斜杠，规范化为单斜杠
     * 输出统一格式：/uploads/animal/xxx.jpg
     *
     * @param filePath 文件相对路径
     * @return 规范化的访问 URL
     */
    public String getFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        // 完整 URL（http/https）原样返回，避免破坏外链
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }

        // 取标准前缀，去掉末尾斜杠（避免与 path 头斜杠拼出双斜杠）
        String prefix = fileUploadConfig.getUrlPrefix();
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }

        // 如果已含前缀，剥离前缀部分，剩下的尾部统一规范
        String tail;
        if (filePath.startsWith(prefix + "/") || filePath.equals(prefix)) {
            tail = filePath.substring(prefix.length());
        } else {
            tail = filePath;
        }

        // 去掉尾部所有连续的前导斜杠，再补一个，确保只有一个分隔符
        while (tail.startsWith("/")) {
            tail = tail.substring(1);
        }

        return prefix + "/" + tail;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件相对路径，例如：/animal/xxx.jpg
     * @return 是否删除成功
     */
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        // 确保以/开头
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        
        try {
            // 获取完整的文件路径
            Path fullPath = getRootPath().resolve(filePath.substring(1));
            log.info("尝试删除文件: {}", fullPath);
            
            // 检查文件是否存在
            if (Files.exists(fullPath)) {
                // 删除文件
                Files.delete(fullPath);
                log.info("文件删除成功: {}", fullPath);
                return true;
            } else {
                log.warn("要删除的文件不存在: {}", fullPath);
                return false;
            }
        } catch (IOException e) {
            log.error("删除文件时出错: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 创建默认的头像文件
     */
    public void createDefaultAvatar() {
        try {
            // 默认头像文件路径
            Path avatarDir = getRootPath().resolve("avatar");
            Path defaultAvatarPath = avatarDir.resolve("default.png");
            
            // 检查目录是否存在，不存在则创建
            if (!Files.exists(avatarDir)) {
                Files.createDirectories(avatarDir);
                log.info("创建头像目录: {}", avatarDir);
            }
            
            // 如果默认头像不存在，则创建一个空文件
            if (!Files.exists(defaultAvatarPath)) {
                // 这里可以替换为复制一个实际的默认头像文件
                Files.createFile(defaultAvatarPath);
                log.info("创建默认头像文件: {}", defaultAvatarPath);
            }
        } catch (IOException e) {
            log.error("创建默认头像文件时出错: {}", e.getMessage(), e);
        }
    }
} 