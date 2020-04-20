package com.dame.cn.mapper;

import com.dame.cn.beans.entities.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dame.cn.beans.vo.DeptWithChildItem;
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
public interface DepartmentMapper extends BaseMapper<Department> {

    List<DeptWithChildItem> selectDeptWithChildList(@Param("pid") String pid);
}
