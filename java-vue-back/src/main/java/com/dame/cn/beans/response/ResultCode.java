package com.dame.cn.beans.response;

/**
 * 公共的返回码
 */
public enum ResultCode {

    SUCCESS(true, 10000, "操作成功！"),

    //---系统错误返回码-----
    FAIL(false, 10001, "操作失败"),
    MULTIPLE_INVOKE_ERROR(false, 99998, "业务正在处理中！"),
    SERVER_ERROR(false, 99999, "抱歉，系统繁忙，请稍后重试！"),

    //---用户操作返回码  2xxxx----
    MOBILE_OR_PASSWORD_ERROR(false, 20001, "用户名或密码错误"),
    PWD_ERROR(false, 20002, "密码错误"),
    UNAUTH_EXPIRE(false, 20003, "登陆失效"),
    UNAUTHENTICATED(false, 20004, "您还未登录"),


    //---企业操作返回码  3xxxx ----
    //---权限操作返回码 4xxxx ----
    UNAUTHORISE(false, 40001, "权限不足");
    //---其他操作返回码----

    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;

    ResultCode(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
