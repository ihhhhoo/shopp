package com.hyb.seckill.controller;

import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.UserService;
import com.hyb.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author hyb
 * @Date 2025/3/10 11:31
 * @Version 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user, String address) {
        return RespBean.success(user);
    }

    @RequestMapping("/updatepwd")
    @ResponseBody
    public RespBean updatePassword(String userTicket,
                                   String password,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        return userService.updatePassword(userTicket, password, request,
                response);
    }
}
