package com.dame.cn.beans.response;

import lombok.Getter;

/**
 * 自定义异常
 */
@Getter
public class BizException extends RuntimeException  {

    private ResultCode resultCode;

    public BizException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
