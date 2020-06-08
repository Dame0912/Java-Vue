package com.dame.cn;

import com.dame.cn.utils.RsaUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

/**
 * @author LYQ
 * @description 生成公私钥
 * @since 2020-06-08
 **/
public class GenerateRsaKey {

    public static void main(String[] args) {
        try {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new ClassPathResource("application.yml"));
            // 公钥文件路径
            String publicKeyFilename = (String) yaml.getObject().get("jwt.config.public-key-path");
            // 私钥文件路径
            String privateKeyFilename = (String) yaml.getObject().get("jwt.config.private-key-path");
            // 生成密钥的密文(盐)
            String secret = (String) yaml.getObject().get("jwt.config.key-secret");
            // 秘钥大小
            int keySize = (Integer) yaml.getObject().get("jwt.config.key-size");
            RsaUtils.generateKey(publicKeyFilename, privateKeyFilename, secret, keySize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
