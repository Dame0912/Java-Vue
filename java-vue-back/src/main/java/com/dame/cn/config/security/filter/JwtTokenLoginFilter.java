package com.dame.cn.config.security.filter;

import com.dame.cn.beans.consts.JwtTokenConst;
import com.dame.cn.beans.dto.SysUser;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.jwt.JwtTokenService;
import com.dame.cn.config.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author LYQ
 * @description 登录认证过滤器
 * @since 2020-06-03
 **/
@Slf4j
public class JwtTokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    /**
     * public UsernamePasswordAuthenticationFilter() {
     *      super(new AntPathRequestMatcher("/login", "POST"));
     * }
     * 默认登录URL 就是 /login
     */
    public JwtTokenLoginFilter(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * 尝试认证
     * public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
     *      super(null);
     *      this.principal = principal;
     *      this.credentials = credentials;
     *      setAuthenticated(false);  // 没有认证的标识
     * }
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(mobile, password);
        return authenticationManager.authenticate(authentication);
    }

    /**
     * 认证失败
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityUtils.responseResult(response, new Result(ResultCode.MOBILEORPASSWORDERROR));
    }

    /**
     * 认证成功
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SysUser sysUser = (SysUser) authResult.getPrincipal();
        String token = jwtTokenService.createToken(sysUser);
        response.addHeader(JwtTokenConst.TOKEN_KEY, token);
        SecurityUtils.responseResult(response, new Result(ResultCode.SUCCESS));
    }

}
