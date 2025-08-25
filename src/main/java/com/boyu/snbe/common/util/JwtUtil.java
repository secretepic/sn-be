package com.boyu.snbe.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
@Slf4j
public class JwtUtil {


    private String secret;

    private Long expiration;

    SecretKey hmacKey;

    @PostConstruct
    public void setKey() {
        hmacKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(hmacKey)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parser().verifyWith(hmacKey).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            // JWT过期
            log.error("JWT已过期");
        } catch (UnsupportedJwtException e) {
            // 不支持的JWT
            log.error("不支持的JWT");
        } catch (MalformedJwtException e) {
            // JWT格式错误
            log.error("JWT格式错误");
        } catch (SignatureException e) {
            // JWT签名不一致
            log.error("JWT签名不一致");
        } catch (IllegalArgumentException e) {
            // JWT为空或格式错误
            log.error("JWT为空或格式错误");
        }
        return null;
    }

    // 从令牌中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从令牌中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 从令牌中提取声明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有声明
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(hmacKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 验证令牌是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 验证令牌
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

}
