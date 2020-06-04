package com.dame.cn.config.security.utils;

import com.alibaba.fastjson.JSON;
import com.dame.cn.beans.dto.SysUser;
import com.dame.cn.beans.response.Result;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author LYQ
 * @description TODO
 * @since 2020-06-03
 **/
public class SecurityUtils {

    public static String getUserId() {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return sysUser.getId();
    }

    public static String getUserName() {
        SysUser sysUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return sysUser.getUsername();
    }

    public static void responseResult(HttpServletResponse response, Result result) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(result));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
