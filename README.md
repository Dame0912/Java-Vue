# 3.0.0 分支

### 一、说明

> **基于 分支1.0.0 改造**



### 二、新增功能

> **1、DataURL方式上传**
>
> **2、阿里云OSS后台上传** 
>
> **3、阿里云OSS后台签名，前台直传**



### 三、启动

> **1、导入 java-vue.sql**



### 四、DataURL方式

#### 4.1 DataURL概述
​		所谓DataURL是指"data"类型的Url格式，目的是对于一些 **“小”** 的数据，可以在网页中直接嵌入，而不是从外部文件载入。

#### 4.2 DataURL语法
* 完整的DataURL语法：DataURL= **data:mediatype;base64,<Base64编码的数据>**。
* mediatype：表述传递的数据的MIME类型（text/html，image/png，image/jpg）
* 简单的说，data类型的Url大致有下面几种形式。

```javascript
    data:,<文本数据>
    data:text/plain,<文本数据>
    data:text/html,<html代码>
    data:text/html;base64,<base64编码的html代码>
    data:text/css,<css代码>
    data:text/css;base64,<base64编码的css代码>
    data:text/javascript,<javascript代码>
    data:text/javascript;base64,<base64编码的javascript代码>
    data:image/gif;base64,base64编码的gif图片数据
    data:image/png;base64,base64编码的png图片数据
    data:image/jpg;base64,base64编码的jpg图片数据
    data:image/jpeg;base64,base64编码的jpeg图片数据
    data:image/x-icon;base64,base64编码的icon图片数据
```

* 格式

  ```javascript
  <image src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALwAAAA5CAYAAACMNEHAAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFn
  
  	.... 省略一大长串
      
  adGYvk0xqJOCn0dufljVHJjWZ0gDgmHTonoE9k5ps4TFhg8msqVn1Z1LV8j8BBgDJTf+jYXO34QAAAABJRU5ErkJggg==">
  ```

  

#### 4.3 优缺点

* 浏览器支持
  * 几乎所有的现代浏览器都支持Data URL格式，包括IE8也支持，但有部分限制，IE9完全支持。

* 数据容量
  * Base64编码的数据体积是原数据的体积4/3，也就是DataURL形式的图片会比二进制格式的图片体积大1/3。

* 使用场景
  * DataURL形式的数据不会占用HTTP会话，所以在访问外部资源或当图片是在服务器端用程序动态生成时借用DataURL是一个不错的选择。

#### 4.4 实现

##### 4.4.1 后端

​	**数据库字段要使用：mediumtext，因为太长了**

```java
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
```



##### 4.4.2 前端

```javascript
<el-form>
  <el-form-item>
	<i>请使用 .jpg 格式图片进行测试：</i><br>
	<input type="file" @change="getFile($event)"/>
  </el-form-item>
  <el-form-item>
	<el-button type="primary" @click="dataUrl_Upload">提交</el-button>
  </el-form-item>
</el-form>


getFile(event) {
  this.dataUrlForm.avatarFile = event.target.files[0];
},
dataUrl_Upload() {
  let formData = new window.FormData();
  formData.append("avatarFile", this.dataUrlForm.avatarFile)
  OssApi.upload_dataUrl(formData).then(res => {
	console.log("上传成功")
  })
}
```



### 五、阿里云OSS后台上传

#### 5.1 开通OSS

#### 5.2 创建Bucket

> 选择：**标准存储、公共读**

#### 5.3 参考SDK文档完成上传

```java
@PostMapping(value = "/oss/back")
public Result upload_oss_back(@RequestParam("avatarFile") MultipartFile file) 

@RequestParam("avatarFile")  和前端传递的保持一致即可    
```



### 六、阿里云OSS后台签名，前台直传

> 优点：
>
> * 不用通过后端服务器，增加了效率
> * 前端获取后台的签名数据进行上传，密钥在服务器保存，安全

#### 6.1 服务端签名后直传原理

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E6%9C%8D%E5%8A%A1%E7%AB%AF%E7%AD%BE%E5%90%8D%E5%90%8E%E7%9B%B4%E4%BC%A0%E5%8E%9F%E7%90%86.png?raw=true" />

#### 6.2 服务端签名

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E6%9C%8D%E5%8A%A1%E7%AB%AF%E7%AD%BE%E5%90%8D%E5%8F%82%E8%80%83.png?raw=true" />

#### 6.3 Bucket跨域配置

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E8%B7%A8%E5%9F%9F%E9%85%8D%E7%BD%AE%E4%BD%8D%E7%BD%AE.png?raw=true" />

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E8%B7%A8%E5%9F%9F%E9%85%8D%E7%BD%AE.png?raw=true" />

#### 6.4 前端上传参数

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/PostObject%E5%89%8D%E7%AB%AF%E4%B8%8A%E4%BC%A0%E5%8F%82%E6%95%B0.png?raw=true" />



### 七、相关代码

#### 7.1 后台

