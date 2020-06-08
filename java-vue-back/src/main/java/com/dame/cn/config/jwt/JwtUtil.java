package com.dame.cn.config.jwt;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.utils.RsaUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
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
     * 使用RSA私钥，生成jwt的token
     *
     * @param id   userId
     * @param name username
     * @param map  其它需要保存的
     * @return token
     */
    public static String createJwt(String id, String name, Long expire, Map<String, Object> map) {
        try {
            // 1.设置失效时间
            long exp = expire;
            // 2.私钥
            PrivateKey privateKey = RsaUtils.getPrivateKey(jwtUtil.jwtProperties.privateKeyPath);
            // 3.创建jwtBuilder，并用私钥加密
            JwtBuilder jwtBuilder = Jwts.builder().setId(id).setSubject(name)
                    .setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.RS256, privateKey) // SignatureAlgorithm 需要是RSA的模式
                    .setExpiration(new Date(exp));
            // 4.根据map设置claims
            if (MapUtil.isNotEmpty(map)) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    jwtBuilder.claim(entry.getKey(), entry.getValue());
                }
            }
            // 5.创建token
            return jwtBuilder.compact();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(ResultCode.TOKENERROR);
        }

    }

    /**
     * 使用RSA公钥，解析token字符串获取clamis
     */
    public static Claims parseJwt(String token) {
        try {
            // 公钥
            PublicKey publicKey = RsaUtils.getPublicKey(jwtUtil.jwtProperties.publicKeyPath);
            // 公钥解密
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
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
