# Shiro-Jwt-Vue

### 一、说明

> **基于 分支2.0.0 改造**



### 二、新增功能

> **1、token过期，自动刷新token**
>
> > <font style="color: red">**原因：**</font> 2.0.0版本，token到达有效期后，用户需要重新登陆。即使用户处于活跃状态，也会过期，导致用户体验不好。
> >
> > <font style="color: red">**解决：**</font> 每次请求认证成功后（*认证失败，说明 token过期即长时间没有用户操作，没有刷新token*），检查是否需要更换token。
> >
> > * 增加Redis，并且增加 redis 与 token 值(*生成 token的时间戳*) 校验，增加了安全性。
> >   * 因为：如果只校验token，那么非法持有token的用户，在一定情况下将一直有效(refreshToken)
> > * 一定程度限制一个账号只能一个用户登陆，另一个用户登陆会挤掉当前用户
> >   * 特例：token没有到达刷新条件，此时不会校验token和reids值，那么token可以继续使用。
> >   * 解决：每次请求后都校验 redis 与 token 值。
> >   * 新问题：每次请求都校验，影响效率。而且这种漏洞正常业务也能满足。如果想要真正的安全，还是要使用 HTTPS
>
> **2、onebyone 锁，防并发**
>
> > <font style="color: red">**原因：**</font> 一个页面同时可能有多个请求，此时如果都刷新token，那么必然只有一个请求的 redis和token 的值相同(*redis值被这个请求更改了*)，其它的请求都会失败，所以增加了防并发。
> >
> > <font style="color: red">**解决：**</font> 同一时间只有一个请求会刷新token，更新redis，其它没获取锁的默认成功。



### 三、启动

> **1、导入 shiro_jwt.sql**
>
> **2、启动本地 Redis**



### 四、后台

####  1. 用户登陆

```java
// 登陆成功
{ 
	String currentTimeMillis = String.valueOf(System.currentTimeMillis());

	// token中放入生成时间
	Map<String, Object> map = MapUtil.of(CURRENT_TIME_MILLIS, currentTimeMillis);
	String token = JwtUtil.createJwt(user.getId(), user.getUsername(), map);

	// redis中也放入token生成的时间，redis过期时间 = token过期时间
	String redisTokenKey = RedisConst.JWT_TOKEN_TIME + user.getId();
	redisClient.setex(redisTokenKey, TokenExpireTime * 60, currentTimeMillis);

	return token;
}
```



#### 2. token刷新

> <font style="color: red">**目的：**</font> ShiroJwtFilter过滤器拦截

```java
private void refreshToken(String jwtToken, ServletResponse response) {
	// 当前时间
	Long currentTimeMillis = System.currentTimeMillis();
	// token中时间
	String tokenMillis = JwtUtil.parseJwt(jwtToken).get(CURRENT_TIME_MILLIS);
	
    // 判断是否需要重新生成token
	if (needRefresh(tokenMillis, currentTimeMillis)) {
		Claims claims = JwtUtil.parseJwt(jwtToken);
		String userId = claims.getId();
		String username = claims.getSubject();
		try {
			// 并发控制，因为页面有时候会同时发送多个请求。
			// 如果没做处理，前面的请求修改了redis，后面的请求都会失败。
			// 所以如果并发请求，只会有一个修改redis，剩下的默认成功。
			oneByOneService.execute(new OneByOne(BIZTYPE, userId,METHOD), () -> {
                // 获取redis中的值
				String redisTokenKey = RedisConst.JWT_TOKEN_TIME + userId;
				String redisMillis = redisClient.get(redisTokenKey);
                
				// 判断redis和token中放入的时间戳是否一致
				if (StringUtils.isEmpty(redisMillis)||!tokenMillis.equals(redisMillis)){
					throw new BizException(ResultCode.UNAUTH_EXPIRE);
				}

				// 更新redis中的时间戳
				redisClient.setex(redisTokenKey,TokenExpireTime()*60, currentTimeMillis);

				// 重新生成token
				Map map=MapUtil.of(CURRENT_TIME_MILLIS, currentTimeMillis);
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
	return (currentTimeMillis - tokenMillis) > RefreshTokenTime() * 60 * 1000L;
}
```



#### 3. 更改跨域

> <font style="color: red">**原因：**</font> 我们将token，放在了header中了。
>
> ​		**httpServletResponse.setHeader(JwtTokenConst.TOKEN_KEY, newToken);**
>
> **跨域配置增加：**config.addExposedHeader(JwtTokenConst.TOKEN_KEY);

```java
@Bean
public FilterRegistrationBean corsFilter() {
	CorsConfiguration config = new CorsConfiguration();
	// 放行哪些原始域
	config.addAllowedOrigin("*");
	// 是否发送Cookie信息
	config.setAllowCredentials(true);
	// 放行哪些原始域(头部信息)
	config.addAllowedHeader("*");
	// 放行哪些原始域(请求方式)
	config.addAllowedMethod("*");
	// 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
	config.addExposedHeader(JwtTokenConst.TOKEN_KEY);
	// 配置预检的有效时长，在时长内无需再次校验，单位为秒。如果请求的URL不同，即使参数不同，也会预检查。
	// 前后端项目，一般跨域，浏览器会先发送一个OPTION请求，通过了再发送真正的请求。为了不让每次都预检。
	config.setMaxAge(3600L);
    
	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", config);
    
	FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
	bean.setOrder(0);
	return bean;
}
```





### 五、前台

#### 1. 统一处理token

在request.js中统一处理，并将user.login中对token的操作关闭。

```javascript
// response interceptor
service.interceptors.response.use(
  response => {
    // 刷新token, 注意后台给的是 Authorization，这边要用小写
    let token = response.headers['authorization']
    if(token){
      setToken(token)
      store.commit('user/SET_TOKEN', token)
    }

      
	// 响应结果处理
    const res = response.data
	
    ....
```











































