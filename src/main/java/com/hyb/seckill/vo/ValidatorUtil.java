package com.hyb.seckill.vo;


import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @Author hyb
 * @Date 2025/3/8 17:28
 * @Version 1.0
 */
//手机校验
public class ValidatorUtil {
    //手机号校验
    private static final Pattern mobile_pattern = Pattern.compile("[1]([3-9])[0-9]{9}$");

    public static boolean isMobile(String mobile){
        if(!StringUtils.hasText(mobile)){
            return false;
        }
        return mobile_pattern.matcher(mobile).matches();//返回校验结果true为正确
    }
}
