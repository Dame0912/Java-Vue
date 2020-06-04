# 4.0.0 分支

### 一、说明

> **基于 分支1.0.0 改造**



### 二、新增功能

> **1、引入Spring-Security + JWT, 完成后端认证及权限控制**

> **2、自动刷新Token配置** （Token快过期时，后端自动刷新Token，并将新的Token放入Head中）。

> **3、自动刷新Token防并发控制**

> **4、一个账号，只能一个用户登录**（Token中增加时间字段和Redis中比对）



### 三、启动

> **1、导入 java-vue.sql**
>
> **2、启动本地 Redis**



### 四、后台

#### 1、用户登录认证

> 默认Security使用  **UsernamePasswordAuthenticationFilter**   完成用户表单数据收集以及认证成功和失败的处理。
>
> 而我们要使用 JWT前后端分离，继承 **UsernamePasswordAuthenticationFilter** 完成。

```java
/**
 * @author LYQ
 * @description 登录认证过滤器
 * @since 2020-06-03
 **/
@Slf4j
public class JwtTokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    /**
     * public UsernamePasswordAuthenticationFilter() {
     *      super(new AntPathRequestMatcher("/login", "POST"));
     * }
     * 默认登录URL 就是 /login
     */
    public JwtTokenLoginFilter(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * 尝试认证
     
     public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
           super(null);
           this.principal = principal;
           this.credentials = credentials;
           setAuthenticated(false);  // 没有认证的标识
      }
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(mobile, password);
        // 交给认证管理器完成认证
        return authenticationManager.authenticate(authentication);
    }

    /**
     * 认证失败
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        SecurityUtils.responseResult(response, new Result(ResultCode.ERROR));
    }

    /**
     * 认证成功
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        SysUser sysUser = (SysUser) authResult.getPrincipal();
        String token = jwtTokenService.createToken(sysUser);
        // 将token放入请求头中
        response.addHeader(JwtTokenConst.TOKEN_KEY, token);
        SecurityUtils.responseResult(response, new Result(ResultCode.SUCCESS));
    }

}
```



#### 2、认证 UserDetailService

> Security中，不知道我们的具体数据存放位置，所以提供了 UserDetailsService 接口让我们实现。

```java
/**
 * @author LYQ
 * @description 用户认证Service
 * @since 2020-06-03
 **/
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.isEmpty(username)) {
            // 从数据库中查询用户信息
            // 使用封装的 SysUser, 最后放Redis中信息比较全面
            SysUser sysUser =  userMapper.getUserAndRP(username);
            return sysUser;
        }
        return null;
    }
}


// 封装的对象， 实现了框架的 UserDetails
@Data
public class SysUser implements UserDetails, Serializable {
    private String id;
    private String mobile;
    private String username;
    private String password;
    private Integer status;
    private String departmentId;
    private String departmentName;
    private List<Role> roleList; // 角色集合
    private List<Permission> permissionList; // 权限集合
    private Long tokenGenerateTime; // token生成时间
    private Long tokenRefreshTime; // token刷新时间
    private String token; //  生成的Token

	
    /**
      *  实现 UserDetails 的方法， 用户的权限集合
      *
      */
    @JSONField(serialize = false)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        // "ROLE_" 表示这是Role信息
        roleList.forEach(role -> 
                 auths.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        permissionList.forEach(permission -> 
                 auths.add(new SimpleGrantedAuthority(permission.getCode())));
        return auths;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}

```



#### 3、Token校验

> Security中  **UsernamePasswordAuthenticationToken**  使用带权限信息的构造器时，**super.setAuthenticated(true);**  标识已经认证通过。
>
> 继承 **BasicAuthenticationFilter** 来校验Token，校验成功则，构造认证成功的 **UsernamePasswordAuthenticationToken**  
>
> ```java
> public UsernamePasswordAuthenticationToken(Object principal, Object credentials,
> 			Collection<? extends GrantedAuthority> authorities) {
> 		super(authorities);
> 		this.principal = principal;
> 		this.credentials = credentials;
> 		super.setAuthenticated(true); // 认证成功标识
> 	}
> ```

```java
/**
 * @author LYQ
 * @description Token校验过滤器
 * @since 2020-06-03
 **/
public class JwtTokenVerifyFilter extends BasicAuthenticationFilter {

    private final JwtTokenService jwtTokenService;

    public JwtTokenVerifyFilter(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        super(authenticationManager);
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * 过滤请求
     */
    @Override
    protected void doFilterInternal(request, response, FilterChain chain) {
        String authorization = request.getHeader(JwtTokenConst.TOKEN_KEY);
        String token = authorization.replace(JwtTokenConst.TOKEN_PREFIX, "");
        try {
            SysUser sysUser;
            if (!StringUtils.isEmpty(authorization) 
                && null != (sysUser = jwtTokenService.querySysUserCache(token))) {
                if (!StringUtils.isEmpty(sysUser.getToken())) {
                    response.setHeader(JwtTokenConst.TOKEN_KEY, sysUser.getToken());
                }
                // 标识已经认证成功
  				UsernamePasswordAuthenticationToken authResult = 
      new UsernamePasswordAuthenticationToken(sysUser, null,sysUser.getAuthorities());
                // 将信息保存到Security的上下文中
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } else {
            	SecurityUtils.responseResult(response, new Result(UNAUTH_EXPIRE));
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            SecurityUtils.responseResult(response, new Result(UNAUTH_EXPIRE));
        }
    }

}
```



#### 4、退出登录处理

> Security中 退出默认使用 **LogoutFilter** 完成， 退出成功，使用 **LogoutSuccessHandler**
>
> 由于我们退出，需要清除Redis，所以我们 实现 **LogoutSuccessHandler** 接口即可。

