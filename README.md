# 1.0.0 分支

### 一、启动

1. 执行 shiro_jwt.sql 文件，启动前后台，即可使用



### 二、功能

#### 1、前台

##### ①完成的功能

* 页面的基本框架

* 完成用户、角色、权限的增删改查

 * 权限方面，完成了，菜单、按钮的控制

   

##### ②页面样式展示

<img src="https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E7%99%BB%E9%99%86%E9%A1%B5%E9%9D%A2.png?raw=true" />



![首页](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E9%A6%96%E9%A1%B5.png?raw=true)

![用户管理](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E7%94%A8%E6%88%B7%E7%AE%A1%E7%90%86.png?raw=true)

![用户管理角色分配](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E7%94%A8%E6%88%B7%E7%AE%A1%E7%90%86%E8%A7%92%E8%89%B2%E5%88%86%E9%85%8D.png?raw=true)

![角色管理](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E8%A7%92%E8%89%B2%E7%AE%A1%E7%90%86.png?raw=true)

![角色管理权限分配](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E8%A7%92%E8%89%B2%E7%AE%A1%E7%90%86%E6%9D%83%E9%99%90%E5%88%86%E9%85%8D.png?raw=true)

![权限管理](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E6%9D%83%E9%99%90%E7%AE%A1%E7%90%86.png?raw=true)





##### ③菜单权限控制功能实现

![菜单实现](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E8%8F%9C%E5%8D%95%E6%9D%83%E9%99%90%E7%94%9F%E6%88%90%E9%80%BB%E8%BE%91.png?raw=true)



##### ④按钮权限控制功能实现

![按钮权限](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E6%8C%89%E9%92%AE%E6%9D%83%E9%99%90%E7%94%9F%E6%88%90%E9%80%BB%E8%BE%91.png?raw=true)





#### 2、后端

##### ①数据库表设计

![总表](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E6%95%B0%E6%8D%AE%E5%BA%93%E8%A1%A8.png?raw=true)

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

![权限表](https://github.com/Dame0912/Shiro-Jwt-Vue/blob/1.0.0/md%E5%9B%BE%E7%89%87/%E6%95%B0%E6%8D%AE%E5%BA%93%E8%A1%A8%E2%80%94%E6%9D%83%E9%99%90%E8%A1%A8.png?raw=true)



##### ③完成功能

* 权限的增删改查
* 用户的登陆、注销、密码修改

##### ④采用的技术

```java
* springboot
* mybatis plus
* druid（含控制台）
* logback-spring
* jwt
* filter
* 跨域
* ThreadLocal
```











































