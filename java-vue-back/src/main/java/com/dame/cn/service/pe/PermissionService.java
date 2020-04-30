package com.dame.cn.service.pe;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.vo.PermNode;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LYQ
 * @since 2020-04-01
 */
public interface PermissionService extends IService<Permission> {

    List<PermNode> getPermNode(String pid);
}
