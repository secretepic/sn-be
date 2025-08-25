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

                // 检查Redis中是否存在该token
//                if (redisService.hasKey(username) && jwt.equals(redisService.get(username))) {
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
                // 验证JWT是否有效且与Redis中存储的一致
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null &&
                        redisJwt != null && redisJwt.equals(jwt) && jwtUtils.validateToken(jwt, username)) {

                    // 5. 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 6. 设置认证信息
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

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