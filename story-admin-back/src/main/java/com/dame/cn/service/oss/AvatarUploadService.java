package com.dame.cn.service.oss;

import com.dame.cn.config.oss.OssUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/28 10:29
 **/
public interface AvatarUploadService {

    String upload_dataUrl(String userId, MultipartFile file);

    String upload_oss_back(String userId, MultipartFile file);

    OssUtil.OssPolicyResult upload_oss_policy();
}
