package com.dame.cn.mapper;

import com.dame.cn.beans.entities.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dame.cn.beans.vo.PermNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author LYQ
 * @since 2020-04-11
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    List<PermNode> selectPermNode(@Param("pid") String pid);
}
