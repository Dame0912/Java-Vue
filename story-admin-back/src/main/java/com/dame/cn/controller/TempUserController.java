package com.dame.cn.controller;


import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author LYQ
 * @since 2020-03-30
 */
@CrossOrigin
@RestController
public class TempUserController {

    /**
     * 'admin-token': {
     * roles: ['admin'],
     * introduction: 'I am a super administrator',
     * avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
     * name: 'Super Admin'
     */

    @PostMapping("/login/login")
    public Map login() {
        Map<String, Object> map = new HashMap<>();
        map.put("roles", new String[]{"admin"});
        map.put("introduction", "我是超级管理员");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name", "Super Admin");
        map.put("token", "admin");
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("code", 10000);
        retMap.put("data", map);
        return retMap;
    }

    /**
     * {"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator","avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif","name":"Super Admin"}}
     * @return
     */
    @GetMapping("/user/info")
    public Map info(){
        Map<String, Object> map = new HashMap<>();
        map.put("roles", new String[]{"admin"});
        map.put("introduction", "我是超级管理员");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name", "Super Admin");
        map.put("token", "admin");
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("code", 10000);
        retMap.put("data", map);
        return retMap;
    }
}

