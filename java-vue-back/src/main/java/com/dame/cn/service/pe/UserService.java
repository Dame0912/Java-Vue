package com.dame.cn.service.pe;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dame.cn.beans.entities.Permission;
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
     * 获取用户基本信息，以及权限信息（菜单，按钮）
     * @return 基本信息封装
     */
    ProfileResult getUserProfile();

}
