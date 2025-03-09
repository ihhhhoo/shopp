package com.hyb.seckill.util;

import java.util.UUID;

/**
 * @Author hyb
 * @Date 2025/3/8 21:07
 * @Version 1.0
 */
public class UUIDUtil {
    public static String uuid(){
        //把UUID中的-替换掉
        return UUID.randomUUID().toString().replace("-","");
    }
}
