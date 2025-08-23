package com.boyu.snbe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.boyu.snbe.mvc.mapper")
public class SnBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnBeApplication.class, args);
    }

}
