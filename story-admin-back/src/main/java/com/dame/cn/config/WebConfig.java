package com.dame.cn.config;

import com.dame.cn.config.web.filter.LoginUserContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LYQ
 * @description web相关配置
 * @since 2020/4/24 10:53
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {


    /**
     * 注册拦截器
     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//    }

    /**
     * 注册监听器
     */
//    @Bean
//    public ServletListenerRegistrationBean servletListenerRegistrationBean() {
//        ServletListenerRegistrationBean srb = new ServletListenerRegistrationBean();
//        return srb;
//    }

    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new LoginUserContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

}
