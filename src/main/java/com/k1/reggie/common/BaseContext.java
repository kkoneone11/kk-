package com.k1.reggie.common;


import com.alibaba.druid.support.json.JSONUtils;

/**
 * 基于ThreadLocal封装工具类创建一个线程，用户保存和获取当前登陆用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


    //设置方法设置线程的值
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //设置方法获得线程的值
    public static Long getCurrentId() {
        return threadLocal.get();
    }


}
