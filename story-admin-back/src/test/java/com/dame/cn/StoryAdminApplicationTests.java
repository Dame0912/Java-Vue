package com.dame.cn;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.service.pe.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class StoryAdminApplicationTests {

    @Autowired
    private PermissionService permissionService;

    @Test
    void contextLoads() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Permission::getType, Arrays.asList(1, 2));
        List<Permission> permList = permissionService.list(wrapper);
        List<String> permIds = permList.stream().map(Permission::getId).collect(Collectors.toList());
        LambdaQueryWrapper<Permission> wrapper2 = new LambdaQueryWrapper<Permission>().in(Permission::getPid, permIds).eq(Permission::getType, 3);
        List<Permission> apiPermList = permissionService.list(wrapper2);
        Map<String, List<Permission>> apiPermMap = apiPermList.stream().collect(Collectors.groupingBy(Permission::getPid));
        HashMap<Object, Object> resultMap = MapUtil.of(new Object[][]{{"permList", permList}, {"apiPermMap", apiPermMap}});
        System.out.println(JSON.toJSONString(resultMap));
    }


}
