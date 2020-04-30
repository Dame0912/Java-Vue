package com.dame.cn.handle;


import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义的公共异常处理器
 * 1.声明异常处理器
 * 2.对异常统一处理
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(HttpServletRequest request, HttpServletResponse response, Exception e) {
        e.printStackTrace();
        if (e.getClass() == BizException.class) {
            //类型转型
            BizException ce = (BizException) e;
            Result result = new Result(ce.getResultCode());
            return result;
        } else {
            Result result = new Result(ResultCode.SERVER_ERROR);
            return result;
        }
    }
}
