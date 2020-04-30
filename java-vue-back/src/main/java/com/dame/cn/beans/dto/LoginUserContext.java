package com.dame.cn.beans.dto;

import com.dame.cn.config.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/26 9:26
 **/
@Slf4j
public class LoginUserContext implements AutoCloseable {

    private static final ThreadLocal<LoginUser> current = new ThreadLocal<>();

    public LoginUserContext(String userId, String username) {
        LoginUser loginUser = new LoginUser(userId, username);
        setCurrentUser(loginUser);
    }

    public LoginUserContext(String token) {
        Claims claims = JwtUtil.parseJwt(token);
        LoginUser loginUser = new LoginUser(claims.getId(), claims.getSubject());
        setCurrentUser(loginUser);
    }

    public static LoginUser getCurrentUser() {
        return current.get();
    }

    public static void setCurrentUser(LoginUser loginUser) {
        current.set(loginUser);
    }

    // 没法使用 AutoCloseable 时， 手动清理
    public static void remove(){
        current.set(null);
    }

    @Override
    public void close() throws Exception {
        current.set(null);
        //current.remove();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginUser {
        private String userId;
        private String username;
    }
}
