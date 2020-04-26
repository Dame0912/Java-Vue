package com.dame.cn.config;

import com.dame.cn.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LYQ
 * @description 应用配置
 * @since 2020/4/26 8:43
 **/
@Configuration
public class AppConfig {

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
