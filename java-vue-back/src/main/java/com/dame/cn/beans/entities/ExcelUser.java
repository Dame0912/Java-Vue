package com.dame.cn.beans.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * excel用户测试表
 * </p>
 *
 * @author LYQ
 * @since 2020-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExcelUser implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 姓名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 性别
     */
    private String sex;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 出生日期
     */
    private Date dateOfBirth;


}
