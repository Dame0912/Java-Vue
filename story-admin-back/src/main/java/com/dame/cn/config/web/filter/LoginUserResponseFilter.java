package com.dame.cn.config.web.filter;

import com.dame.cn.beans.dto.LoginUserContext;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author LYQ
 * @description 清理 LoginUser的 ThreadLocal
 * @since 2020/4/26 9:54
 **/
public class LoginUserResponseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        // 清理ThreadLocal中的内容
        LoginUserContext.remove();
    }

    @Override
    public void destroy() {

    }
}
