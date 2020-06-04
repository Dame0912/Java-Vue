package com.dame.cn.config.jwt;

import cn.hutool.core.map.MapUtil;
import com.dame.cn.beans.dto.SysUser;
import com.dame.cn.config.redis.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author LYQ
 * @description TODO
 * @since 2020-06-03
 **/
@Service
public class JwtTokenService {

    @Resource
    private RedisCache redisCache;
    @Resource
    private JwtProperties jwtProperties;

    private static final Long MILLIS_MINUTE = 60 * 1000L;
    private static final String user_prefix = "login_user:";
    private static final String token_generate_time = "tokenGenerateTime";

    /**
     * 生成Token，并保存用户数据至Redis
     */
    public String createToken(SysUser sysUser) {
        sysUser.setTokenGenerateTime(System.currentTimeMillis());
        sysUser.setTokenRefreshTime(sysUser.getTokenGenerateTime() + jwtProperties.getTokenExpireTime() * MILLIS_MINUTE);
        String redisKey = user_prefix + sysUser.getId();
        sysUser.setToken("");
        sysUser.setPassword("");
        redisCache.setCacheObject(redisKey, sysUser, jwtProperties.getTokenExpireTime(), TimeUnit.MINUTES);
        HashMap<String, Object> map = MapUtil.of(token_generate_time, sysUser.getTokenGenerateTime());
        return JwtUtil.createJwt(sysUser.getId(), sysUser.getUsername(), sysUser.getTokenRefreshTime(), map);
    }

    /**
     * 查询用户信息，并判断是否需要刷新Token
     */
    public SysUser querySysUserCache(String token) {
        Claims claims = JwtUtil.parseJwt(token);
        String userId = claims.getId();
        Long tokenGenerateTime = (Long) claims.get(token_generate_time);
        String redisKey = user_prefix + userId;
        final SysUser sysUser = redisCache.getCacheObject(redisKey);
        if (null == sysUser || !tokenGenerateTime.equals(sysUser.getTokenGenerateTime())) { //保证只能有一个用户登录
            return null;
        }
        refreshToken(sysUser);
        return sysUser;
    }

    /**
     * 刷新Token
     */
    public void refreshToken(SysUser sysUser) {
        if (redisCache.lock(sysUser.getId())) { // 防止页面一下有多个请求同时过来并发
            try {
                Long tokenRefreshTime = sysUser.getTokenRefreshTime();
                Long currentTime = System.currentTimeMillis();
                if (tokenRefreshTime - currentTime <= jwtProperties.getRefreshCheckTime() * MILLIS_MINUTE) {
                    sysUser.setToken(createToken(sysUser));
                }
            } finally {
                redisCache.unlock(sysUser.getId());
            }
        }
    }

    /**
     * 删除用户缓存数据
     */
    public void delSysUserCache(String token){
        Claims claims = JwtUtil.parseJwt(token);
        String userId = claims.getId();
        String redisKey = user_prefix + userId;
        redisCache.deleteObject(redisKey);
    }
}
