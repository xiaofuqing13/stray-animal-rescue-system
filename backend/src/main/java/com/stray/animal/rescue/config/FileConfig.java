package com.stray.animal.rescue.config;

import com.stray.animal.rescue.util.FileUploadUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件配置类，确保应用启动时自动创建上传目录
 */
@Slf4j
@Configuration
public class FileConfig implements CommandLineRunner {

    @Resource
    private FileUploadConfig fileUploadConfig;
    
    @Resource
    private FileUploadUtil fileUploadUtil;

    @Override
    public void run(String... args) {
        log.info("开始初始化文件上传目录...");
        
        // 获取上传根目录的绝对路径
        Path rootPath = Paths.get(fileUploadConfig.getPath()).toAbsolutePath().normalize();
        log.info("上传根目录的绝对路径: {}", rootPath);
        
        try {
            // 创建上传根目录
            createDirectory(rootPath.toString());
            
            // 创建各类型上传目录
            createDirectory(rootPath.resolve("animal").toString());
            createDirectory(rootPath.resolve("carousel").toString());
            createDirectory(rootPath.resolve("avatar").toString());
            createDirectory(rootPath.resolve("video").toString());
            createDirectory(rootPath.resolve("rescue").toString());
            createDirectory(rootPath.resolve("forum").toString());
            
            // 创建默认头像
            fileUploadUtil.createDefaultAvatar();
            
            log.info("文件上传目录初始化完成");
        } catch (Exception e) {
            log.error("初始化文件上传目录时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 创建目录
     *
     * @param directoryPath 目录路径
     */
    private void createDirectory(String directoryPath) {
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
                log.info("目录创建成功: {}", directoryPath);
            } catch (Exception e) {
                log.error("目录创建失败: {} - 错误: {}", directoryPath, e.getMessage(), e);
                // 尝试分析失败原因
                Path parent = directory.getParent();
                if (parent != null && !Files.exists(parent)) {
                    log.error("父目录不存在: {}", parent);
                }
                if (parent != null && Files.exists(parent) && !Files.isWritable(parent)) {
                    log.error("父目录没有写权限: {}", parent);
                }
            }
        } else {
            log.info("目录已存在: {}", directoryPath);
        }
    }
} 