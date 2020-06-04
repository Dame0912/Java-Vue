package com.dame.cn.beans.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.entities.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LYQ
 * @description TODO
 * @since 2020-06-03
 **/
@Data
public class SysUser implements UserDetails, Serializable {
    private String id;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户名称
     */
    private String username;

    private String password;

    /**
     * 启用状态 0是禁用，1是启用
     */
    private Integer status;

    /**
     * 部门ID
     */
    private String departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 角色集合
     */
    private List<Role> roleList;

    /**
     * 权限集合
     */
    private List<Permission> permissionList;

    /**
     * token生成时间
     */
    private Long tokenGenerateTime;

    /**
     * token刷新时间
     */
    private Long tokenRefreshTime;

    /**
     * 生成的Token
     */
    private String token;


    @JSONField(serialize = false)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        roleList.forEach(role -> auths.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        permissionList.forEach(permission -> auths.add(new SimpleGrantedAuthority(permission.getCode())));
        return auths;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
