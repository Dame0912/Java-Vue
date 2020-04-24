package com.dame.cn.config.shiro;

import com.alibaba.fastjson.JSON;
import com.dame.cn.beans.consts.JwtTokenConst;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author LYQ
 * @description 自定义的 判断是否登陆的 filter， 类似 authc
 * @since 2020/4/20 21:37
 **/
@Slf4j
public class ShiroJwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 过滤器入口，判断请求是否放行
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        log.info("shiroJwtFilter 开始拦截。。。路径：{}。。。", WebUtils.toHttp(request).getRequestURI());
        if (this.isLoginAttempt(request, response)) {
            try {
                return this.executeLogin(request, response);
            }catch (Exception e1) {
                log.error("ShiroJwtFilter.isAccessAllowed 异常:{}",e1.getMessage());
                // 捕获异常，返回false，执行认证失败方法，即下面的 onAccessDenied 方法，
                return false;
            }
        }
        return false; // 请求头没有token，肯定没有认证
    }

    /**
     * 真正判断是否登陆成功
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(JwtTokenConst.TOKEN_KEY);
        String jwtToken = token.replace(JwtTokenConst.TOKEN_PREFIX, "");
        // 交给自定义的Realm, 进行认证。 如果失败，会抛异常，在 isAccessAllowed() 中捕获处理
        getSubject(request, response).login(new JwtAuthenticationToken(jwtToken));

        return true;
    }

    /**
     * 判断请求是否已经登陆，通过请求头是否有token先粗略检查
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwtToken = httpServletRequest.getHeader(JwtTokenConst.TOKEN_KEY);
        return !StringUtils.isEmpty(jwtToken);
    }


    /**
     * 父类失败要走的方法
     *
        protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
            boolean loggedIn = false; //false by default or we wouldn't be in this method
            if (isLoginAttempt(request, response)) {
                loggedIn = executeLogin(request, response);
            }
            if (!loggedIn) {
                sendChallenge(request, response);
            }
            return loggedIn;
        }
     */

    /**
     * 重写，认证失败走的方法，避免父类中调用再次executeLogin
     *
     * 构建 200 返回码，方便前端处理。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        Result result = new Result(ResultCode.UNAUTH_EXPIRE);
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            writer.write(JSON.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
