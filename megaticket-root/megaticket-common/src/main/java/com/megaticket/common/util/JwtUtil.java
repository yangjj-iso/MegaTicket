package com.megaticket.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * author Yang JunJie
 * since 2026/1/12
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "megaticket.jwt")
public class JwtUtil {

    /**
     * 密钥 (至少32个字符)
     */
    @Value("${megaticket.jwt.secret}")
    private String secret;
    /**
     * 过期时间 (ms)，默认24小时
     */
    private long expiration = 86400000;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    /**
     * 生成Token (仅用户ID)
     *
     * @param userId 用户ID
     * @return 加密后的Token字符串
     */
    public String generateToken(Long userId) {
        return generateToken(userId, String.valueOf(userId));
    }

    /**
     * 生成Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return 加密后的Token字符串
     */
    public String generateToken(Long userId, String username) {
        // 1. 准备荷载 (Claims)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        // 2. 计算过期时间
        long nowTime = System.currentTimeMillis();
        Date now = new Date(nowTime);
        Date exp = new Date(nowTime + expiration);

        // 3. 生成密钥
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // 4. 构建Token
        return Jwts.builder()
                .claims(claims)          // 设置自定义荷载
                .subject(username)       // 设置主题 (Subject)
                .issuedAt(now)           // 签发时间
                .expiration(exp)         // 过期时间
                .id(String.valueOf(userId)) // JWT ID
                .signWith(key, Jwts.SIG.HS256) // 签名算法
                .compact();
    }

    /**
     * 解析Token获取Claims
     *
     * @param token Token字符串
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        // 1. 去除前缀
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }

        // 2. 生成密钥,进行密钥比对
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload();
        } catch (JwtException e) {
            log.error("Token解析失败: {}", e.getMessage());
            throw new RuntimeException("无效的Token");
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
}
