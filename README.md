# Shiro-Jwt-Vue

### 一、启动

1. 执行 shiro_jwt.sql 文件，启动前后台，即可使用



### 二、功能

#### 1、前台

##### ①完成的功能

* 页面的基本框架

* 完成用户、角色、权限的增删改查

 * 权限方面，完成了，菜单、按钮的控制

   

##### ②页面样式展示

<img src="D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\登陆页面.png" alt="图片" style="zoom:50%;" />



<img src="D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\首页.png" alt="首页" style="zoom:60%;" />

![用户管理](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\用户管理.png)

![用户管理角色分配](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\用户管理角色分配.png)

![角色管理](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\角色管理.png)

![角色管理权限分配](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\角色管理权限分配.png)

![权限管理](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\权限管理.png)





##### ③菜单权限控制功能实现

![菜单实现](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\菜单权限生成逻辑.png)



##### ④按钮权限控制功能实现

![按钮权限](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\按钮权限生成逻辑.png)





#### 2、后端

##### ①数据库表设计

![总表](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\数据库表.png)

>co_user：用户表
>pe_user_role：用户角色关联表
>pe_role：角色表
>pe_role_permission：角色权限关联表
>pe_permission：权限表
>
>它们关系都是 多对多



>pe_permission_api：api权限拓展表
>pe_permission_menu：菜单权限拓展表
>pe_permission_point：按钮权限拓展表
>这三张表，目前项目没有使用，他们和pe_permission是一对一关系，公用主键



##### ②权限表(pe_permission)设计说明

![权限表](D:\2_WorkSpace\1_Project\4_GitHub\Shiro-Jwt-Vue\md图片\数据库表—权限表.png)



##### ③完成功能

* 权限的增删改查
* 用户的登陆、注销、密码修改

##### ④采用的技术

	* springboot
	* mybatis plus
	* druid（含控制台）
	* logback-spring
	* jwt











































