package com.hyb.seckill.config;

import com.hyb.seckill.pojo.User;

/**
 * @Author hyb
 * @Date 2025/3/13 9:44
 * @Version 1.0
 */
public class UserContext {
    //每个线程都有自己的ThreadLocal,把共享数据存放到这里，保证线程安全
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user){
        userThreadLocal.set(user);
    }

    public static User getUser(){
        return userThreadLocal.get();
    }
}
