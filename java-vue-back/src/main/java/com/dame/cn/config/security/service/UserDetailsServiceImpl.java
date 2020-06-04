package com.dame.cn.config.security.service;

import com.dame.cn.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

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
        log.info("UserDetailsServiceImpl -> userName:{}", username);
        if (!StringUtils.isEmpty(username)) {
            // SysUser implements UserDetails
            // public User(String username, String password, Collection<? extends GrantedAuthority > authorities)
            // SysUser目的：后面的认证主体就是这个sysUser对象，而不是 username这简单信息
            return userMapper.getUserAndRP(username);
        }
        return null;
    }
}
