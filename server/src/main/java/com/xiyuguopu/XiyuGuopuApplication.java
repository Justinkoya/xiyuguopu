package com.xiyuguopu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 西域果铺 — 后端 API 服务
 */
@SpringBootApplication
@MapperScan("com.xiyuguopu.mapper")
public class XiyuGuopuApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiyuGuopuApplication.class, args);
    }
}
