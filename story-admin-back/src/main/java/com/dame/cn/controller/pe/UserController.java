package com.dame.cn.controller.pe;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dame.cn.beans.entities.User;
import com.dame.cn.beans.entities.UserRole;
import com.dame.cn.beans.response.PageResult;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.service.pe.UserRoleService;
import com.dame.cn.service.pe.UserService;
import com.dame.cn.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author LYQ
 * @since 2020-04-01
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sys/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private IdWorker idWorker;


    /**
     * 查询员工集合
     */
    @RequiresPermissions(value = {"settings-user-findall"})
    @GetMapping(value = "/list")
    public Result findAll(@RequestParam Map<String, Object> map,
                          @RequestParam(name = "page", defaultValue = "1") int page,
                          @RequestParam(name = "size", defaultValue = "10") int size) {
        IPage<User> userIPage = userService.findAll(map, page, size);
        PageResult<User> pageResult = new PageResult<>(userIPage.getTotal(), userIPage.getRecords());
        return new Result(ResultCode.SUCCESS, pageResult);
    }

    /**
     * 根据ID查询user
     */
    @RequiresPermissions(value = {"settings-user-findById"})
    @GetMapping(value = "/find/{id}")
    public Result findById(@PathVariable("id") String id) {
        User user = userService.getById(id);
        user.setPassword("********");
        return new Result(ResultCode.SUCCESS, user);
    }

    /**
     * 保存user
     */
    @RequiresPermissions(value = {"settings-user-save"})
    @PostMapping(value = "/save")
    public Result save(@RequestBody User user) {
        //user.setLevel("user");
        user.setId(String.valueOf(idWorker.nextId()));

        // 密码处理 todo

        userService.save(user);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 修改User
     */
    @RequiresPermissions(value = {"settings-user-update"})
    @PutMapping(value = "/update/{id}")
    public Result update(@PathVariable("id") String id, @RequestBody User user) {
        user.setId(id);
        // 密码处理
        userService.updateById(user);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据id删除
     */
    @RequiresPermissions(value = {"settings-user-remove"})
    @DeleteMapping(value = "/delete/{id}")
    public Result delete(@PathVariable(value = "id") String id) {
        userService.removeById(id);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 根据用户Id查询角色
     */
    @RequiresPermissions(value = {"settings-user-findUserRole"})
    @GetMapping(value = "/role/{id}")
    public Result findUserRole(@PathVariable("id") String userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId);
        Set<String> roleIds = userRoleService.list(wrapper).stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return new Result(ResultCode.SUCCESS, roleIds);
    }

    /**
     * 给用户分配角色
     */
    @RequiresPermissions(value = {"settings-user-assignRoles"})
    @PutMapping(value = "/assign/roles")
    public Result assign(@RequestBody Map<String, Object> map) {
        //1.获取被分配的用户id
        String userId = MapUtil.getStr(map, "userId");
        //2.获取到角色的id列表
        List<String> roleIds = (List<String>) map.get("roleIds");
        //3.完成角色分配
        List<UserRole> userRoles = new ArrayList<>();
        for (String roleId : roleIds) {
            userRoles.add(new UserRole(roleId, userId));
        }
        userRoleService.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        userRoleService.saveBatch(userRoles);
        return new Result(ResultCode.SUCCESS);
    }

}

