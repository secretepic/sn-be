package com.boyu.snbe.mvc.controller;

import com.boyu.snbe.common.service.RedisService;
import com.boyu.snbe.common.servlet.SnResponse;
import com.boyu.snbe.common.util.JwtUtil;
import com.boyu.snbe.mvc.entity.UserEntity;
import com.boyu.snbe.mvc.service.UserService;
import com.boyu.snbe.mvc.vo.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final RedisService redisService;

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private static final String JWT_REDIS_PREFIX = "jwt:token:";


    @RequestMapping("/list")
    public List<UserEntity> list() {
        return userService.list();
    }

    @PostMapping("/login")
    public SnResponse login(@RequestBody LoginVo loginVo) {
        // todo 生成一个加密解密的util   &  利用@Null notnull 等注解简化开发
        String username = loginVo.getUsername();
        String token = jwtUtil.generateToken(username);
        redisService.set(JWT_REDIS_PREFIX + username, token, 1800);
        SnResponse res = SnResponse.ok();
        res.put("token", token);
        return res;
    }

    @RequestMapping("/save")
    public boolean save() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("admin");
        userEntity.setPassword(passwordEncoder.encode("admin"));
        userEntity.setSalt("admin");
        userEntity.setStatus(1);
        userEntity.setCreator(0);
        userEntity.setUpdater(0);
        userEntity.setCreateTime(new Date());
        userEntity.setUpdateTime(new Date());
        return userService.save(userEntity);
    }
}
