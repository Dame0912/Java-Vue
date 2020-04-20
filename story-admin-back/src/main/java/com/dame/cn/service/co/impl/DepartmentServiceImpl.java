package com.dame.cn.service.co.impl;

import com.dame.cn.beans.entities.Department;
import com.dame.cn.beans.vo.DeptWithChildItem;
import com.dame.cn.mapper.DepartmentMapper;
import com.dame.cn.service.co.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LYQ
 * @since 2020-04-11
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public List<DeptWithChildItem> getWithChildList() {
        return baseMapper.selectDeptWithChildList("0");
    }
}
