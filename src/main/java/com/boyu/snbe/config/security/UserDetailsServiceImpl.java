package com.boyu.snbe.config.security;

import com.alibaba.fastjson2.JSONObject;
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
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_USER_PREFIX = "user:info:";
    private static final long USER_CACHE_EXPIRATION = 30 * 60;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先从Redis查询用户
        String userStr = redisTemplate.opsForValue().get(REDIS_USER_PREFIX + username);
        UserEntity user = JSONObject.parseObject(userStr, UserEntity.class);
        if (user == null || user.getId() == null) {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", username);
            user = userMapper.selectOne(queryWrapper);
            Objects.requireNonNull(user, "用户不存在");
            // 查询到的用户信息存入Redis
            redisTemplate.opsForValue().set(
                    REDIS_USER_PREFIX + username,
                    JSONObject.toJSONString(user),
                    USER_CACHE_EXPIRATION,
                    TimeUnit.MINUTES
            );
        }
        return UserPrincipal.build(user);
    }
}
