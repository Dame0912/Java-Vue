package com.dame.cn.config.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/17 11:03
 **/
@ConfigurationProperties(prefix = "jwt.config")
@Data
public class JwtProperties {

    /**
     * token过期时间，单位分钟
     */
    Integer tokenExpireTime;

    /**
     * 更新令牌时间，单位分钟
     */
    Integer refreshCheckTime;

    /**
     * 私钥路径
     */
    String privateKeyPath;

    /**
     * 公钥路径
     */
    String publicKeyPath;

    /**
     * 生成密钥的密文(盐)
     */
    String keySecret;

    /**
     * 生成秘钥大小
     */
    Integer keySize;
}
