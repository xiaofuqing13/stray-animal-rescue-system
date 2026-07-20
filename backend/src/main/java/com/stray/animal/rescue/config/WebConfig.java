package com.stray.animal.rescue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.stray.animal.rescue.interceptor.JwtInterceptor;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置类
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private FileUploadConfig fileUploadConfig;
    
    @Resource
    private JwtInterceptor jwtInterceptor;

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将上传目录解析为绝对路径，避免相对路径在不同 cwd 下解析异常
        Path uploadAbsPath = Paths.get(fileUploadConfig.getPath()).toAbsolutePath().normalize();
        // 末尾必须带 / 才能作为目录被 Spring 识别
        String location = uploadAbsPath.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        log.info("静态资源映射: {}** -> {}", fileUploadConfig.getUrlPrefix(), location);

        // 配置上传文件访问路径（context-path 之后的部分，最终对外暴露在 /api/uploads/**）
        registry.addResourceHandler(fileUploadConfig.getUrlPrefix() + "**")
                .addResourceLocations(location);
        
        // Knife4j资源路径
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    
    /**
     * 视图控制器配置
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/doc.html");
    }
    
    /**
     * 拦截器配置，合并自InterceptorConfig
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/user/login",   // 登录接口
                        "/user/register", // 注册接口
                        "/error",        // 错误页面
                        "/doc.html",     // knife4j接口文档
                        "/v3/api-docs/**", // OpenAPI文档
                        "/swagger-ui/**", // Swagger UI
                        "/swagger-resources/**", // Swagger资源
                        "/webjars/**",   // webjar资源
                        "/favicon.ico",  // 网站图标
                        "/uploads/**",   // 上传文件访问
                        "/admin/upload", // 管理员上传接口
                        "/user/recommendation/hot", // 热门动物（不需要登录）
                        "/user/recommendation/personalized", // 个性化推荐（可选登录）
                        "/user/animal/list", // 动物列表（不需要登录）
                        "/user/animal/[0-9]*", // 动物详情（不需要登录）
                        "/announcement/**", // 公告（不需要登录）
                        "/carousel/**"   // 轮播图（不需要登录）
                );
    }
} 