package com.boyu.snbe.mvc.controller;

import com.boyu.snbe.mvc.entity.UserEntity;
import com.boyu.snbe.mvc.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @RequestMapping("/list")
    public List<UserEntity> list() {
        return userService.list();
    }

    @RequestMapping("/save")
    public boolean save() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("admin");
        userEntity.setPassword("admin");
        userEntity.setSalt("admin");
        userEntity.setStatus(1);
        userEntity.setCreator(0);
        userEntity.setUpdater(0);
        userEntity.setCreateTime(new Date());
        userEntity.setUpdateTime(new Date());
        return userService.save(userEntity);
    }
}
