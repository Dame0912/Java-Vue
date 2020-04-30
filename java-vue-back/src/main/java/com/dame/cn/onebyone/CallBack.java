package com.dame.cn.onebyone;

/**
 * @author LYQ
 * @description 回调接口
 * @since 2020/4/20 21:13
 **/
public interface CallBack<T> {

    /**
     * 调用
     *
     * @return 结果
     */
    T invoke();

}