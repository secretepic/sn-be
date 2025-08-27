package com.boyu.snbe.mvc.controller;

import com.boyu.snbe.common.service.RedisService;
import com.boyu.snbe.common.servlet.SnResponse;
import com.boyu.snbe.common.util.JwtUtil;
import com.boyu.snbe.mvc.entity.UserEntity;
import com.boyu.snbe.mvc.service.UserService;
import com.boyu.snbe.mvc.vo.LoginVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final AuthenticationManager authenticationManager;


    private final PasswordEncoder passwordEncoder;

    private static final String JWT_REDIS_PREFIX = "jwt:token:";


    @RequestMapping("/list")
    public List<UserEntity> list() {
        return userService.list();
    }

    /**
     *   登录核心流程
     *   Authentication authentication = authenticationManager.authenticate(upToken);
     *   这个地方会调用DaoAuthenticationProvider，就是securityConfig里配置的
     *   先调用loadUserByUsername方法，会得到一个UserDetails
     *   紧接着会调用DaoAuthenticationProvider的additionalAuthenticationChecks方法
     *   这个方法里有一行if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword()))
     *   presentedPassword这个是来源于 new UsernamePasswordAuthenticationToken(username, password);
     *   这里会根据BCryptPasswordEncoder这个对象来加密vo里的密码，和数据库的对比，如果一致则返回true
     */
    @PostMapping("/login")
    public SnResponse login(@RequestBody LoginVo loginVo) {
        // todo 生成一个加密解密的util   &  利用@Null notnull 等注解简化开发
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
        // 这个地方用BCryptPasswordEncoder自动随机加盐并且单向加密，无法解密，所以不需要设计盐值这个字段了
//        userEntity.setSalt("admin");
        userEntity.setStatus(1);
        userEntity.setCreator(0);
        userEntity.setUpdater(0);
        userEntity.setCreateTime(new Date());
        userEntity.setUpdateTime(new Date());
        return userService.save(userEntity);
    }
}
