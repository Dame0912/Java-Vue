package com.dame.cn.service.pe.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.vo.PermNode;
import com.dame.cn.mapper.PermissionMapper;
import com.dame.cn.service.pe.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LYQ
 * @since 2020-04-01
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public List<PermNode> getPermNode(String pid) {
        return baseMapper.selectPermNode(pid);
    }
}
