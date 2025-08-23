package com.boyu.snbe.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
@Slf4j
public class JwtUtil {

    private String secret;

    private Long expiration;

    SecretKey hmacKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

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

}