```java
/**
 * @author LYQ
 * @description 退出成功执行
 * @since 2020-06-04
 **/
@Service
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Resource
    private JwtTokenService jwtTokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        // SecurityContext 已经被清空
        // String userId = SecurityUtils.getUserId();
        try {
            String authorization = request.getHeader(JwtTokenConst.TOKEN_KEY);
            String token = authorization.replace(JwtTokenConst.TOKEN_PREFIX, "");
            // 删除缓存
            jwtTokenService.delSysUserCache(token);
            SecurityUtils.responseResult(response, new Result(ResultCode.SUCCESS));
        }catch (Exception e) {
            SecurityUtils.responseResult(response, new Result(ResultCode.FAIL));
        }

    }
}
```



#### 5、自定义SecurityUtils

```java
public class SecurityUtils {
	// 从Security上下文中获取用户ID和NAME
    public static String getUserId() {
        SysUser sysUser = 
            (SysUser) SecurityContextHolder.getContext()
            	.getAuthentication().getPrincipal();
        return sysUser.getId();
    }

    public static String getUserName() {
        SysUser sysUser = 
            (SysUser) SecurityContextHolder.getContext()
            	.getAuthentication().getPrincipal();
        return sysUser.getUsername();
    }
	
    // 返回前台数据
    public static void responseResult(HttpServletResponse response, Result result) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(result));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



#### 6、Security 主配置

```java
/**
 * @author LYQ
 * @description SpringSecurity主配置
 * @since 2020-06-03
 **/
@Configuration
@EnableWebSecurity // 开启Security
@EnableGlobalMethodSecurity(prePostEnabled = true)// 开启Security的方法权限控制注解
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Resource
    private JwtTokenService jwtTokenService;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private LogoutSuccessHandler logoutSuccessHandler;
	
    // 密码器
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder());
    }
	
    // 权限控制
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //跨域及关闭跨站请求防护
                .cors().and().csrf().disable()
            
                //前后端分离是无状态的，不用session了，直接禁用
                .sessionManagement()
            		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
                .and()
            
                .authorizeRequests()
            
                //处理 跨域 请求中的Preflight请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            
                //允许不登陆就可以访问的方法，多个用逗号分隔
                .antMatchers("/webjars/**").anonymous()
            
                //其他的需要授权后访问
                .anyRequest().authenticated()
            
                .and()
            
        //增加自定义认证过滤器
        .addFilter(new JwtTokenLoginFilter(authenticationManager(), jwtTokenService))
        //增加自定义验证认证过滤器
        .addFilter(new JwtTokenVerifyFilter(authenticationManager(), jwtTokenService))
        //退出成功处理
        .logout().logoutSuccessHandler(logoutSuccessHandler);
    }

}
```



#### 7、跨域配置

> 使用SpringSecurity需要在上面的主配置类中增加 跨域配置，同时还要加入下面的跨域配置

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 注册 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader(JwtTokenConst.TOKEN_KEY);
        return corsConfiguration;
    }

}
```



#### 8、Token生成及刷新Token

> 1、token和redis中都放入tokenTime，二者比较，确保一个用户使用
>
> 2、刷新token，同时防止并发

```java
@Service
public class JwtTokenService {

    @Resource
    private RedisCache redisCache;
    private static final String user_prefix = "login_user:";
    private static final String token_generate_time = "tokenGenerateTime";

    /**
     * 生成Token，并保存用户数据至Redis
     */
    public String createToken(SysUser sysUser) {
        // token生成时间
        sysUser.setTokenGenerateTime(System.currentTimeMillis()); 
        // token有效时间
        sysUser.setTokenRefreshTime(sysUser.getTokenGenerateTime() + 60分钟)
        String redisKey = user_prefix + sysUser.getId();
        sysUser.setToken("");
        sysUser.setPassword("");
        // 将用户信息保存到Redis中
        redisCache.setCacheObject(redisKey, sysUser, 60分钟, TimeUnit.MINUTES);
        HashMap<String, Object> map = MapUtil.of(token_generate_time, sysUser.getTokenGenerateTime());
        // 生成token，map中保存用户token生成的时间
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
        if (null == sysUser || 
            // 比较token时间和Redis时间，保证只能有一个用户登录
            !tokenGenerateTime.equals(sysUser.getTokenGenerateTime())) { 
            return null;
        }
        refreshToken(sysUser);
        return sysUser;
    }

    /**
     * 刷新Token
     */
    public void refreshToken(SysUser sysUser) {
        // 防止页面一下有多个请求同时过来并发
        if (redisCache.lock(sysUser.getId())) {
            try {
                Long tokenRefreshTime = sysUser.getTokenRefreshTime();
                Long currentTime = System.currentTimeMillis();
                // 防止频繁刷新
                if (tokenRefreshTime - currentTime <= 20分钟) {
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
```



#### 9、权限控制

> 在controller中使用 Security 提供的注解拦截控制，@PreAuthorize ，其实就是 **AOP**，和我们使用的 MethodInterceptor 一样道理

```java
// 权限控制注解
@PreAuthorize("hasAuthority('settings-user-findById')")
@GetMapping(value = "/find/{id}")
public Result findById(@PathVariable("id") String id) {
	User user = userService.getById(id);
	user.setPassword("********");
	return new Result(ResultCode.SUCCESS, user);
}
```





### 五、前台

#### 1、刷新Token

> 每次相应，判断请求token是否有Token，有就更新

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
	
    // 相应处理  
    const res = response.data
	...
```

























