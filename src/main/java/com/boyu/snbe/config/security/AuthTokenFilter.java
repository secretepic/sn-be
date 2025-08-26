package com.boyu.snbe.config.security;

import com.boyu.snbe.common.service.RedisService;
import com.boyu.snbe.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * springSecurity的扩展点
 * 自己实现一个Filter效果是一样的，官网推荐这种方式，更灵活
 * 保证这个过滤器只会被调用一次，就算是内部有转发或者重定向等等，在req到req的过程中，只会调用一次
 */
@Slf4j
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisService redisService;
    private static final String JWT_REDIS_PREFIX = "jwt:token:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            // 检查用户是否已认证
            if (jwt != null) {
                Jws<Claims> claimsJws = jwtUtils.validateToken(jwt);
                String username = claimsJws.getPayload().getSubject();
                // 从Redis中获取存储的JWT
                String redisJwt = redisService.get(JWT_REDIS_PREFIX + username);

                // 验证JWT是否有效且与Redis中存储的一致, 并且用户名与JWT中的用户名一致，如果任何一个没满足就会返回401
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null &&
                        redisJwt != null && redisJwt.equals(jwt) && jwtUtils.validateToken(jwt, username)) {

                    // 加载用户信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 设置认证信息
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 这里是给后续的过滤器提供认证信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}