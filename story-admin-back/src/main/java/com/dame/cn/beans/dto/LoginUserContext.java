package com.dame.cn.beans.dto;

import com.dame.cn.beans.entities.User;

/**
 * @author LYQ
 * @description 登陆用户上下文信息
 * @since 2020/4/24 10:57
 **/
public class LoginUserContext implements AutoCloseable {

    private static final ThreadLocal<LoginUser> current = new ThreadLocal<>();

    public LoginUserContext(LoginUser loginUser) {
        current.set(loginUser);
    }

    public static LoginUser getCurrentUser() {
        return current.get();
    }

    public static void setCurrentUser(LoginUser loginUser) {
        current.set(loginUser);
    }

    @Override
    public void close() {
        current.set(null);
    }
}
