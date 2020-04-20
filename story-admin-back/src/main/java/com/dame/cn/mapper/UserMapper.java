package com.dame.cn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.entities.User;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author LYQ
 * @since 2020-04-11
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询用户 权限 列表
     */
    List<Permission> getUserPerms(String userId);
}
