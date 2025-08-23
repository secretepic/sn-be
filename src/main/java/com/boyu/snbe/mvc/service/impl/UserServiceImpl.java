package com.boyu.snbe.mvc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boyu.snbe.mvc.entity.UserEntity;
import com.boyu.snbe.mvc.mapper.UserMapper;
import com.boyu.snbe.mvc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
