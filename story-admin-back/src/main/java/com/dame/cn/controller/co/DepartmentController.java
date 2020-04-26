package com.dame.cn.controller.co;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.entities.Department;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.beans.vo.DeptListResult;
import com.dame.cn.beans.vo.DeptWithChildItem;
import com.dame.cn.service.co.DepartmentService;
import com.dame.cn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author LYQ
 * @since 2020-04-11
 */
@CrossOrigin
@RestController
@RequestMapping("/co")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private IdWorker idWorker;

    // 保存部门
    @PostMapping("department")
    public Result save(@RequestBody Department department) {
        department.setId(String.valueOf(idWorker.nextId()));
        if (StringUtils.isEmpty(department.getPid())) {
            department.setPid("0");
        }
        department.setCreator(LoginUserContext.getCurrentUser().getUsername());
        departmentService.save(department);
        return new Result(ResultCode.SUCCESS);
    }

    // 查询企业部门列表
    @GetMapping(value = "department")
    public Result findAll() {
        List<DeptWithChildItem> deptWithChildItemList = departmentService.getWithChildList();
        DeptListResult deptListResult = new DeptListResult(deptWithChildItemList);
        return new Result(ResultCode.SUCCESS, deptListResult);
    }

    // 根据id查询部门
    @GetMapping(value = "department/{id}")
    public Result findById(@PathVariable(value = "id") String id) {
        Department department = departmentService.getById(id);
        return new Result(ResultCode.SUCCESS, department);
    }

    // 根据id修改部门
    @PutMapping(value = "department/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody Department department) {
        department.setId(id);
        department.setEditor(LoginUserContext.getCurrentUser().getUsername());
        departmentService.updateById(department);
        return new Result(ResultCode.SUCCESS);
    }

    // 根据id删除部门
    @DeleteMapping(value = "department/{id}")
    public Result delete(@PathVariable("id") String id) {
        departmentService.removeById(id);
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<Department>().eq(Department::getPid, id);
        departmentService.remove(wrapper);
        return new Result(ResultCode.SUCCESS);
    }
}

