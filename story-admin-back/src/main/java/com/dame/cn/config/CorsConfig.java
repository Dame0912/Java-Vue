package com.dame.cn.config;

import com.dame.cn.beans.consts.JwtTokenConst;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author LYQ
 * @description 跨域配置
 * @since 2020/4/22 8:52
 * <p>
 * 在 Springboot 中解决跨域有好几种方式，比如：
 * ①使用 @CrossOrigin 注解
 * ②实现 WebMvcConfigurer，然后重写它的 addCorsMappings 方法。
 * 这两种方式在 springboot 中都能解决跨域的问题，但是在整合shiro后，跨域就失效了。
 * <p>
 * 原因是：shiro的过滤器会在跨域处理之前执行，这就导致未允许跨域的请求先到达shiro过滤器，这样就会出现跨域错误。
 * <p>
 * 解决方案
 * 使用Filter的方式解决跨域
 **/
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        // 1.1 放行哪些原始域
        config.addAllowedOrigin("*");
        // 1.2 是否发送Cookie信息
        config.setAllowCredentials(true);
        // 1.3 放行哪些原始域(请求方式)
        config.addAllowedMethod("*");
        // 1.4 放行哪些原始域(头部信息)
        config.addAllowedHeader("*");
        // 1.5 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        config.addExposedHeader("content-type");
        config.addExposedHeader("Content-Range");
        // 也可以在每个请求中使用 response.setHeader("Access-Control-Expose-Headers", JwtTokenConst.TOKEN_KEY);
        config.addExposedHeader(JwtTokenConst.TOKEN_KEY);

        //2.添加映射路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        //3.返回新的CorsFilter
        return new CorsFilter(source);
    }
}
