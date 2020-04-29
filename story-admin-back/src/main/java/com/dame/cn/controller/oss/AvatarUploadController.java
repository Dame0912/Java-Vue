package com.dame.cn.controller.oss;

import com.dame.cn.beans.dto.LoginUserContext;
import com.dame.cn.beans.response.Result;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.oss.OssUtil;
import com.dame.cn.service.oss.AvatarUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LYQ
 * @description 文件上传
 * @since 2020/4/28 10:20
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/upload/")
public class AvatarUploadController {

    @Autowired
    private AvatarUploadService avatarUploadService;

    /**
     * 以 DataURL 格式保存图片
     */
    @PostMapping(value = "/dataUrl")
    public Result upload_dataUrl(@RequestParam("avatarFile") MultipartFile file) {
        String userId = LoginUserContext.getCurrentUser().getUserId();
        String image = avatarUploadService.upload_dataUrl(userId, file);
        return new Result(ResultCode.SUCCESS, image);
    }

    /**
     * 后台以流的形式上传文件
     */
    @PostMapping(value = "/oss/back")
    public Result upload_oss_back(@RequestParam("avatarFile") MultipartFile file) {
        String userId = LoginUserContext.getCurrentUser().getUserId();
        String image = avatarUploadService.upload_oss_back(userId, file);
        return new Result(ResultCode.SUCCESS, image);
    }

    /**
     * 前端获取后端服务器生成的传输密钥，然后上传
     */
    @PostMapping(value = "/oss/policy")
    public Result upload_oss_policy() {
        OssUtil.OssPolicyResult ossPolicyResult = avatarUploadService.upload_oss_policy();
        return new Result(ResultCode.SUCCESS, ossPolicyResult);
    }

}
