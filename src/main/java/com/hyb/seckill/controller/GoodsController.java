package com.hyb.seckill.controller;

import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.GoodsService;
import com.hyb.seckill.service.UserService;
import com.hyb.seckill.vo.GoodsVo;
import com.hyb.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisTemplate redisTemplate;
    //手动渲染
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    //跳转到商品列表页
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody//使用了 redis 缓存页面需要添加
    public String toList(Model model, User user,
                         HttpServletRequest request,
                         HttpServletResponse response){
        //先从 redis 中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasText(html)) {
            return html;
        }


        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
        //展示商品
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        WebContext webContext = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (StringUtils.hasText(html)) {
            //每 60s 更新一次 redis 页面缓存, 即 60s 后, 该页面缓存失效, Redis 会清除该页面缓存
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }


    //跳转商品详情页面
    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long goodsId,
                           HttpServletRequest request,
                           HttpServletResponse response){

        //使用页面缓存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (StringUtils.hasText(html)) {
            return html;
        }

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
        //如果为 null，手动渲染，存入 redis 中
        WebContext webContext = new WebContext(request, response, request.getServletContext()
                , request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine()
                .process("goodsDetail", webContext);
        if (StringUtils.hasText(html)) {
            //设置每 60s 更新一次缓存, 即 60s 后, 该页面缓存失效, Redis 会清除该页面缓存
            valueOperations.set("goodsDetail:" +
                    goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;

    }



}
