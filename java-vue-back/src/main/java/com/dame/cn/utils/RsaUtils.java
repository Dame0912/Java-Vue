package com.dame.cn.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author LYQ
 * @description RSA工具类
 * @since 2020-06-08
 **/
@Slf4j
public class RsaUtils {
    private static final int DEFAULT_KEY_SIZE = 2048;

    /**
     * 从文件中读取公钥
     *
     * @param filename 公钥保存路径
     * @return 公钥对象
     */
    public static PublicKey getPublicKey(String filename) throws Exception {
        byte[] bytes = FileUtil.readBytes(filename);
        byte[] decode = Base64.decode(bytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * 从文件中读取私钥
     *
     * @param filename 私钥保存路径
     * @return 私钥对象
     */
    public static PrivateKey getPrivateKey(String filename) throws Exception {
        byte[] bytes = FileUtil.readBytes(filename);
        byte[] decode = Base64.decode(bytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 根据密文，生存rsa公钥和私钥,并写入指定文件
     *
     * @param publicKeyFilename  公钥文件路径
     * @param privateKeyFilename 私钥文件路径
     * @param secret             生成密钥的密文
     * @param keySize            秘钥大小
     */
    public static void generateKey(String publicKeyFilename, String privateKeyFilename, String secret, int keySize) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
        // 使用给定的随机源（和默认参数集）初始化特定密钥大小的密钥对生成器。
        keyPairGenerator.initialize(Math.max(keySize, DEFAULT_KEY_SIZE), secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 获取公钥并写出
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String publicKeyStr = Base64.encode(publicKeyBytes);
        FileUtil.writeBytes(publicKeyStr.getBytes(), publicKeyFilename);
        // 获取私钥并写出
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        String privateKeyStr = Base64.encode(privateKeyBytes);
        FileUtil.writeBytes(privateKeyStr.getBytes(), privateKeyFilename);
        log.info("生成公私钥结束");
    }
}
