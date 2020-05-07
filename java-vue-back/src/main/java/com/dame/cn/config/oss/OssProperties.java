package com.dame.cn.config.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author LYQ
 * @description 阿里云OSS配置属性
 * @since 2020/4/28 15:00
 **/
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {

    /**
     * 区域
     */
    private String endpoint;

    /**
     * 密钥ID
     */
    private String accessKeyId;

    /**
     * 密钥
     */
    private String accessKeySecret;

    /**
     * 使用的 Bucket
     */
    private String bucketName;

    /**
     * 文件存放的根目录，自定义的，方便文件的管理
     */
    private String fileHost;

    /**
     * 签名过期时间（秒）
     */
    private Integer expire;

    /**
     * 最大允许（MB）
     */
    private Integer maxSize;

    /**
     * Excel文件存放的根目录
     */
    private String excelHost;
}
