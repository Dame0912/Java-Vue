package com.dame.cn.config;

import com.dame.cn.config.shiro.CustomerRealm;
import com.dame.cn.config.shiro.ShiroFilterMapProperties;
import com.dame.cn.config.shiro.ShiroJwtFilter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LYQ
 * @description Shiro主配置
 * @since 2020/4/20 21:13
 **/
@Configuration
public class ShiroConfig {

    // 配置自定义Realm
    @Bean
    public CustomerRealm customerRealm() {
        return new CustomerRealm();
    }

    // 配置安全管理器
    @Bean
    public SecurityManager securityManager(CustomerRealm customerRealm) {
        // 使用默认的Web安全管理器
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 将自定义的realm交给安全管理器统一调度管理, 如果有多个realm，也可以配置，securityManager.setRealms(Collection<Realm> realms)
        securityManager.setRealm(customerRealm);

        // 关闭shiro自带的session管理
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
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
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        permList.forEach(perm -> filterChainDefinitionMap.put(perm.get("path"), perm.get("filter")));

        //5.设置过滤器
        filterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return filterFactoryBean;
    }

    //下面两个，开启对shiro注解的支持
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    // *****************  配置Shiro基于redis的会话和缓存管理，开始 ********************
    @Autowired
    private JedisPool jedisPool;

    // 1. 配置shiro的RedisManager，通过shiro-redis包提供的RedisManager统一对redis操作
    private RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setJedisPool(jedisPool);
        return redisManager;
    }

    // 2. Shiro内部有自己的本地缓存机制，为了更加统一方便管理，全部替换redis实现
    private RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }
    // *****************  配置Shiro基于redis的缓存管理，结束 ********************

}
