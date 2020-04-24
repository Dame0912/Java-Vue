package com.dame.cn.config.shiro;

import com.dame.cn.config.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crazycake.shiro.AuthCachePrincipal;

import java.io.Serializable;

/**
 * @author LYQ
 * @description So please use AuthCachePrincipal to tell shiro-redis how to get the cache key
 *              目的就是为了告诉redis如何获取 key
 * @since 2020/4/22 16:56
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipalInfo implements AuthCachePrincipal, Serializable {

    private String token;

    @Override
    public String getAuthCacheKey() {
        return JwtUtil.parseJwt(this.token).getId();
    }
}
