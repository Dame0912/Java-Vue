package com.dame.cn.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author LYQ
 * @description 使用shiro认证jwt，参考 UsernamePasswordToken
 * @since 2020/4/20 22:10
 **/
public class JwtAuthenticationToken implements AuthenticationToken {

    private String token;

    public JwtAuthenticationToken(String token) {
        this.token = token;
    }

    // 主体
    @Override
    public Object getPrincipal() {
        return this.token;
    }

    // 密码
    @Override
    public Object getCredentials() {
        return this.token;
    }
}
