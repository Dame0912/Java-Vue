# Shiro-Jwt-Vue

### 一、说明

> **基于 分支1.0.0 改造**



### 二、新增功能

> **1、引入Shiro,完成后端认证及权限控制**
> **2、jedis/jedisPool配置**
>    （由于使用shiro-redis的jar完成Shiro的缓存配置，其依赖jedis,所以没有使用redisTemplate）。
> **3、密码加密**



### 三、启动

> **1、导入 shiro_jwt.sql**
>
> **2、启动本地 Redis**



### 四、后台

####  1. Shiro主配置

```java
// 配置安全管理器
@Bean
public SecurityManager securityManager(CustomerRealm customerRealm) {
    // 使用默认的Web安全管理器
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    // 将自定义的realm交给安全管理器统一调度管理, 如果有多个realm，也可以配置，
    // securityManager.setRealms(Collection<Realm> realms)
    securityManager.setRealm(customerRealm);

    // 关闭shiro自带的session管理
    DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
    DefaultSessionStorageEvaluator sessionStorage = new DefaultSessionStorageEvaluator();
    sessionStorage.setSessionStorageEnabled(false);
    subjectDAO.setSessionStorageEvaluator(sessionStorage);
    securityManager.setSubjectDAO(subjectDAO);

    // 自定义缓存实现，使用redis，shiro-redis已经提供，只需要配置即可
    securityManager.setCacheManager(redisCacheManager());
    return securityManager;
}

    // Filter工厂，设置对应的过滤条件和跳转条件
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroFilterMapProperties shiroFilterMapProperties) {
        // 1.创建shiro过滤器工厂
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        // 2.设置安全管理器
        filterFactoryBean.setSecurityManager(securityManager);

        // 3.添加自定义的过滤器
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new ShiroJwtFilter());
        filterFactoryBean.setFilters(filterMap);

        /*
         * anon：开放权限，可以理解为匿名用户或游客
         * authc：需要认证，即需要登陆
         * perms[user]：参数可写多个，表示需要某个或某些权限才能通过，多个参数时写 perms[“user, admin”]，当有多个参数时必须每个参数都通过才算通过
         * roles[admin]：参数可写多个，表示是某个或某些角色才能通过，多个参数时写 roles[“admin，user”]，当有多个参数时必须每个参数都通过才算通过
         * logout：注销，执行后会直接跳转到配置的登陆页
         *
         * 如：
         *  LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();
         *  filterMap.put("/user/home", "anon");
         *  filterMap.put("/user/find", "perms[user-find]");
         *  filterMap.put("/user/update/*", "roles[系统管理员]");
         *  filterMap.put("/user/**", "authc");
         *  filterMap.put("/logout", "logout");
         */
        
        // 4.配置过滤器集合
        // 存在第一次匹配原则，即权限从上向下匹配，匹配到了就不会在匹配后面的，所以精确的要放到最前面
        // 使用配置文件，配置过滤器集合
        List<Map<String, String>> permList = shiroFilterMapProperties.getPerms();
        LinkedHashMap<String, String> filterMap = new LinkedHashMap<>();
        permList.forEach(perm -> filterMap.put(perm.get("path"), perm.get("filter")));

        //5.设置过滤器
        filterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return filterFactoryBean;
    }
```



#### 2. JWT包装

> <font style="color: red">**目的：**</font> 能够处理我们的 JWT。类似于Shiro自带的 UsernamePasswordToken

```java
/**
 * 使用shiro认证jwt，参考 UsernamePasswordToken
 **/
public class JwtAuthenticationToken implements AuthenticationToken {

    private String token;

    public JwtAuthenticationToken(String token) {
        this.token = token;
    }

    // 主体
    @Override
    public Object getPrincipal() {
        return this.token;
    }

    // 密码
    @Override
    public Object getCredentials() {
        return this.token;
    }
}
```



#### 3. 自定义Realm

> <font style="color: red">**目的：**</font>完成用户的身份认证，以及授权



​	由于我们使用 JWT 完成用户的身份认证，所以 要自定义 AuthenticationToken 来支持，同时要在自定义Realm中显示声明。

```java
public class CustomerRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    public void setName(String name) {
        super.setName("CustomerRealm");
    }

    /**
     * true if this authentication realm can process the submitted token instance of the class, false otherwise.
     *  意思就是判断这个Realm能否处理这个类型的 token
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtAuthenticationToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String jwtToken = (String) token.getPrincipal();
        // 只要校验没有问题即可，如果校验失败，则会抛异常
        try {
            JwtUtil.parseJwt(jwtToken);
        }catch (JwtException e) {
            throw new AuthenticationException("token 过期");
        }

        /*
             * Object principal: 传递的安全数据
             * Object hashedCredentials： 数据库密码
             * ByteSource credentialsSalt： 密码的盐值
             * String realmName：realmName
             */
        // return new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
        // 使用 UserPrincipalInfo 的原因是，使用了redis缓存，缓存存取的时候要我们指定 key
 return new SimpleAuthenticationInfo(new UserPrincipalInfo(jwtToken),jwtToken,getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
        UserPrincipalInfo up = (UserPrincipalInfo) principals.getPrimaryPrincipal();
        String jwtToken = up.getToken();
        String userId = JwtUtil.parseJwt(jwtToken).getId();

        // 设置用户的角色role
        List<Role> roleList = userService.findRolesByUserId(userId);
        roleList.forEach(role -> auth.addRole(role.getName()));

        // 设置用户的权限permission
        List<Permission> permissionList = userService.findPermsByUserId(userId);
        permissionList.forEach(perm -> auth.addStringPermission(perm.getCode()));

        return auth;
    }
}
```



