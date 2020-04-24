package com.dame.cn.config.web.filter;

import com.alibaba.fastjson.JSON;
import com.dame.cn.beans.consts.JwtTokenConst;
import com.dame.cn.beans.dto.LoginUser;
import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author LYQ
 * @description 验证Token，并保存登陆人信息至 ThreadLocal
 * @since 2020/4/24 11:06
 **/
@Slf4j
public class JwtTokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (httpServletRequest.getRequestURI().equals("/sys/user/login")) {
            chain.doFilter(request, response);
        } else {
            String authorization = httpServletRequest.getHeader(JwtTokenConst.TOKEN_KEY);
            if (!StringUtils.isEmpty(authorization)) {
                String userId;
                String username;
                try {
                    Claims claims = JwtUtil.parseJwt(authorization.replace(JwtTokenConst.TOKEN_PREFIX, ""));
                    userId = claims.getId();
                    username = claims.getSubject();
                } catch (Exception e) {
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    Result result = new Result(ResultCode.UNAUTH_EXPIRE);
                    writer.append(JSON.toJSONString(result));
                    return;
                }
                try (LoginUserContext loginUserContext = new LoginUserContext(new LoginUser(userId, username))) {
                    chain.doFilter(request, response);
                }
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                Result result = new Result(ResultCode.UNAUTHENTICATED);
                writer.append(JSON.toJSONString(result));
                return;
            }
        }

    }

    @Override
    public void destroy() {

    }
}
