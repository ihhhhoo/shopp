package com.hyb.seckill.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author hyb
 * @Date 2025/3/8 16:54
 * @Version 1.0
 */
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }
    private static final String salt = "UCmP7xHA";//加盐
    public static String inputPassToMidPass(String inputPass) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    //盐随机生成完成的是md5(md5(pass + salt1) + salt2)
    public static String midPassToDBPass(String midPass,String salt) {
        String str = salt.charAt(1) + midPass + salt.charAt(5);
        return md5(str);
    }

    /**
     * 进行两次加密加盐
     最后存到数据库的 md5（ md5（pass+salt1）+salt2）
     * salt1 是前端进行的
     * salt2 是后端进行的随机生成
     */
    public static String inputPassToDBPass(String inputPass,String salt) {
        String midPass = inputPassToMidPass(inputPass);
        String dbPass = midPassToDBPass(midPass, salt);
        return dbPass;
    }
}
