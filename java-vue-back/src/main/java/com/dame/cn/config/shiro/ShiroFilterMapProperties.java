package com.dame.cn.config.shiro;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author LYQ
 * @description 拦截器集合
 * @since 2020/4/20 21:52
 **/
@Data
@ConfigurationProperties(prefix = "shiro.filter.chain")
public class ShiroFilterMapProperties {

    /**
     * 拦截器链集合
     * key: path
     * value: filter
     */
    private List<Map<String,String>> perms;
}
