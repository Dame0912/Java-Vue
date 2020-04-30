package com.dame.cn.onebyone;

/**
 * @author LYQ
 * @description 防止并发控制
 * @since 2020/4/20 21:13
 **/
public interface OneByOneService {

	/**
	 * 执行方法体
	 *
	 * @param oneByOne 一个接一个处理记录
	 * @param callBack 回调
	 * @return 执行结果
	 */
	<T> T execute(OneByOne oneByOne, CallBack<T> callBack);
}