```java
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {
    // 区域
    private String endpoint;
    // 密钥ID
    private String accessKeyId;
    // 密钥
    private String accessKeySecret;
    // 使用的 Bucket
    private String bucketName;
	// 文件存放的根目录，自定义的，方便文件的管理
    private String fileHost;
	// 签名过期时间（秒）
    private Integer expire;
	// 最大允许（MB）
    private Integer maxSize;
}
```



```java
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
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret;
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
    public static OssPolicyResult policy() {
        OssPolicyResult ossPolicyResult = new OssPolicyResult();
        // 存储目录,如：  avatar/2020/04/28
        String timePath = DateUtil.format(new Date(), "yyyy/MM/dd");
        String dir = ossUtil.ossProperties.getFileHost() + "/" + timePath;

        // 签名有效期
        long expireEndTime = System.currentTimeMillis() + expire * 1000;
        Date expiration = new Date(expireEndTime);

        // 文件大小(B)
        long maxSize = maxSize * 1024 * 1000;

        // 提交地址
        String host = "http://" + bucketName + "." + endpoint;
        
        try {
            PolicyConditions pc = new PolicyConditions();
            pc.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
            pc.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = ossClient.generatePostPolicy(expiration, pc);
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
    public static String upload(MultipartFile file) {
        String fileURL;
        try {
            // 判断Bucket是否存在
            if (!ossClient.doesBucketExist(BucketName) {
                // 创建Bucket
                ossClient.createBucket(BucketName;
                //设置oss实例的访问权限：公共读
                ossClient.setBucketAcl(BucketName, CannedAccessControlList.PublicRead);
            }

            // 更改文件名，防止覆盖
            String of = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString();
            String fileType = of.substring(of.lastIndexOf("."));
            String newFileName = fileName + fileType;
            // 生成时间路径
            String timePath = DateUtil.format(new Date(), "yyyy/MM/dd");
            // 生成最终文件路径：如：  avatar/2020/04/28/OUOJ-KJYJHJH-KJLJL.jpg
            String filePath = FileHost + "/" + timePath + "/" + newFileName;

            // 获取上传文件流
            InputStream inputStream = file.getInputStream();
            //文件上传至阿里云
            ossClient.putObject(BucketName, filePath, inputStream);

            // 获取文件url地址
            fileURL = "http://" + BucketName + "." + Endpoint + "/" + filePath;
        } catch (Exception e) {
            throw new BizException(ResultCode.OSS_UPLOAD_ERROR);
        }
        return fileURL;
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
```



#### 7.2 前台

```html
<el-upload
  :action="dataObj.host"<!-- 上传的地址 -->
  :data="dataObj"<!-- 上传携带的数据 -->
  list-type="picture"
  :multiple="false"
  :show-file-list="true"
  :file-list="fileList"
  :on-remove="handleRemove"
  :before-upload="beforeUpload"<!-- 上传前执行的方法 -->
  :on-success="handleUploadSuccess"><!-- 上传成功执行的方法 -->

  <el-button size="small" type="primary">点击上传</el-button>
  <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
</el-upload>
```

```javascript
// 上传携带的数据
dataObj: {
  policy: '',
  signature: '',
  OSSAccessKeyId: '',
  key: '',
  host: '',
  dir: '',
},
```

```javascript
beforeUpload(file) {
	let _self = this;
	return new Promise((resolve, reject) => {
      // 请求后台获取签名，上传地址等数据  
	  upload_oss_policy().then(response => {
		_self.dataObj.policy = response.data.policy;
		_self.dataObj.signature = response.data.signature;
		_self.dataObj.OSSAccessKeyId = response.data.accessKeyId;
        // ${filename}，oss后读取为文件名，这里最好修改为UUID形式，防止覆盖
		_self.dataObj.key = response.data.dir + '/${filename}';
		_self.dataObj.dir = response.data.dir;
		_self.dataObj.host = response.data.host;
		resolve(true)
	  }).catch(err => {
		console.log(err)
		reject(false)
	  })
	})
 },
      
handleUploadSuccess(res, file) {
    // 文件地址: this.dataObj.host + '/' + this.dataObj.dir + '/' + file.name
	this.fileList.push({name: file.name, url: this.dataObj.host + '/' + this.dataObj.dir + '/' + file.name});
	this.emitInput(this.fileList[0].url);
}
```



##### 7.2.1 前端签名上传，遇到的一些坑（使用的 ImageCropper 组件）

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E5%89%8D%E7%AB%AF%E5%9D%911.png?raw=true" >

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E5%89%8D%E7%AB%AF%E5%9D%912.png?raw=true" />

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E5%89%8D%E7%AB%AF%E5%9D%913.png?raw=true" />

![坑4](https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E5%89%8D%E7%AB%AF%E5%9D%914.png?raw=true)

<img src="https://github.com/Dame0912/Java-Vue/blob/3.0.0/md%E5%9B%BE%E7%89%87/%E5%89%8D%E7%AB%AF%E5%9D%915.png?raw=true" />

































