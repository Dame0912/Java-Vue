package com.dame.cn.beans.vo;

import com.dame.cn.beans.entities.Permission;
import lombok.Data;

import java.util.List;

/**
 * @author LYQ
 * @description 权限层级
 * @since 2020/3/31 14:08
 **/
@Data
public class PermNode extends Permission {

    private List<PermNode> children;
}
