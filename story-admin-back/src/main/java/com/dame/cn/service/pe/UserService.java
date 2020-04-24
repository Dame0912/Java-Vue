package com.dame.cn.service.pe;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.entities.Role;
import com.dame.cn.beans.entities.User;
import com.dame.cn.beans.vo.ProfileResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LYQ
 * @since 2020-04-01
 */
public interface UserService extends IService<User> {

    IPage<User> findAll(Map map, int page, int size);

    /**
     * 用户登陆
     */
    String login(Map<String,String> loginMap);

    /**
     * 获取用户基本信息，以及权限信息（菜单，按钮）
     * @param token token
     * @return 基本信息封装
     */
    ProfileResult getUserProfile(String token);

    /**
     * 查询用户角色集合
     * @param userId 用户ID
     * @return 角色集合
     */
    List<Role> findRolesByUserId(String userId);

    /**
     * 查询用户权限集合
     * @param userId 用户ID
     * @return 权限集合
     */
    List<Permission> findPermsByUserId(String userId);

}
