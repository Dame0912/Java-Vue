package com.dame.cn.service.pe.impl;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.entities.Role;
import com.dame.cn.beans.entities.User;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.beans.vo.ProfileResult;
import com.dame.cn.config.jwt.JwtUtil;
import com.dame.cn.config.shiro.JwtAuthenticationToken;
import com.dame.cn.config.shiro.ShiroUtil;
import com.dame.cn.mapper.UserMapper;
import com.dame.cn.service.pe.UserRoleService;
import com.dame.cn.service.pe.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author LYQ
 * @since 2020-04-01
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;


    @Override
    public IPage<User> findAll(Map map, int page, int size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 手机号
        if (StrUtil.isNotBlank(MapUtil.getStr(map, "mobile"))) {
            wrapper.eq(User::getMobile, MapUtil.getStr(map, "mobile"));
        }
        // 姓名
        if (StrUtil.isNotBlank(MapUtil.getStr(map, "username"))) {
            wrapper.like(User::getUsername, MapUtil.getStr(map, "username"));
        }
        // 状态
        if (null != MapUtil.getInt(map, "status")) {
            wrapper.eq(User::getStatus, MapUtil.getInt(map, "status"));
        }
        //根据请求的hasDept判断  是否分配部门 ，hasDept
        if (StrUtil.isNotBlank(MapUtil.getStr(map, "hasDept"))) {
            //0 未分配（departmentId = null），1 已分配（departmentId ！= null）
            if ("0".equals(MapUtil.getStr(map, "hasDept"))) {
                wrapper.isNull(User::getDepartmentId);
            } else {
                wrapper.isNotNull(User::getDepartmentId);
            }
        }

        Page<User> userPage = new Page<>(page, size);
        return baseMapper.selectPage(userPage, wrapper);
    }

    @Override
    public String login(Map<String, String> loginMap) {
        String mobile = loginMap.get("mobile");
        String password = loginMap.get("password");
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getMobile, mobile);
        User user = userService.getOne(wrapper);
        if (null == user || !ShiroUtil.checkMd5Password(password, ShiroUtil.salt, user.getPassword())) {
            throw new BizException(ResultCode.MOBILE_OR_PASSWORD_ERROR);
        } else {
            //登录成功
            String token = JwtUtil.createJwt(user.getId(), user.getUsername(), null);
            SecurityUtils.getSubject().login(new JwtAuthenticationToken(token));
            return token;
        }
    }

    @Override
    public ProfileResult getUserProfile() {
        try {
            String userId = LoginUserContext.getCurrentUser().getUserId();
            User user = userService.getById(userId);
            List<Permission> permissionList = baseMapper.getUserPerms(userId);
            return new ProfileResult(user, permissionList);
        } catch (ExpiredJwtException e) {
            throw new BizException(ResultCode.UNAUTH_EXPIRE);
        }
    }

    @Override
    public List<Role> findRolesByUserId(String userId) {
        return baseMapper.getUserRoles(userId);
    }

    @Override
    public List<Permission> findPermsByUserId(String userId) {
        return baseMapper.getUserPerms(userId);
    }

}
