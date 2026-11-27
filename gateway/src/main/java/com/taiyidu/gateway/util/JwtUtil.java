package com.taiyidu.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT对称签名工具类 HS256
 * 配置写在yml，使用时@Autowired注入，不要new
 * 注意：JWT的payload是明文，禁止存放敏感信息！
 */
@Component
public class JwtUtil {

    /**
     * 从yml读取jwt密钥
     * HS256强制要求：密钥原始字符串最少32个字符
     */
    @Value("${jwt.secret}")
    private String secretStr;

    /**
     * token过期时间，yml单位毫秒
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成HMAC密钥对象
     * 每次调用重新生成，避免提前初始化静态key空值问题
     */
    private SecretKey getHmacKey() {
        return Keys.hmacShaKeyFor(secretStr.getBytes());
    }

    /**
     * 生成JWT令牌
     * @param subject 主题，一般存用户id
     * @param claims  自定义载荷，存放username、role等非敏感数据
     * @return jwt token字符串
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims) //载荷
                .setSubject(subject) //存储用户id
                .setIssuedAt(new Date(now))          // 签发时间
                .setExpiration(new Date(now + expiration)) // 到期时间
                .signWith(getHmacKey(), SignatureAlgorithm.HS256) // 对称签名算法
                .compact();
    }

    /**
     * 解析+校验token
     * 校验内容：签名是否篡改、token是否过期
     * @param token 前端传过来的token
     * @return 载荷Claims，里面存我们放的数据
     * @throws ExpiredJwtException token过期
     * @throws SignatureException 签名错误(密钥不对/被篡改)
     * @throws MalformedJwtException token格式非法
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getHmacKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

