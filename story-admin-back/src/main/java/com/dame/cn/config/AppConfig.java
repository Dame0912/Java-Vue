package com.dame.cn.config;

import com.dame.cn.utils.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description TODO
 * @Author lyq
 * @Date 2020/3/30 15:16
 **/
@Configuration
public class AppConfig {

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
