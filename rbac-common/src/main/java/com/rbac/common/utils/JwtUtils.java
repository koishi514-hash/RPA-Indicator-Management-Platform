package com.rbac.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtils {

    /**
     * 默认密钥（生产环境请使用配置文件）
     */
    private static final String SECRET_KEY = "financial-scaffold-secret-key-for-jwt-token-generation-2024";

    /**
     * 过期时间（毫秒）- 默认30天
     */
    private static final long EXPIRATION = 30 * 24 * 60 * 60 * 1000;

    /**
     * 生成密钥
     */
    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Token
     *
     * @param subject 主题（通常是用户ID）
     * @return Token
     */
    public static String generateToken(String subject) {
        return generateToken(subject, null);
    }

    /**
     * 生成Token
     *
     * @param subject 主题（通常是用户ID）
     * @param claims  自定义声明
     * @return Token
     */
    public static String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成Token
     *
     * @param subject 主题（通常是用户ID）
     * @param claims  自定义声明
     * @param expirationMs 过期时间（毫秒）
     * @return Token
     */
    public static String generateToken(String subject, Map<String, Object> claims, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析Token
     *
     * @param token Token
     * @return Claims
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取Token中的主题
     *
     * @param token Token
     * @return 主题
     */
    public static String getSubject(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            log.error("验证Token失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断Token是否过期
     *
     * @param claims Claims
     * @return 是否过期
     */
    private static boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 刷新Token
     *
     * @param token 原Token
     * @return 新Token
     */
    public static String refreshToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return generateToken(claims.getSubject(), claims);
    }
}
