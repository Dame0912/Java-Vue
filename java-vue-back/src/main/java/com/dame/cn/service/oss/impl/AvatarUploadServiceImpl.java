package com.dame.cn.service.oss.impl;

import com.dame.cn.beans.entities.User;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.oss.OssUtil;
import com.dame.cn.mapper.UserMapper;
import com.dame.cn.service.oss.AvatarUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;


/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/28 10:30
 **/
@Service
public class AvatarUploadServiceImpl implements AvatarUploadService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public String upload_dataUrl(String userId, MultipartFile file) {
        try {
            // 对上传文件进行 Base64 编码
            String s = Base64.getEncoder().encodeToString(file.getBytes());
            // 拼接DataUrl数据头, image/jpg 只是举例，不是所有都是这个
            String dataUrl = "data:image/jpg;base64," + s;
            // 保存用户
            User user = new User().setId(userId).setAvatar(dataUrl);
            userMapper.updateById(user);
            return dataUrl;
        } catch (IOException e) {
            throw new BizException(ResultCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public String upload_oss_back(String userId, MultipartFile file) {
        String fileURL = OssUtil.upload(file);
        // 保存用户
        User user = new User().setId(userId).setAvatar(fileURL);
        userMapper.updateById(user);
        return fileURL;
    }

    @Override
    public OssUtil.OssPolicyResult upload_oss_policy() {
        return OssUtil.policy(true);
    }
}
