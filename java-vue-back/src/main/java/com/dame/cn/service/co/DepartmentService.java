package com.dame.cn.service.co;

import com.dame.cn.beans.entities.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dame.cn.beans.vo.DeptWithChildItem;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LYQ
 * @since 2020-04-11
 */
public interface DepartmentService extends IService<Department> {

    // 获取所有部门，并按照层级排序
    List<DeptWithChildItem> getWithChildList();
}
