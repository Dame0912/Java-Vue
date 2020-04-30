/*
SQLyog Ultimate v10.00 Beta1
MySQL - 5.7.21-log : Database - java-vue
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`java-vue` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `java-vue`;

/*Table structure for table `co_department` */

DROP TABLE IF EXISTS `co_department`;

CREATE TABLE `co_department` (
  `id` varchar(40) NOT NULL,
  `pid` varchar(255) DEFAULT NULL COMMENT '父级部门ID',
  `company_id` varchar(255) DEFAULT '0' COMMENT '企业ID',
  `name` varchar(255) DEFAULT NULL COMMENT '部门名称',
  `code` varchar(255) DEFAULT NULL COMMENT '部门编码',
  `introduce` text COMMENT '介绍',
  `manager` varchar(40) DEFAULT NULL COMMENT '部门负责人',
  `manager_id` varchar(255) DEFAULT NULL COMMENT '负责人ID',
  `creator` varchar(255) DEFAULT NULL COMMENT '创建人',
  `editor` varchar(255) DEFAULT NULL COMMENT '修改人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `co_department` */

insert  into `co_department`(`id`,`pid`,`company_id`,`name`,`code`,`introduce`,`manager`,`manager_id`,`creator`,`editor`,`created_time`,`modified_time`) values ('1249309487725289472','0','0','行政部','XZB','管理公司行政','管理员','1249613010895708160','管理员','管理员','2020-04-12 20:12:47','2020-04-26 10:30:47'),('1249309626753884160','0','0','科技部','KJB','主管科技的','王五','1250737486240702464','管理员','管理员','2020-04-12 20:13:20','2020-04-24 15:45:11'),('1249309718567198720','0','0','财务部','CWB','管理财务的','王五','1250737486240702464','管理员','管理员','2020-04-12 20:13:42','2020-04-24 15:45:09'),('1249309851862179840','1249309487725289472','0','人事部','RSB','管理人事的','王五','1250737486240702464','管理员','管理员','2020-04-12 20:14:14','2020-04-24 15:45:07'),('1249310042602348544','1249309626753884160','0','开发部','KFB','开发的','李四','1250737307668209664','管理员','管理员','2020-04-12 20:14:59','2020-04-24 15:45:05'),('1249310161720582144','1249309626753884160','0','测试部','CSB','管理测试的','赵六','1250737604654292992','管理员','管理员','2020-04-12 20:15:28','2020-04-24 15:45:04'),('1249310266993418240','1249309626753884160','0','运维部','YWB','管理运维的','张三','1250737217079631872','管理员','管理员','2020-04-12 20:15:53','2020-04-24 15:45:03');

/*Table structure for table `co_user` */

DROP TABLE IF EXISTS `co_user`;

CREATE TABLE `co_user` (
  `id` varchar(40) NOT NULL COMMENT 'ID',
  `mobile` varchar(40) NOT NULL COMMENT '手机号码',
  `username` varchar(255) NOT NULL COMMENT '用户名称',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `status` int(2) NOT NULL DEFAULT '1' COMMENT '启用状态 0是禁用，1是启用',
  `department_id` varchar(40) DEFAULT NULL COMMENT '部门ID',
  `department_name` varchar(40) DEFAULT NULL COMMENT '部门名称',
  `last_pwd_modified_time` datetime DEFAULT NULL COMMENT '上次修改密码时间',
  `creator` varchar(255) DEFAULT NULL COMMENT '创建人',
  `editor` varchar(255) DEFAULT NULL COMMENT '修改人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_mobile` (`mobile`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `co_user` */

insert  into `co_user`(`id`,`mobile`,`username`,`password`,`status`,`department_id`,`department_name`,`last_pwd_modified_time`,`creator`,`editor`,`created_time`,`modified_time`) values ('1249613010895708160','13600000001','管理员','a4e24a4d31f8c8aee8cf346945b0890c',1,'1249309487725289472','行政部','2020-04-26 11:14:39','管理员','管理员','2020-04-13 16:18:53','2020-04-26 11:12:39'),('1250737217079631872','13600000002','张三','a4e24a4d31f8c8aee8cf346945b0890c',1,'1249309851862179840','人事部',NULL,'管理员','管理员','2020-04-16 18:46:04','2020-04-26 11:12:40'),('1250737307668209664','13600000003','李四','a4e24a4d31f8c8aee8cf346945b0890c',1,'1249309945688760320','后勤部',NULL,'管理员','管理员','2020-04-16 18:46:26','2020-04-26 11:12:42'),('1250737486240702464','13600000004','王五','a4e24a4d31f8c8aee8cf346945b0890c',1,'1249309718567198720','财务部',NULL,'管理员','管理员','2020-04-16 18:47:08','2020-04-26 11:12:43'),('1250737604654292992','13600000005','赵六','a4e24a4d31f8c8aee8cf346945b0890c',1,'1249310042602348544','开发部',NULL,'管理员','管理员','2020-04-16 18:47:37','2020-04-26 11:12:46');

/*Table structure for table `pe_permission` */

DROP TABLE IF EXISTS `pe_permission`;

CREATE TABLE `pe_permission` (
  `id` varchar(40) NOT NULL COMMENT '主键',
  `pid` varchar(40) DEFAULT '0' COMMENT '主键',
  `name` varchar(255) DEFAULT NULL COMMENT '权限名称',
  `type` tinyint(4) DEFAULT NULL COMMENT '权限类型 1为菜单 2为功能 3为API',
  `code` varchar(100) DEFAULT NULL COMMENT '权限标识，通过该字段进行区分',
  `description` text COMMENT '权限描述',
  `creator` varchar(255) DEFAULT NULL COMMENT '创建人',
  `editor` varchar(255) DEFAULT NULL COMMENT '修改人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_90280428` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pe_permission` */

insert  into `pe_permission`(`id`,`pid`,`name`,`type`,`code`,`description`,`creator`,`editor`,`created_time`,`modified_time`) values ('1250260032363442176','0','公司设置',1,'settings','公司设置菜单','管理员','管理员','2020-04-15 11:09:55','2020-04-26 10:31:16'),('1250260128098430976','1250260032363442176','部门管理',1,'settings-department','部门管理菜单','管理员','管理员','2020-04-15 11:10:17','2020-04-24 15:46:34'),('1250260211900624896','1250260032363442176','用户管理',1,'settings-user','用户管理菜单','管理员','管理员','2020-04-15 11:10:37','2020-04-24 15:46:36'),('1250260307782414336','1250260032363442176','角色管理',1,'settings-role','角色管理菜单','管理员','管理员','2020-04-15 11:11:00','2020-04-24 15:46:37'),('1250260435289255936','1250260032363442176','权限管理',1,'settings-permission','权限管理菜单','管理员','管理员','2020-04-15 11:11:31','2020-04-24 15:46:38'),('1250260988379541504','1250260211900624896','用户查询',2,'settings-user-select','用户查询按钮','管理员','管理员','2020-04-15 11:13:43','2020-04-24 15:46:39'),('1250261106428227584','1250260211900624896','用户新增',2,'settings-user-add','用户新增按钮','管理员','管理员','2020-04-15 11:14:11','2020-04-24 15:46:41'),('1250261232563531776','1250260211900624896','用户编辑',2,'settings-user-edit','用户编辑按钮','管理员','管理员','2020-04-15 11:14:41','2020-04-24 15:46:42'),('1250261360485609472','1250260211900624896','用户删除',2,'settings-user-delete','用户删除按钮','管理员','管理员','2020-04-15 11:15:11','2020-04-24 15:46:44'),('1250261830771945472','1250260988379541504','查询所有用户',3,'settings-user-findall','根据条件查询所有用户','管理员','管理员','2020-04-15 11:17:03','2020-04-24 15:46:46'),('1250262040352927744','1250261106428227584','新增用户',3,'settings-user-save','新增用户','管理员','管理员','2020-04-15 11:17:53','2020-04-24 15:46:47'),('1250262233982971904','1250261232563531776','查询用户',3,'settings-user-findById','查询指定用户','管理员','管理员','2020-04-15 11:18:39','2020-04-24 15:46:48'),('1250262364388077568','1250261232563531776','更新用户',3,'settings-user-update','更新指定用户','管理员','管理员','2020-04-15 11:19:11','2020-04-24 15:46:49'),('1250262516226076672','1250261360485609472','删除用户',3,'settings-user-remove','删除指定用户','管理员','管理员','2020-04-15 11:19:47','2020-04-24 15:46:51'),('1250262760586227712','1250260211900624896','用户角色',2,'settings-user-role','用户角色编辑','管理员','管理员','2020-04-15 11:20:45','2020-04-24 15:46:52'),('1250263062462869504','1250262760586227712','查询用户角色',3,'settings-user-findUserRole','查询指定用户的角色','管理员','管理员','2020-04-15 11:21:57','2020-04-24 15:46:54'),('1250263260362715136','1250262760586227712','用户分配角色',3,'settings-user-assignRoles','给指定用户分配角色','管理员','管理员','2020-04-15 11:22:44','2020-04-24 15:46:57');

/*Table structure for table `pe_permission_api` */

DROP TABLE IF EXISTS `pe_permission_api`;

CREATE TABLE `pe_permission_api` (
  `id` varchar(40) NOT NULL COMMENT '主键ID',
  `api_level` varchar(2) DEFAULT NULL COMMENT '权限等级，1为通用接口权限，2为需校验接口权限',
  `api_method` varchar(255) DEFAULT NULL COMMENT '请求类型',
  `api_url` varchar(255) DEFAULT NULL COMMENT '链接',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pe_permission_api` */

/*Table structure for table `pe_permission_menu` */

DROP TABLE IF EXISTS `pe_permission_menu`;

CREATE TABLE `pe_permission_menu` (
  `id` varchar(40) NOT NULL COMMENT '主键ID',
  `menu_icon` varchar(100) DEFAULT NULL COMMENT '权限代码',
  `menu_order` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pe_permission_menu` */

/*Table structure for table `pe_permission_point` */

DROP TABLE IF EXISTS `pe_permission_point`;

CREATE TABLE `pe_permission_point` (
  `id` varchar(40) NOT NULL COMMENT '主键ID',
  `point_class` varchar(100) DEFAULT NULL COMMENT '按钮类型',
  `point_icon` varchar(100) DEFAULT NULL COMMENT '按钮icon',
  `point_status` int(11) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pe_permission_point` */

/*Table structure for table `pe_role` */

DROP TABLE IF EXISTS `pe_role`;

CREATE TABLE `pe_role` (
  `id` varchar(40) NOT NULL COMMENT '主键ID',
  `name` varchar(40) DEFAULT NULL COMMENT '权限名称，唯一',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  `creator` varchar(255) DEFAULT NULL COMMENT '创建人',
  `editor` varchar(255) DEFAULT NULL COMMENT '修改人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_k3beff7qglfn58qsf2yvbg41i` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pe_role` */

insert  into `pe_role`(`id`,`name`,`description`,`creator`,`editor`,`created_time`,`modified_time`) values ('1250309872166432768','超级管理员','拥有所有的权限','管理员','管理员','2020-04-15 14:27:57','2020-04-26 10:31:24'),('1250313345037561856','用户管理员','用户管理所有权限','管理员','管理员','2020-04-15 14:41:45','2020-04-24 15:45:44'),('1250313534662045696','用户管理无授权员','用户管理没有授权权限','管理员','管理员','2020-04-15 14:42:31','2020-04-24 15:45:45'),('1250313641482579968','用户管理查询员','用户管理查询的权限','管理员','管理员','2020-04-15 14:42:56','2020-04-24 15:45:46'),('1250313736466788352','普通游客','没有公司设置菜单下的权限','管理员','管理员','2020-04-15 14:43:19','2020-04-24 15:45:49');

/*Table structure for table `pe_role_permission` */

DROP TABLE IF EXISTS `pe_role_permission`;

CREATE TABLE `pe_role_permission` (
  `role_id` varchar(40) NOT NULL COMMENT '角色ID',
  `permission_id` varchar(40) NOT NULL COMMENT '权限ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pe_role_permission` */

insert  into `pe_role_permission`(`role_id`,`permission_id`) values ('1250309872166432768','1250260032363442176'),('1250309872166432768','1250260128098430976'),('1250309872166432768','1250260211900624896'),('1250309872166432768','1250260988379541504'),('1250309872166432768','1250261106428227584'),('1250309872166432768','1250261232563531776'),('1250309872166432768','1250261360485609472'),('1250309872166432768','1250262760586227712'),('1250309872166432768','1250260307782414336'),('1250309872166432768','1250260435289255936'),('1250309872166432768','1250261830771945472'),('1250309872166432768','1250262233982971904'),('1250309872166432768','1250262040352927744'),('1250309872166432768','1250262364388077568'),('1250309872166432768','1250262516226076672'),('1250309872166432768','1250263260362715136'),('1250309872166432768','1250263062462869504'),('1250313345037561856','1250260211900624896'),('1250313345037561856','1250260988379541504'),('1250313345037561856','1250261106428227584'),('1250313345037561856','1250261232563531776'),('1250313345037561856','1250261360485609472'),('1250313345037561856','1250262760586227712'),('1250313345037561856','1250260032363442176'),('1250313345037561856','1250261830771945472'),('1250313345037561856','1250262040352927744'),('1250313345037561856','1250262364388077568'),('1250313345037561856','1250262516226076672'),('1250313345037561856','1250263260362715136'),('1250313345037561856','1250263062462869504'),('1250313345037561856','1250262233982971904'),('1250313534662045696','1250260988379541504'),('1250313534662045696','1250261106428227584'),('1250313534662045696','1250261232563531776'),('1250313534662045696','1250261360485609472'),('1250313534662045696','1250260032363442176'),('1250313534662045696','1250260211900624896'),('1250313534662045696','1250262233982971904'),('1250313534662045696','1250262364388077568'),('1250313534662045696','1250262516226076672'),('1250313534662045696','1250262040352927744'),('1250313534662045696','1250261830771945472'),('1250313641482579968','1250260988379541504'),('1250313641482579968','1250260032363442176'),('1250313641482579968','1250260211900624896'),('1250313641482579968','1250261830771945472');

/*Table structure for table `pe_user_role` */

DROP TABLE IF EXISTS `pe_user_role`;

CREATE TABLE `pe_user_role` (
  `role_id` varchar(40) NOT NULL COMMENT '角色ID',
  `user_id` varchar(40) NOT NULL COMMENT '权限ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pe_user_role` */

insert  into `pe_user_role`(`role_id`,`user_id`) values ('1250313641482579968','1249625982401531904'),('1250313534662045696','1249625982401531904'),('1250313345037561856','1249626348237115392'),('1250313736466788352','1249626348237115392'),('1250313736466788352','1249626878749528064'),('1250309872166432768','1249613010895708160'),('1250313345037561856','1250737217079631872'),('1250313534662045696','1250737307668209664'),('1250313641482579968','1250737486240702464'),('1250313736466788352','1250737604654292992');

/*Table structure for table `sys_login_log` */

DROP TABLE IF EXISTS `sys_login_log`;

CREATE TABLE `sys_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '用户名',
  `login_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `content` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '内容',
  `status` char(2) COLLATE utf8_bin DEFAULT NULL COMMENT '有效标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='登录日志';

/*Data for the table `sys_login_log` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
