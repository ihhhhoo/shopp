package com.hyb.seckill.controller;

import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.GoodsService;
import com.hyb.seckill.service.UserService;
import com.hyb.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

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

    @Autowired
    private GoodsService goodsService;

    //跳转到商品列表页
    @RequestMapping(value = "/toList")
    public String toList(Model model, User user){
        // //如果cookie没有生成ticket，则跳转到登录页
        // if(!StringUtils.hasText(ticket)){
        //     return "login";
        // }
        // // User user = (User)session.getAttribute(ticket);
        // User user = userService.getUserByCookie(ticket,request,response);
        //如果用户没有登录

        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
        //展示商品
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        System.out.println("-------"+goodsService.findGoodsVo());
        return "goodsList";

    }


    //跳转商品详情页面
    @RequestMapping(value = "/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId){
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        //============处理秒杀倒计时和状态 start ==============
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            //秒杀还没有开始
        remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
    } else if (nowDate.after(endDate)) {
            //秒杀结束
        secKillStatus = 2;//状态变成2 秒杀结束
        remainSeconds = -1;//倒计时为-1
    } else {
            //秒杀进行中
        secKillStatus = 1;//1  秒杀进行中
        remainSeconds = 0;//秒杀开始倒计时为0
    }
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        //============处理秒杀倒计时和状态 end ==============

        model.addAttribute("goods",goodsVo);
        return "goodsDetail";
    }

}
