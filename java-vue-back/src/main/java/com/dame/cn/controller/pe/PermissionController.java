package com.dame.cn.controller.pe;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.entities.RolePermission;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.beans.vo.PermNode;
import com.dame.cn.service.pe.PermissionService;
import com.dame.cn.service.pe.RolePermissionService;
import com.dame.cn.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
@RequestMapping("/sys/perm")
@Slf4j
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private IdWorker idWorker;

    @GetMapping(value = "/assignPermNodes")
    public Result findAssignPerms(@RequestParam(name = "roleId") Long roleId) {
        List<Permission> permList = (List<Permission>) findAll(MapUtil.of("type", 0)).getData();
        List<String> permIds = permList.stream().map(Permission::getId).collect(Collectors.toList());
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<Permission>().in(Permission::getPid, permIds).eq(Permission::getType, 3);
        List<Permission> apiPermList = permissionService.list(wrapper);
        Map<String, List<Permission>> apiPermMap = apiPermList.stream().collect(Collectors.groupingBy(Permission::getPid));

        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissionList = rolePermissionService.list(queryWrapper);
        List<String> rolePermIds = rolePermissionList.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        List<String> collect = apiPermList.stream().map(Permission::getId).collect(Collectors.toList());
        List<String> apiIds = rolePermIds.stream().filter(apiPermId -> collect.contains(apiPermId)).collect(Collectors.toList());
        List<String> noApiIds = rolePermIds.stream().filter(id -> !apiIds.contains(id)).collect(Collectors.toList());

        List<Permission> apiCheckPerms = apiPermList.stream().filter(perm -> apiIds.contains(perm.getId())).collect(Collectors.toList());

        HashMap<Object, Object> resultMap = MapUtil.of(new Object[][]{{"permList", permList}, {"apiPermMap", apiPermMap}, {"apiCheckPerms", apiCheckPerms}, {"noApiIds", noApiIds}});
        return new Result(ResultCode.SUCCESS, resultMap);
    }

    /**
     * 查询全部
     * type      : 查询全部权限列表type：0：菜单 + 按钮（权限点） 1：菜单2：按钮（权限点）3：API接口
     * pid ：父id
     */
    @GetMapping(value = "/list")
    public Result findAll(@RequestParam Map map) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        //根据父id查询
        if (!StringUtils.isEmpty(map.get("pid"))) {
            wrapper.eq(Permission::getPid, MapUtil.getStr(map, "pid"));
        }
        //根据类型 type
        if (StrUtil.isNotBlank(MapUtil.getStr(map, "type"))) {
            String type = MapUtil.getStr(map, "type");
            if ("0".equals(type)) {
                wrapper.in(Permission::getType, Arrays.asList(1, 2));
            } else {
                wrapper.in(Permission::getType, Integer.parseInt(type));
            }
        }
        List<Permission> list = permissionService.list(wrapper);

        return new Result(ResultCode.SUCCESS, list);
    }


    /**
     * 根据ID查询
     */
    @GetMapping(value = "/find/{id}")
    public Result findById(@PathVariable(value = "id") String id) {
        return new Result(ResultCode.SUCCESS, permissionService.getById(id));
    }

    /**
     * 保存
     */
    @PostMapping(value = "/save")
    public Result save(@RequestBody Permission permission) {
        //设置主键的值
        permission.setId(String.valueOf(idWorker.nextId()));
        permission.setCreator(LoginUserContext.getCurrentUser().getUsername());
        //3.保存
        permissionService.save(permission);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 修改
     */
    @PutMapping(value = "/update/{id}")
    public Result update(@PathVariable(value = "id") String id, @RequestBody Permission permission) {
        permission.setId(id);
        permission.setEditor(LoginUserContext.getCurrentUser().getUsername());
        //3.保存
        permissionService.updateById(permission);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据权限id删除权限
     */
    @DeleteMapping(value = "/delete/{id}")
    public Result delete(@PathVariable(value = "id") String id) {
        List<PermNode> permNodes = permissionService.getPermNode(id);
        List<String> ids = getIds(permNodes);
        ids.add(id);
        permissionService.removeByIds(ids);
        return new Result(ResultCode.SUCCESS);
    }

    private List<String> getIds(List<PermNode> permNodes) {
        List<String> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(permNodes)) {
            for (PermNode permNode : permNodes) {
                list.add(permNode.getId());
                if (CollUtil.isNotEmpty(permNode.getChildren())) {
                    list.addAll(getIds(permNode.getChildren()));
                }
            }
        }
        return list;
    }

}

