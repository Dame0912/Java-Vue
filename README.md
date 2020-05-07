# 3.0.1 分支

### 一、说明

> **基于 分支3.0.0 改造**



### 二、新增功能

> **1、Excel常规导入和导出操作**
>
> **2、Excel根据提供的模板样式构建新Excel** 
>
> **3、Excel百万数据的导入和导出处理**



### 三、启动

> **1、导入 java-vue.sql**
>
> **2、application.yml 中将  aliyun.oss 配置换成自己的**



### 四、Excel简介

#### 4.1 Excel的两种形式

​		目前世面上的Excel分为两个大的版本Excel2003和Excel2007及以上两个版本，两者之间的区别如下：

|               | Excel2003                                | Excel2007                           |
| ------------- | ---------------------------------------- | ----------------------------------- |
| 文件后缀      | xls                                      | xlsx                                |
| 结构          | 二进制格式，核心结构是复合文档类型的结构 | XML 类型的结构                      |
| 单Sheet数据量 | 行：65535；<br /> 列：256                | 行：1048576；<br /> 列：16384       |
| 特点          | 存储容量有限                             | 基于XML压缩，占用空间小，操作效率高 |



#### 4.2 核心类

* **2003：HSSFWorkbook**
* **2007：XSSFWorkbooK**



#### 4.3 基本API

|    API    | 说明                                                         |
| :-------: | :----------------------------------------------------------- |
| Workbook  | Excel的文档对象,针对不同的Excel类型分为：HSSFWorkbook（2003）和 XSSFWorkbooK（2007） |
|   Sheet   | Excel的表单                                                  |
|    Row    | Excel的行                                                    |
|   Cell    | Excel的格子单元                                              |
|   Font    | Excel字体                                                    |
| CellStyle | 格子单元样式                                                 |



### 五、后台

#### 5.1 利用原生POI

> HSSFWorkbook（2003）和 XSSFWorkbooK（2007）

#### 5.2 利用Hutool工具包

> 它是对POI的再次封装，操作更简单



### 六、导出功能

#### 6.1 后台

```java
@GetMapping(value = "/export")
public void exportExcel(HttpServletRequest request, HttpServletResponse response)  {
	// 1、生成Excel数据
	ExcelWriter writer = ExcelUtil.getWriter(true);
	...

	// 2、完成下载
	String fileName = URLEncoder.encode("会员统计数据.xlsx", "UTF-8");
	response.setContentType("application/octet-stream");
	//保存的文件名,必须和页面编码一致,否则乱码
	response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("ISO8859-1")));
	response.setHeader("fileName", fileName);
	ServletOutputStream outputStream = response.getOutputStream();
	writer.flush(outputStream, true);
	writer.close();
	IoUtil.close(outputStream);
}
```



#### 6.2 前台

> **由于请求后台接口需要携带 Token**，直接使用 **a标签** 无法完成下载。

```html
<el-button type="success" plain @click="excelExport">导出</el-button>
```

```javascript
  excelExport() {
	ExcelApi.exportExcel() // 请求后台的下载Excel接口
	  .then(res => {
		const blob = new Blob([res.data])
		const fileName = decodeURIComponent(res.headers['filename'])
		const eLink = document.createElement('a')
		eLink.download = fileName
		eLink.style.display = 'none'
		eLink.href = URL.createObjectURL(blob)
		document.body.appendChild(eLink)
		eLink.click()// 自调用
		URL.revokeObjectURL(eLink.href)
		document.body.removeChild(eLink)
	  })
	  .catch(error => {
		console.log(error);
		this.$message.error('导出报表失败！')
	  })
  }
```



> **axios请求要携带响应类型， responseType: 'blob'**

```javascript
export function exportExcel() {
  return request({
    url: '/excel/user/export',
    method: 'get',
    responseType: 'blob'
  })
}
```



> **后台接口为void，统一拦截request.js需要变更**

```javascript
response => {
    const res = response.data

    if(res.code === undefined){
      return response
    }
	...
```



































