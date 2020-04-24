package com.dame.cn.handle;


import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义的公共异常处理器
 * 1.声明异常处理器
 * 2.对异常统一处理
 */
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    /**
     * 权限异常。
     * 如果用户权限不足，shiro会抛出该权限异常，方便前端获取消息体的内容，修改返回的结果。
     *
     * 说明：认证失败。已经在 ShiroJwtFilter中 处理了。这里不捕获。
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result handleUnauthorizedException(HttpServletRequest request, HttpServletResponse response, UnauthorizedException e) {
        return new Result(ResultCode.UNAUTHORISE);
    }

    @ExceptionHandler(BizException.class)
    public Result handleBizException(BizException e) {
        return new Result(e.getResultCode());
    }

    @ExceptionHandler(value = Exception.class)
    public Result error(Exception e) {
        e.printStackTrace();
        return new Result(ResultCode.SERVER_ERROR);
    }
}
