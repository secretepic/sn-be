package com.boyu.snbe.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.boyu.snbe.mvc.entity.UserEntity;
import com.boyu.snbe.mvc.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_USER_PREFIX = "user:info:";
    // 用户信息在Redis中的过期时间(分钟)
    private static final long USER_CACHE_EXPIRATION = 30;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先从Redis查询用户
        UserEntity user = (UserEntity) redisTemplate.opsForValue().get(REDIS_USER_PREFIX + username);
        if (user == null) {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", username);
            user = userMapper.selectOne(queryWrapper);
            Objects.requireNonNull(user, "用户不存在");
            // 查询到的用户信息存入Redis
            redisTemplate.opsForValue().set(
                    REDIS_USER_PREFIX + username,
                    user,
                    USER_CACHE_EXPIRATION,
                    TimeUnit.MINUTES
            );
        }
        return UserPrincipal.build(user);
    }
}
