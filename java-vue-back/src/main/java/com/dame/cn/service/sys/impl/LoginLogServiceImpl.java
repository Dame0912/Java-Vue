package com.dame.cn.service.sys.impl;

import com.dame.cn.beans.entities.LoginLog;
import com.dame.cn.mapper.LoginLogMapper;
import com.dame.cn.service.sys.LoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 登录日志 服务实现类
 * </p>
 *
 * @author LYQ
 * @since 2020-04-11
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

}
