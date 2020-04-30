package com.dame.cn.beans.vo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.dame.cn.beans.entities.Permission;
import com.dame.cn.beans.entities.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Setter
@Getter
public class ProfileResult {
    private String userId;
    private String mobile;
    private String username;
    private String avatar;
    private Map<String, Object> roles = new HashMap<>();

    public ProfileResult(User user, List<Permission> permissionList) {
        this.userId = user.getId();
        this.mobile = user.getMobile();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();

        // mybatis，left join的时候，主表有数据，从表没有，根据where条件结果如果没有数据，返回的是 [null]，所以必须特殊判断下
        if (CollUtil.isEmpty(permissionList) || (1 == permissionList.size() && null == permissionList.get(0))) {
            return;
        }

        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for (Permission perm : permissionList) {
            String code = perm.getCode();
            if (perm.getType() == 1) {
                menus.add(code);
            } else if (perm.getType() == 2) {
                points.add(code);
            } else {
                apis.add(code);
            }
        }
        this.roles.put("menus", menus);
        this.roles.put("points", points);
        this.roles.put("apis", apis);
    }
}
