package com.dame.cn.config.shiro;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/17 11:02
 **/
@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    private static JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        jwtUtil = this;
        jwtUtil.jwtProperties = this.jwtProperties;
    }

    /**
     * 生成jwt的token
     *
     * @param id   userId
     * @param name username
     * @param map  其它需要保存的
     * @return token
     */
    public static String createJwt(String id, String name, Map<String, Object> map) {
        //1.设置失效时间
        long now = System.currentTimeMillis();//当前毫秒
        long exp = now + jwtUtil.jwtProperties.getTokenExpireTime() * 60 * 1000L;
        //2.创建jwtBuilder
        JwtBuilder jwtBuilder = Jwts.builder().setId(id).setSubject(name)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, jwtUtil.jwtProperties.getSecretKey())
                .setExpiration(new Date(exp));
        //3.根据map设置claims
        if (MapUtil.isNotEmpty(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jwtBuilder.claim(entry.getKey(), entry.getValue());
            }
        }
        //4.创建token
        return jwtBuilder.compact();
    }

    /**
     * 解析token字符串获取clamis
     */
    public static Claims parseJwt(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtUtil.jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new BizException(ResultCode.UNAUTH_EXPIRE);
        }
    }

    /**
     * 不校验token，单纯获取token的消息体内容
     */
    public static Map getPlayHold(String token) throws UnsupportedEncodingException {
        String playHold = token.substring(token.indexOf(".") + 1, token.lastIndexOf("."));
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] decode = base64UrlCodec.decode(playHold);
        String decodeStr = new String(decode, "utf-8");
        return JSON.parseObject(decodeStr, Map.class);
    }

}