#### 4.  自定义ShiroJwtFilter

> <font style="color: red">**目的：**</font>完成用户的身份认证，等价于Shiro自带的authc。
>
> 同时我们在Shiro主配置中指明使用该过滤器拦截请求

```java
public class ShiroJwtFilter extends BasicHttpAuthenticationFilter {

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
                // 捕获异常，返回false，执行认证失败方法，即下面的 onAccessDenied 方法，
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
        return true;
    }

    /**
     * 判断请求是否已经登陆，通过请求头是否有token，先粗略检查
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwtToken = httpServletRequest.getHeader(JwtTokenConst.TOKEN_KEY);
        return !StringUtils.isEmpty(jwtToken);
    }

    /**
     * 重写，认证失败走的方法，避免父类中调用再次executeLogin,同时父类会跳转页面，而我们需要返回JSON
     * <p>
     * 构建 200 返回码，方便前端处理。
     * 直接使用response.getWrite(), 将JSON结果写给前端。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
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
}
```



#### 5. principal包装

> 我们使用了shiro-redis，用户的权限信息，将保存在Redis中，而这个过程都是该jar包处理的，我们只需要指明如何获取 KEY 即可。

```java
/**
  * 认证
  */
@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) ... {
    ...

    /*
     * new UserPrincipalInfo(jwtToken), 就是传递的安全数据，并交给下面授权方法的入参。
     * shiro-redis,也是从安全数据中拿到我们指明的 key，进行存储
     */
 return new SimpleAuthenticationInfo(new UserPrincipalInfo(jwtToken),jwtToken,getName());
}
```

```java
/**
 *  So please use AuthCachePrincipal to tell shiro-redis how to get the cache key
 *  目的就是为了告诉redis如何获取 key
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
```



#### 6. 权限校验

> 使用Shiro自带的注解，完成权限的校验
>
> **RequiresAuthentication：需要认证通过**
>
> **RequiresPermissions：需要响应权限**
>
> **RequiresRoles：需要响应角色**
>
> **RequiresGuest：游客可以访问**
>
> **RequiresUser：必须是应用的用户**

>​		一般我们会使用 **RequiresPermissions** 来控制权限，而不使用 **RequiresRoles** 因为角色可能变更，如：增加/减少等，不方便维护，但是permission一般不会发生变更。
>
>​		**RequiresAuthentication** ，由于我们使用了 shiroJwtFilter, 已经校验了，所以这里就不需要了		

```java
/**
  * 根据ID查询user
  */
@RequiresPermissions(value = {"settings-user-findById"})
@GetMapping(value = "/find/{id}")
public Result findById(@PathVariable("id") String id) {
    ...
}
```



#### 7. 权限异常捕获

```java
@RestControllerAdvice
public class BaseExceptionHandler {

    /**
     * 权限异常。
     * 如果用户权限不足，shiro会抛出该权限异常，方便前端获取消息体的内容，修改返回的结果。
     *
     * 说明：认证失败。已经在 ShiroJwtFilter中 处理了。这里不捕获。
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result handleUnauthorizedException(UnauthorizedException e) {
        return new Result(ResultCode.UNAUTHORISE);
    }

    @ExceptionHandler(BizException.class)
    public Result handleBizException(BizException e) {
        return new Result(e.getResultCode());
    }

    @ExceptionHandler(value = Exception.class)
    public Result error(Exception e) {
        e.printStackTrace();
        return new Result(ResultCode.SERVER_ERROR);
    }
}
```



### 五、前台

> <font style="color: red">**未做修改**</font>

说明：后台返回 httpStatus=401 处理

背景：Shiro在权限不足时，会返回 401，vue-element-admin 没有作处理。

​			这里因为没处理，是因为后端将401转换为200。

如果要处理 401：对request.js 修改。

```javascript
// response interceptor
service.interceptors.response.use(
  /**
   * 这里处理的都是 httpstatus=200的请求
   */
  response => {
    const res = response.data
    
    // if the custom code is not 20000, it is judged as an error.
    if (res.code !== 10000) {
      Message({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 
      if (res.code === 20001 || res.code === 20002 || res.code === 20003) {
        setTimeout(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        }, 1500)
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('err：' + error) // for debug

    // 上面只能接收 http 状态码为200的请求，如果要获取别的状态码，可以这样处理。
    // 如果想处理，可以
    if (error.response && error.response.status === 401) {
      Message({
        message: error.response.data.message,
        type: 'error',
        duration: 5 * 1000
      })
      setTimeout(() => {
        store.dispatch('user/resetToken').then(() => {
          location.reload()
        })
      }, 1500)
    } else {
      Message({
        message: error.message,
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)
```











































