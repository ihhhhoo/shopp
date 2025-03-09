package com.hyb.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.vo.LoginVo;
import com.hyb.seckill.vo.RespBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author hyb
 * @Date 2025/3/8 17:30
 * @Version 1.0
 */
public interface UserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request,
                     HttpServletResponse response);


    //根据cookie获取用户
    User getUserByCookie(String userTicket, HttpServletRequest request,HttpServletResponse
                         response);
}
