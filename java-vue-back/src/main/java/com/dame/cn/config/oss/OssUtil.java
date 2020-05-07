package com.dame.cn.config.oss;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PolicyConditions;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.ResultCode;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/29 10:40
 **/
@Component
public class OssUtil {

    @Autowired
    private OssProperties ossProperties;
    @Autowired
    private OSS ossClient;

    private static OssUtil ossUtil;

    @Bean
    public OSS ossClient() {
        // 创建OSSClient实例。
        return new OSSClientBuilder().build(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
    }

    @PostConstruct
    public void init() {
        ossUtil = this;
        ossUtil.ossProperties = this.ossProperties;
        ossUtil.ossClient = this.ossClient;
    }

    /**
     * 获取OSS上传签名
     *
     * @return 签名数据
     */
    public static OssPolicyResult policy(boolean pathTime, String... fileHost) {
        OssPolicyResult ossPolicyResult = new OssPolicyResult();
        // 存储目录,如：  avatar/2020/04/28
        String timePath = DateUtil.format(new Date(), "yyyy/MM/dd");
        String dir;
        if (null != fileHost && fileHost.length > 0) {
            if (pathTime) {
                dir = fileHost[0] + "/" + timePath;
            } else {
                dir = fileHost[0];
            }

        } else {
            dir = ossUtil.ossProperties.getFileHost() + "/" + timePath;
        }

        // 签名有效期
        long expireEndTime = System.currentTimeMillis() + ossUtil.ossProperties.getExpire() * 1000;
        Date expiration = new Date(expireEndTime);

        // 文件大小(B)
        long maxSize = ossUtil.ossProperties.getMaxSize() * 1024 * 1000;

        // 提交地址
        String host = "http://" + ossUtil.ossProperties.getBucketName() + "." + ossUtil.ossProperties.getEndpoint();
        try {
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = ossUtil.ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            // 生成policy
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            // 生成签名
            String postSignature = ossUtil.ossClient.calculatePostSignature(postPolicy);

            ossPolicyResult.setAccessKeyId(ossUtil.ossProperties.getAccessKeyId());
            ossPolicyResult.setPolicy(encodedPolicy);
            ossPolicyResult.setSignature(postSignature);
            ossPolicyResult.setDir(dir);
            ossPolicyResult.setHost(host);

        } catch (Exception e) {
            throw new BizException(ResultCode.OSS_POLICY_ERROR);
        }
        return ossPolicyResult;
    }


    /**
     * 文件流上传
     *
     * @param file 文件
     * @return oss
     */
    public static String upload(MultipartFile file, String... fileHost) {
        String fileURL;
        try {
            // 判断Bucket是否存在
            if (!ossUtil.ossClient.doesBucketExist(ossUtil.ossProperties.getBucketName())) {
                // 创建Bucket
                ossUtil.ossClient.createBucket(ossUtil.ossProperties.getBucketName());
                //设置oss实例的访问权限：公共读
                ossUtil.ossClient.setBucketAcl(ossUtil.ossProperties.getBucketName(), CannedAccessControlList.PublicRead);
            }

            // 更改文件名，防止覆盖
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = fileName + fileType;
            // 生成时间路径
            String timePath = DateUtil.format(new Date(), "yyyy/MM/dd");
            // 生成最终文件路径：如：  avatar/2020/04/28/OUOJ-KJYJHJH-KJLJL.jpg
            String filePath;
            if (null != fileHost && fileHost.length > 0) {
                filePath = fileHost[0] + "/" + timePath + "/" + newFileName;
            } else {
                filePath = ossUtil.ossProperties.getFileHost() + "/" + timePath + "/" + newFileName;
            }

            // 获取上传文件流
            InputStream inputStream = file.getInputStream();
            //文件上传至阿里云
            ossUtil.ossClient.putObject(ossUtil.ossProperties.getBucketName(), filePath, inputStream);

            // 获取文件url地址
            fileURL = "http://" + ossUtil.ossProperties.getBucketName() + "." + ossUtil.ossProperties.getEndpoint() + "/" + filePath;
        } catch (Exception e) {
            throw new BizException(ResultCode.OSS_UPLOAD_ERROR);
        }
        return fileURL;
    }

    /**
     * 下载文件
     */
    public static InputStream downLoad(String objectName) {
        try {
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
            OSSObject ossObject = ossUtil.ossClient.getObject(ossUtil.ossProperties.getBucketName(), objectName);
            InputStream inputStream = ossObject.getObjectContent();
            return inputStream;

            // 读取文件内容。
//            BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
//            while (true) {
//                String line = reader.readLine();
//                if (line == null) break;
//            }
        } catch (Exception e) {
            throw new BizException(ResultCode.OSS_DOWNLOAD_ERROR);
        }
    }


    //获取OSS上传授权返回结果
    @Data
    public static class OssPolicyResult {
        private String accessKeyId;
        private String policy;
        private String signature;
        private String dir;
        private String host;
    }

}
