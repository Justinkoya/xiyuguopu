package com.xiyuguopu.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 — 管理后台 + 小程序共用
 */
public class JwtUtils {

    private static final String DEV_SECRET = "xiyuguopu-jwt-secret-key-2025-keep-it-safe!!";
    private static final long EXPIRE_MS = 7 * 24 * 60 * 60 * 1000L; // 7 天
    private static final SecretKey KEY = Keys.hmacShaKeyFor(resolveSecret().getBytes(StandardCharsets.UTF_8));

    private static String resolveSecret() {
        String secret = System.getenv("XIYU_JWT_SECRET");
        if (secret != null && !secret.isBlank()) {
            return secret;
        }

        String profiles = firstNonBlank(
                System.getProperty("spring.profiles.active"),
                System.getenv("SPRING_PROFILES_ACTIVE"));
        if (profiles != null && profiles.toLowerCase().contains("prod")) {
            throw new IllegalStateException("生产环境必须配置 XIYU_JWT_SECRET 环境变量");
        }

        return DEV_SECRET;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    /** 管理后台 token：载荷存 userId + username + role=ADMIN */
    public static String createToken(Long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", "ADMIN")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRE_MS))
                .signWith(KEY)
                .compact();
    }

    /** 小程序用户 token：载荷存 userId + openid + role=USER */
    public static String createUserToken(Long userId, String openid) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("openid", openid)
                .claim("role", "USER")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRE_MS))
                .signWith(KEY)
                .compact();
    }

    /** 解析 token，无效返回 null */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /** 从 Claims 中取 userId（通用） */
    public static Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    /** 从 Claims 中取 username（管理后台用） */
    public static String getUsername(Claims claims) {
        return claims.get("username", String.class);
    }

    /** 从 Claims 中取 openid（小程序用） */
    public static String getOpenid(Claims claims) {
        return claims.get("openid", String.class);
    }

    /** 从 Claims 中取 role（ADMIN / USER） */
    public static String getRole(Claims claims) {
        return claims.get("role", String.class);
    }
}
