package com.dame.cn.controller.pe;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.dame.cn.beans.entities.User;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.beans.vo.ProfileResult;
import com.dame.cn.config.jwt.JwtUtil;
import com.dame.cn.config.shiro.UserPrincipalInfo;
import com.dame.cn.service.pe.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 登陆
     */
    @PostMapping(value = "/login")
    public Result login(@RequestBody Map<String, String> loginMap) {
        String token = userService.login(loginMap);
        return new Result(ResultCode.SUCCESS, token);
    }

    /**
     * 用户登录成功之后，获取用户信息，角色，权限
     * 1.获取用户id
     * 2.根据用户id查询用户
     * 3.构建返回值对象
     * 4.响应
     * <p>
     * 前后端约定：前端请求微服务时需要添加头信息Authorization ,内容为Bearer+空格+token，现在交给Shiro处理
     */
    //@RequiresAuthentication, 加不加都一样，自定义的filter已经认证过了
    @PostMapping("/login/info")
    public Result profile() {
        UserPrincipalInfo userPrincipalInfo = (UserPrincipalInfo) SecurityUtils.getSubject().getPrincipal();
        ProfileResult profileResult = userService.getUserProfile(userPrincipalInfo.getToken());
        return new Result(ResultCode.SUCCESS, profileResult);
    }

    /**
     * 用户登出。
     * 没有直接在拦截时使用自带的logout过滤器，因为默认登出会跳转到自定义或默认("/")的URL中，这里需要返回 ResponseBody到前端，
     * 同时由于我们禁用SessionDao, logoutFilter处理时，获取不到认证主体即 SecurityUtils.getSubject().getPrincipal()，
     * 所以这里需要这样处理，先被自定义的ShiroJwtFilter认证，放入Principal,然后 logout()
     */
    @PostMapping("/logout")
    public Result logout() {
        // 调用Shiro自带的logout方法，shiro-redis也会清空缓存
        SecurityUtils.getSubject().logout();
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 获取个人信息，前台 我的 功能
     */
    @PostMapping(value = "/profile")
    public Result getProfileInfo(HttpServletRequest request) {
        UserPrincipalInfo userPrincipalInfo = (UserPrincipalInfo) SecurityUtils.getSubject().getPrincipal();
        String userId = JwtUtil.parseJwt(userPrincipalInfo.getToken()).getId();
        User user = userService.getById(userId);
        user.setPassword("******");
        Map<String, Object> beanMap = BeanUtil.beanToMap(user);
        beanMap.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return new Result(ResultCode.SUCCESS, beanMap);
    }

    /**
     * 修改密码
     */
    @PutMapping(value = "/edit/pwd")
    public Result editPassWord(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new BizException(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        Claims claims = JwtUtil.parseJwt(token);
        String userId = claims.getId();
        // 原密码
        User user = userService.getById(userId);
        if (null == user || !user.getPassword().equals(MapUtil.get(map, "originPwd", String.class))) {
            throw new BizException(ResultCode.PWD_ERROR);
        }
        user.setPassword(MapUtil.get(map, "newPwd", String.class));
        userService.updateById(user);
        // 新密码
        return new Result(ResultCode.SUCCESS);
    }
}

