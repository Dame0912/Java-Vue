package com.dame.cn.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LYQ
 * @description jwt相关配置项。
 * @since 2020/4/17 11:03
 **/
@ConfigurationProperties(prefix = "jwt.config")
@Data
public class JwtProperties {

    /**
     * token过期时间，单位分钟。一般该时间长于更新令牌时间。如：24H
     */
    Integer tokenExpireTime;

    /**
     * 更新令牌时间，单位分钟。一般该时间短于更新令牌时间。如：2H
     */
    Integer refreshTokenTime;

    /**
     * token加密密钥
     */
    String secretKey;
}
