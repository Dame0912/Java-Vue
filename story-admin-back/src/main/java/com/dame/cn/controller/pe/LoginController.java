package com.dame.cn.controller.pe;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.dame.cn.beans.consts.JwtTokenConst;
import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.entities.User;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.beans.vo.ProfileResult;
import com.dame.cn.config.shiro.ShiroUtil;
import com.dame.cn.service.pe.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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
    public Result login(HttpServletResponse response, @RequestBody Map<String, String> loginMap) {
        String token = userService.login(loginMap);
        // 将token放入header中，因为token会刷新。前端在处理时只需要检查响应header即可判断是否要更新token即可(request.js统一处理)。
        response.setHeader(JwtTokenConst.TOKEN_KEY, token);
        return new Result(ResultCode.SUCCESS);
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
        ProfileResult profileResult = userService.getUserProfile();
        return new Result(ResultCode.SUCCESS, profileResult);
    }

    /**
     * 用户登出
     * <p>
     * 没有直接在拦截时使用shiro自带的logout过滤器，因为：
     * 1、因为默认登出会跳转到自定义或默认("/")的URL中，而这里需要返回 ResponseBody到前端。
     * 2、我们禁用了SessionDao, 自带的 logoutFilter处理时，无法通过cookie中的sessionId找到认证主体，
     * 即 SecurityUtils.getSubject().getPrincipal()，
     * 所以这里需要这样处理，先被自定义的ShiroJwtFilter认证，放入Principal,然后 logout()
     * <p>
     * 调用Shiro自带的logout方法，shiro-redis也会清空缓存
     */
    @PostMapping("/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 获取个人信息，前台 我的 功能
     */
    @PostMapping(value = "/profile")
    public Result getProfileInfo() {
        String userId = LoginUserContext.getCurrentUser().getUserId();
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
    public Result editPassWord(@RequestBody Map<String, Object> map) {
        String userId = LoginUserContext.getCurrentUser().getUserId();
        User user = userService.getById(userId);
        if (null == user || !ShiroUtil.checkMd5Password(MapUtil.getStr(map, "originPwd"), ShiroUtil.salt, user.getPassword())) {
            throw new BizException(ResultCode.PWD_ERROR);
        }
        String pwd = ShiroUtil.md5(MapUtil.getStr(map, "newPwd"), ShiroUtil.salt);
        user.setPassword(pwd);
        user.setLastPwdModifiedTime(new Date());
        userService.updateById(user);
        return new Result(ResultCode.SUCCESS);
    }
}

