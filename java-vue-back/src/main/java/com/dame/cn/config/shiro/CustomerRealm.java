package com.dame.cn.config.shiro;

import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.entities.Role;
import com.dame.cn.config.jwt.JwtUtil;
import com.dame.cn.service.pe.UserService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/20 21:15
 **/
@Slf4j
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
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("认证校验。。。。。。");
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
         //return new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
        // 使用 UserPrincipalInfo 的原因是，使用了redis缓存，缓存存取的时候要我们指定 key
        return new SimpleAuthenticationInfo(new UserPrincipalInfo(jwtToken), jwtToken, getName());
    }

    /**
     * 授权
     *
     * @param principals doGetAuthenticationInfo 传递过来的安全数据
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("用户授权。。。。。。");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserPrincipalInfo userPrincipalInfo = (UserPrincipalInfo) principals.getPrimaryPrincipal();
        String jwtToken = userPrincipalInfo.getToken();
        String userId = JwtUtil.parseJwt(jwtToken).getId();

        // 设置用户的角色role
        List<Role> roleList = userService.findRolesByUserId(userId);
        roleList.forEach(role -> authorizationInfo.addRole(role.getName()));

        // 设置用户的权限permission
        List<Permission> permissionList = userService.findPermsByUserId(userId);
        permissionList.forEach(perm -> authorizationInfo.addStringPermission(perm.getCode()));

        return authorizationInfo;
    }

}
