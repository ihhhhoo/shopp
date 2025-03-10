package com.hyb.seckill.util;


import com.hyb.seckill.mapper.UserMapper;
import com.hyb.seckill.pojo.User;
import org.junit.jupiter.api.Test;

/**
 * @Author hyb
 * @Date 2025/3/8 17:07
 * @Version 1.0
 */
public class MD5UtilTest {

    private UserMapper userMapper;

    @Test
    public void t1(){
        //1. 12345 就是用户输入的密码, inputPassToMidPass() 返回的是中间密码
        //, 中间密码是为了增强安全性, 防止传输的密码被网络拦截设计的
        //2. 中间密码也是前端经过 md5() 计算得到, 并通过网络发送给服务器的
        //3. 也就是说, 我们发送给服务其后端的密码是先经过加密,
        //再通过网络发给服务器的, 并不是发送的
        //用户原生密码
        System.out.println(MD5Util.inputPassToMidPass("123456"));
        //4. midPassToDBPass() 方法通过中间密码 得到存在数据库的密码
        System.out.println(MD5Util.midPassToDBPass("75d7eacdc4a31d0c8c939112ae5880cf",
                        "UCmP7xHA"));
        //5. inputPassToDBPass 方法是通过用户原生密码,得到存在数据库的密码
        System.out.println(MD5Util.inputPassToDBPass("123456", "UCmP7xHA"));
    }


    @Test
    void t2(){
        //插入一个值为19944595352和12345
        User user = new User();
        user.setId(19944595352L);

        user.setPassword(MD5Util.inputPassToDBPass("123456","UCmP7xHA"));
        System.out.println(user);
        userMapper.insert(user);
    }


}

