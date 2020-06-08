# 4.0.1 分支

### 一、说明

> **基于 分支4.0.0 改造**



### 二、新增功能

> **1、引入RSA, 完成Token的生成和解密**。
>
> **2、单点登录**
>
> > 在认证服务中，使用私钥加密Token，在其他资源服务中使用公钥解密Token，如果能够解密成功，证明用户状态为登录，否则为非登录状态。



### 三、启动

> **1、导入 java-vue.sql**
>
> **2、启动本地 Redis**
>
> **3、运行 GenerateRsaKey 类，生成公私钥**



### 四、后台

#### 1、RsaUtils

> 生成公私钥，以及读取文件的公私钥

```java
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
```



#### 2、JwtUtil

> 使用私钥，生成 Token
>
> 使用公钥，解析 Token

```java
/**
 * 使用RSA私钥，生成jwt的token
 *
 * @param id   userId
 * @param name username
 * @param map  其它需要保存的
 * @return token
 */
public static String createJwt(String id, String name, Long expire, Map map) {
	try {
		// 1.设置失效时间
		long exp = expire;
		// 2.私钥
        String privatePath = jwtUtil.jwtProperties.privateKeyPath;
		PrivateKey privateKey = RsaUtils.getPrivateKey(privatePath);
		// 3.创建jwtBuilder，并用私钥加密
		JwtBuilder jwtBuilder = Jwts.builder().setId(id).setSubject(name)
				.setIssuedAt(new Date())
            	 // SignatureAlgorithm 需要是RSA的模式
				.signWith(SignatureAlgorithm.RS256, privateKey) 
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
        String publicPath = jwtUtil.jwtProperties.publicKeyPath;
		PublicKey publicKey = RsaUtils.getPublicKey(publicPath);
		// 公钥解密
		return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
	} catch (Exception e) {
		throw new BizException(ResultCode.UNAUTH_EXPIRE);
	}
}
```















