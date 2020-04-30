package com.dame.cn.controller.pe;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.entities.Role;
import com.dame.cn.beans.entities.RolePermission;
import com.dame.cn.beans.response.PageResult;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.service.pe.RolePermissionService;
import com.dame.cn.service.pe.RoleService;
import com.dame.cn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author LYQ
 * @since 2020-04-01
 */
@CrossOrigin
@RestController
@RequestMapping("/sys/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private IdWorker idWorker;


    /**
     * 获取所有角色
     */
    @GetMapping(value = "/all")
    public Result list() {
        return new Result(ResultCode.SUCCESS, roleService.list());
    }

    /**
     * 分页查询角色
     */
    @GetMapping(value = "/list")
    public Result findByPage(@RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<Role> rolePage = new Page<>(page, size);
        IPage<Role> searchPage = roleService.page(rolePage, null);
        PageResult<Role> pageResult = new PageResult<>(searchPage.getTotal(), searchPage.getRecords());
        return new Result(ResultCode.SUCCESS, pageResult);
    }

    /**
     * 根据ID获取角色信息
     */
    @GetMapping(value = "/find/{id}")
    public Result findById(@PathVariable(name = "id") String id) {
        Role role = roleService.getById(id);
        return new Result(ResultCode.SUCCESS, role);
    }

    /**
     * 添加角色
     */
    @PostMapping(value = "/save")
    public Result add(@RequestBody Role role) {
        role.setId(String.valueOf(idWorker.nextId()));
        role.setCreator(LoginUserContext.getCurrentUser().getUsername());
        roleService.save(role);
        return Result.SUCCESS();
    }

    /**
     * 更新角色
     */
    @PutMapping(value = "/update/{id}")
    public Result update(@PathVariable(name = "id") String id, @RequestBody Role role) {
        role.setId(id);
        role.setEditor(LoginUserContext.getCurrentUser().getUsername());
        roleService.updateById(role);
        return Result.SUCCESS();
    }

    /**
     * 删除角色
     */
    @DeleteMapping(value = "/delete/{id}")
    public Result delete(@PathVariable(name = "id") String id) {
        roleService.removeById(id);
        return Result.SUCCESS();
    }

    /**
     * 给角色分配权限
     */
    @PutMapping(value = "/assign/perms")
    public Result assign(@RequestBody Map<String, Object> map) {
        //1.获取被分配的角色的id
        String roleId = MapUtil.getStr(map, "roleId");
        //2.获取到权限的id列表
        List<String> permIds = (List<String>) map.get("permIds");
        //3.完成权限分配
        List<RolePermission> rolePermissions = new ArrayList<>();
        for (String permId : permIds) {
            rolePermissions.add(new RolePermission(roleId, permId));
        }
        rolePermissionService.remove(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        rolePermissionService.saveBatch(rolePermissions);
        return new Result(ResultCode.SUCCESS);
    }

}

