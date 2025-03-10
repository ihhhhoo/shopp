package com.hyb.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hyb.seckill.pojo.Order;
import com.hyb.seckill.pojo.SeckillOrder;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.GoodsService;
import com.hyb.seckill.service.OrderService;
import com.hyb.seckill.service.SeckillOrderService;
import com.hyb.seckill.vo.GoodsVo;
import com.hyb.seckill.vo.RespBeanEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @Author hyb
 * @Date 2025/3/9 22:18
 * @Version 1.0
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private SeckillOrderService seckillOrderService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;

    // @RequestMapping(value = "/doSeckill")
    // public String doSeckill(Model model, User user, Long goodsId) {
    //     System.out.println("-----秒杀 V1.0--------");
    //     //===================秒杀 v1.0 start =========================
    //     if (user == null) {
    //         return "login";
    //     }
    //     model.addAttribute("user", user);
    //     GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
    //     //判断库存
    //     if (goodsVo.getStockCount() < 1) {
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return "secKillFail";
    //     }
    //     //解决重复抢购
    //     SeckillOrder seckillOrder = seckillOrderService.getOne(new
    //                     QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
    //                     .eq("goods_id", goodsId));
    //     if (seckillOrder != null) {
    //         model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return "secKillFail";
    //     }
    //     //抢购
    //     Order order = orderService.seckill(user, goodsVo);
    //     if (order == null) {
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return "secKillFail";
    //     }
    //     model.addAttribute("order", order);
    //     model.addAttribute("goods", goodsVo);
    //     return "orderDetail";
    //     //===================秒杀 v1.0 end... =========================
    //
    // }

    @RequestMapping(value = "/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        System.out.println("-----秒杀 V2.0--------");
        //===================秒杀 v1.0 start =========================
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //解决重复抢购
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new
        //         QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
        //         .eq("goods_id", goodsId));
        // if (seckillOrder != null) {
        //     model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
        //     return "secKillFail";
        // }
            //解决重复抢购, 从 redis 中取出订单, 提高效率，如果有则已经抢购过了
        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get
                ("order:" + user.getId() + ":" + goodsVo.getId());
        if(o != null){
            //则已经抢购了
            model.addAttribute("errmsg",
                    RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }
        //抢购
        Order order = orderService.seckill(user, goodsVo);
        if (order == null) {
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";
        //===================秒杀 v1.0 end... =========================

    }
}
