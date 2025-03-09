package com.hyb.seckill.controller;

import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author hyb
 * @Date 2025/3/8 21:27
 * @Version 1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    //跳转到商品列表页
    @RequestMapping(value = "/toList")
    public String toList(Model model,
                         @CookieValue("userTicket") String ticket,
                         HttpServletRequest request, HttpServletResponse response){
        //如果cookie没有生成ticket，则跳转到登录页
        if(!StringUtils.hasText(ticket)){
            return "login";
        }
        // User user = (User)session.getAttribute(ticket);
        User user = userService.getUserByCookie(ticket,request,response);
        //如果用户没有登录
        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
        return "goodsList";

    }
}
