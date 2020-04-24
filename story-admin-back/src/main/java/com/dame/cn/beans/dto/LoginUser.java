package com.dame.cn.beans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LYQ
 * @description TODO
 * @since 2020/4/24 10:46
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {
    private String userId; // 用户ID
    private String username; // 用户名称
}
