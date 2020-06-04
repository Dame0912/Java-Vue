package com.dame.cn.handle;


import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 自定义的公共异常处理器
 * 1.声明异常处理器
 * 2.对异常统一处理
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public Result exception(BizException e) {
        return new Result(e.getResultCode());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public Result exception(AccessDeniedException e) {
        return new Result(ResultCode.UNAUTHORISE);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exception(Exception e) {
        e.printStackTrace();
        return new Result(ResultCode.SERVER_ERROR);
    }

}
