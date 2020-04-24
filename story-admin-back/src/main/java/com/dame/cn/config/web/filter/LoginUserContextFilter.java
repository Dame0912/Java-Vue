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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author LYQ
 * @description 保存登陆人信息至 ThreadLocal
 * @since 2020/4/24 11:06
 **/
@Slf4j
public class LoginUserContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        handleCross(response);
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

    // 处理跨域，因为filter在controller之前，所以@CrossOrigin在这不会起作用
    private void handleCross(ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
    }

    @Override
    public void destroy() {

    }
}
