package com.dame.cn.config;

import com.dame.cn.config.web.filter.LoginUserResponseFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LYQ
 * @description Web主配置
 * @since 2020/4/26 8:43
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 注册 跨域 过滤器
     * <p>
     * 在 SpringBoot 中解决跨域有好几种方式，比如：
     * * ①使用 @CrossOrigin 注解
     * * ②实现 WebMvcConfigurer，然后重写它的 addCorsMappings 方法。
     * * 这两种方式在 SpringBoot 中都能解决跨域的问题，但是在整合shiro后，跨域就失效了。
     * 原因是：shiro的过滤器会在跨域处理之前执行，这就导致未允许跨域的请求先到达shiro过滤器，这样就会出现跨域错误。
     * 解决：使用过滤器，并且在其它过滤器Filter之前执行，即 Order 足够小，
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 放行哪些原始域
        config.addAllowedOrigin("*");
        // 是否发送Cookie信息
        config.setAllowCredentials(true);
        // 放行哪些原始域
        config.addAllowedOrigin("*");
        // 放行哪些原始域(头部信息)
        config.addAllowedHeader("*");
        // 放行哪些原始域(请求方式)
        config.addAllowedMethod("*");
        // 配置预检的有效时长，在时长内无需再次校验，单位为秒。如果请求的URL不同，即使参数不同，也会预检查。
        // 前后端项目，一般跨域，浏览器会先发送一个OPTION请求，通过了再发送真正的请求。为了不让每次都预检。
        config.setMaxAge(3600L);
        // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        // config.addExposedHeader("a");
        // config.addExposedHeader("b");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public FilterRegistrationBean loginUserResponseFilter() {
        FilterRegistrationBean<LoginUserResponseFilter> bean = new FilterRegistrationBean<>(new LoginUserResponseFilter());
        bean.addUrlPatterns("/*");
        return bean;
    }
}
