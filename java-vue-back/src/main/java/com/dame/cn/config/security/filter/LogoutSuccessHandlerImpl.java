package com.dame.cn.config.security.filter;

import com.dame.cn.beans.consts.JwtTokenConst;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.jwt.JwtTokenService;
import com.dame.cn.config.security.utils.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author LYQ
 * @description 退出成功执行
 * @since 2020-06-04
 **/
@Service
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Resource
    private JwtTokenService jwtTokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // SecurityContext 已经被清空
        // String userId = SecurityUtils.getUserId();
        try {
            String authorization = request.getHeader(JwtTokenConst.TOKEN_KEY);
            String token = authorization.replace(JwtTokenConst.TOKEN_PREFIX, "");
            jwtTokenService.delSysUserCache(token);
            SecurityUtils.responseResult(response, new Result(ResultCode.SUCCESS));
        }catch (Exception e) {
            SecurityUtils.responseResult(response, new Result(ResultCode.FAIL));
        }

    }
}
