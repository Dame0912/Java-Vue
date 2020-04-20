package com.dame.cn.beans.vo;

import com.dame.cn.beans.entities.Department;
import lombok.Data;

import java.util.List;

/**
 * @author LYQ
 * @description 部门层级分类
 * @since 2020/3/31 14:08
 **/
@Data
public class DeptWithChildItem extends Department {

    private List<DeptWithChildItem> children;
}
