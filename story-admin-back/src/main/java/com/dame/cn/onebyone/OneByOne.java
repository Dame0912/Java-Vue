package com.dame.cn.onebyone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneByOne {

	/**
	 * 业务类型
	 */
	private String bizType;

	/**
	 * 业务ID
	 */
	private String bizId;

	/**
	 * 方法
	 */
	private String method;

	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 描述
	 */
	private String description;
	
	/**
	 * 创建一个接一个处理记录
	 * 
	 * @param bizType 业务类型
	 * @param bizId   业务ID
	 * @param method  方法
	 */
	public OneByOne(String bizType, String bizId, String method) {
		this.bizType = bizType;
		this.bizId = bizId;
		this.method = method;
	}

}
