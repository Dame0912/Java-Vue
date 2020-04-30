package com.dame.cn.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/21 10:14
 **/
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.timeout:10000}")
    private int timeout;

    @Bean
    public JedisPool jedisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 总数
        jedisPoolConfig.setMaxTotal(200);
        // 获取连接时等待的最大毫秒数
        jedisPoolConfig.setMaxWaitMillis(10 * 1000);
        // 最小剩余数
        jedisPoolConfig.setMaxIdle(10);
        // 如果到最大数，是否阻塞
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 再获取连接时，检查是否有效
        jedisPoolConfig.setTestOnBorrow(true);
        return new JedisPool(jedisPoolConfig, host, port, timeout, null, database);
    }
}
