package com.dame.cn;

import com.dame.cn.config.jwt.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@MapperScan("com.dame.cn.mapper")
@SpringBootApplication
@EnableConfigurationProperties(value = {JwtProperties.class})
public class StoryAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoryAdminApplication.class, args);
    }

}
