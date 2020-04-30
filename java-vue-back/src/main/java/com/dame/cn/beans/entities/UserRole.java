package com.dame.cn.beans.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author LYQ
 * @since 2020-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pe_user_role")
@NoArgsConstructor
@AllArgsConstructor
public class UserRole implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 权限ID
     */
    private String userId;


}
