package com.stray.animal.rescue.interceptor;

import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * JWT拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        
        // 打印请求路径，用于调试
        log.info("拦截到请求: {}", request.getRequestURI());
        
        // 如果没有token，直接放行（由控制器决定是否需要登录）
        if (token == null || token.isEmpty()) {
            log.info("未携带Token, URI: {}", request.getRequestURI());
            return true;
        }
        
        // Bearer Token处理
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            // 验证token是否过期
            if (JwtUtil.isTokenExpired(token)) {
                log.warn("Token已过期");
                responseError(response, 401, "登录已过期，请重新登录");
                return false;
            }
            
            // 从token中获取用户信息，可以保存到请求中供后续使用
            Long userId = JwtUtil.getUserId(token);
            String username = JwtUtil.getUsername(token);
            
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            
            log.info("Token验证成功, userId: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败", e);
            responseError(response, 401, "身份验证失败");
            return false;
        }
    }
    
    /**
     * 返回错误信息
     */
    private void responseError(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, message)));
    }
} 