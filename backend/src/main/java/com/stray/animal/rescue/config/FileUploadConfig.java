package com.stray.animal.rescue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 文件上传路径
     */
    private String path;

    /**
     * 文件访问URL前缀
     */
    private String urlPrefix;
} 