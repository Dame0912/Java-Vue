package com.dame.cn.controller.pe;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.dame.cn.beans.entities.User;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.beans.vo.ProfileResult;
import com.dame.cn.config.shiro.JwtUtil;
import com.dame.cn.service.pe.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
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
     * 前后端约定：前端请求微服务时需要添加头信息Authorization ,内容为Bearer+空格+token
     */
    @PostMapping("/login/info")
    public Result profile(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new BizException(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        ProfileResult profileResult = userService.getUserProfile(token);
        return new Result(ResultCode.SUCCESS, profileResult);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new BizException(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 获取个人信息
     */
    @PostMapping(value = "/profile")
    public Result getProfileInfo(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            throw new BizException(ResultCode.UNAUTHENTICATED);
        }
        String token = authorization.replace("Bearer ", "");
        Claims claims = JwtUtil.parseJwt(token);
        String userId = claims.getId();
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
        if(null == user || !user.getPassword().equals(MapUtil.get(map,"originPwd",String.class))){
            throw new BizException(ResultCode.PWD_ERROR);
        }
        user.setPassword(MapUtil.get(map,"newPwd",String.class));
        userService.updateById(user);
        // 新密码
        return new Result(ResultCode.SUCCESS);
    }
}

