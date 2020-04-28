package com.dame.cn.config.shiro;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.dame.cn.beans.consts.JwtTokenConst;
import com.dame.cn.beans.consts.OneByOneConstant;
import com.dame.cn.beans.consts.RedisConst;
import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.jwt.JwtProperties;
import com.dame.cn.config.jwt.JwtUtil;
import com.dame.cn.config.redis.RedisClient;
import com.dame.cn.onebyone.OneByOne;
import com.dame.cn.onebyone.OneByOneService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

/**
 * @author LYQ
 * @description 自定义的 判断是否登陆的 filter， 类似 authc
 * @since 2020/4/20 21:37
 **/
@Slf4j
public class ShiroJwtFilter extends BasicHttpAuthenticationFilter {

    private RedisClient redisClient;
    private OneByOneService oneByOneService;
    private JwtProperties jwtProperties;
    private DefaultWebSecurityManager securityManager;

    public ShiroJwtFilter(RedisClient redisClient, OneByOneService oneByOneService, JwtProperties jwtProperties, SecurityManager securityManager) {
        this.redisClient = redisClient;
        this.oneByOneService = oneByOneService;
        this.jwtProperties = jwtProperties;
        this.securityManager = (DefaultWebSecurityManager) securityManager;
    }

    /**
     * 过滤器入口，判断请求是否放行
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (this.isLoginAttempt(request, response)) {
            try {
                return this.executeLogin(request, response);
            } catch (Exception e1) {
                log.error("ShiroJwtFilter.isAccessAllowed 异常:{}", e1.getMessage());
                // 捕获异常，返回false，执行认证失败方法，即下面的 onAccessDenied 方法
                return false;
            }
        }
        return false; // 请求头没有token，肯定没有认证
    }

    /**
     * 真正判断是否认证成功
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(JwtTokenConst.TOKEN_KEY);
        String jwtToken = token.replace(JwtTokenConst.TOKEN_PREFIX, "");
        // 交给自定义的Realm, 进行认证。 如果失败，会抛异常，在 isAccessAllowed() 中捕获处理
        getSubject(request, response).login(new JwtAuthenticationToken(jwtToken));

        // 将登陆人信息放入ThreadLocal,方便使用
        new LoginUserContext(jwtToken);

        // 检查是否需要更换token，需要则更换
        this.refreshToken(jwtToken, response);

        return true;
    }

    /**
     * 判断请求是否已经登陆，通过请求头是否有token先粗略检查
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwtToken = httpServletRequest.getHeader(JwtTokenConst.TOKEN_KEY);
        return !StringUtils.isEmpty(jwtToken);
    }


    /**
     * 父类失败要走的方法
     *
         protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
            boolean loggedIn = false; //false by default or we wouldn't be in this method
            if (isLoginAttempt(request, response)) {
                loggedIn = executeLogin(request, response);
            }
            if (!loggedIn) {
                sendChallenge(request, response);
            }
            return loggedIn;
         }
     */

    /**
     * 重写，认证失败走的方法，避免父类中调用再次executeLogin
     * <p>
     * 构建 200 返回码，方便前端处理。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 清理redis缓存
        this.clearCache(request);

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        Result result = new Result(ResultCode.UNAUTH_EXPIRE);
        try (PrintWriter writer = httpServletResponse.getWriter()) {
            writer.write(JSON.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清理 Redis缓存
     */
    private void clearCache(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(JwtTokenConst.TOKEN_KEY);
        String jwtToken = token.replace(JwtTokenConst.TOKEN_PREFIX, "");
        CacheManager cacheManager = securityManager.getCacheManager();

        UserPrincipalInfo userPrincipalInfo = new UserPrincipalInfo(jwtToken);

        Collection<Realm> realms = securityManager.getRealms();
        for (Realm realm : realms) {
            if (realm instanceof AuthorizingRealm) {
                AuthorizingRealm authorizingRealm = (AuthorizingRealm) realm;
                // 获取缓存名称
                String authorizationCacheName = authorizingRealm.getAuthorizationCacheName();
                Cache<Object, Object> ss = cacheManager.getCache(authorizationCacheName);
                // 获取缓存KEY
                ss.remove(userPrincipalInfo.getId());
            }
        }
    }

    /**
     * 检查是否需要更换token，需要则更换
     * <p>
     * 1、增加Redis，并且增加 redis 与 token 值校验，增加了安全性。
     *    因为：如果只校验token，那么非法持有token的用户，在一定情况下将一直有效(refreshToken)
     * 2、一定程度限制一个账号只能一个用户登陆，另一个用户登陆会挤掉当前用户
     *    特例：token没有到达needRefresh()，即不用刷新，那么将继续可以使用。
     *    解决：将 redis 与 token 值校验, 放到 needRefresh() 前。
     *    新问题：每次都检查 redis和token，影响效率。而且这种漏洞正常业务也能满足。如果想要真正的安全，还是要使用 HTTPS
     */
    private void refreshToken(String jwtToken, ServletResponse response) {
        // 当前时间
        Long currentTimeMillis = System.currentTimeMillis();
        // token中时间
        String tokenMillis = JwtUtil.parseJwt(jwtToken).get(JwtTokenConst.CURRENT_TIME_MILLIS, String.class);
        // 判断是否需要重新生成token
        if (needRefresh(tokenMillis, currentTimeMillis)) {
            Claims claims = JwtUtil.parseJwt(jwtToken);
            String userId = claims.getId();
            String username = claims.getSubject();
            try {
                // 并发控制，因为页面有时候会同时发送多个请求。
                // 如果没做处理，前面的请求修改了redis，后面的请求都会失败。
                // 所以如果并发请求，只会有一个修改redis，剩下的默认成功。
                oneByOneService.execute(new OneByOne(OneByOneConstant.TOKEN_REFRESH_BIZTYPE, userId, OneByOneConstant.TOKEN_REFRESH_METHOD), () -> {
                    // 获取redis中的值
                    String redisTokenKey = RedisConst.JWT_TOKEN_TIME + userId;
                    String redisMillis = redisClient.get(redisTokenKey);
                    // 判断redis和token中放入的时间戳是否一致，其实也算是合法性的校验。
                    // 如果只校验token，那么非法持有的token，一直会有效(有效期内并请求又获取新token)。
                    if (StringUtils.isEmpty(redisMillis) || !tokenMillis.equals(redisMillis)) {
                        throw new BizException(ResultCode.UNAUTH_EXPIRE);
                    }

                    // 更新redis中的时间戳
                    redisClient.setex(redisTokenKey, jwtProperties.getTokenExpireTime() * 60, String.valueOf(currentTimeMillis));

                    // 重新生成token
                    Map<String, Object> map = MapUtil.of(JwtTokenConst.CURRENT_TIME_MILLIS, String.valueOf(currentTimeMillis));
                    String newToken = JwtUtil.createJwt(userId, username, map);

                    // 将新token放入响应头
                    HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
                    httpServletResponse.setHeader(JwtTokenConst.TOKEN_KEY, newToken);

                    return null;
                });
            } catch (BizException e) {
                log.info("并发控制：{}", e.getMessage());
            }
        }
    }


    /**
     * 判断是否要更新token，防止频繁更新
     *
     * @param tokenMillis       token生成时间
     * @param currentTimeMillis 当前时间
     * @return boolean
     */
    private boolean needRefresh(String tokenMillis, Long currentTimeMillis) {
        // 判断token签发时间，是否超过了需要刷新的时间
        return (currentTimeMillis - Long.parseLong(tokenMillis)) > this.jwtProperties.getRefreshTokenTime() * 60 * 1000L;
    }
}
