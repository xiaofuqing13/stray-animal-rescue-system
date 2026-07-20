package com.stray.animal.rescue.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtil {

    /**
     * 密钥（演示用默认值，生产环境务必更换为随机长密钥，勿使用默认值部署）
     */
    private static final String SECRET_KEY_STRING = "stray-animal-rescue-demo-jwt-secret-change-me-0123456789abcdef";
    
    /**
     * 生成密钥
     */
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    /**
     * 过期时间（毫秒）
     */
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @return token
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 解析结果
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从token中获取用户名
     *
     * @param token token
     * @return 用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username").toString();
    }
} 