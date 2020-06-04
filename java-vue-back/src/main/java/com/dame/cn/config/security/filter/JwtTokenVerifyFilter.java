package com.dame.cn.config.security.filter;

import com.dame.cn.beans.consts.JwtTokenConst;
import com.dame.cn.beans.dto.SysUser;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.jwt.JwtTokenService;
import com.dame.cn.config.security.utils.SecurityUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author LYQ
 * @description Token校验过滤器
 * @since 2020-06-03
 **/
public class JwtTokenVerifyFilter extends BasicAuthenticationFilter {

    private final JwtTokenService jwtTokenService;

    public JwtTokenVerifyFilter(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        super(authenticationManager);
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * 过滤请求
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorization = request.getHeader(JwtTokenConst.TOKEN_KEY);
        try {
            SysUser sysUser;
            if (!StringUtils.isEmpty(authorization) && null != (sysUser = jwtTokenService.querySysUserCache(authorization.replace(JwtTokenConst.TOKEN_PREFIX, "")))) {
                if (!StringUtils.isEmpty(sysUser.getToken())) {
                    response.setHeader(JwtTokenConst.TOKEN_KEY, sysUser.getToken());
                }
                UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(sysUser, null, sysUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } else {
                SecurityUtils.responseResult(response, new Result(ResultCode.UNAUTH_EXPIRE));
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            SecurityUtils.responseResult(response, new Result(ResultCode.UNAUTH_EXPIRE));
        }
    }

}
