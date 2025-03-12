package com.hyb.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hyb.seckill.pojo.Order;
import com.hyb.seckill.pojo.SeckillMessage;
import com.hyb.seckill.pojo.SeckillOrder;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.rabbitmq.MQSenderMessage;
import com.hyb.seckill.service.GoodsService;
import com.hyb.seckill.service.OrderService;
import com.hyb.seckill.service.SeckillOrderService;
import com.hyb.seckill.vo.GoodsVo;
import com.hyb.seckill.vo.RespBean;
import com.hyb.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author hyb
 * @Date 2025/3/9 22:18
 * @Version 1.0
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Resource
    private GoodsService goodsService;
    @Resource
    private SeckillOrderService seckillOrderService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;

    private HashMap<Long, Boolean> entryStockMap =
            new HashMap<>();

    @Resource
    private MQSenderMessage mqSenderMessage;

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

    // @RequestMapping(value = "/doSeckill")
    // public String doSeckill(Model model, User user, Long goodsId) {
    //     System.out.println("-----秒杀 V2.0--------");
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
    //     // SeckillOrder seckillOrder = seckillOrderService.getOne(new
    //     //         QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
    //     //         .eq("goods_id", goodsId));
    //     // if (seckillOrder != null) {
    //     //     model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
    //     //     return "secKillFail";
    //     // }
    //         //解决重复抢购, 从 redis 中取出订单, 提高效率，如果有则已经抢购过了
    //     SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get
    //             ("order:" + user.getId() + ":" + goodsVo.getId());
    //     if(o != null){
    //         //则已经抢购了
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.REPEAT_ERROR.getMessage());
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
    //     //===================秒杀 v2.0 end... =========================
    //
    // }

    // @RequestMapping(value = "/doSeckill")
    // public String doSeckill(Model model, User user, Long goodsId) {
    //     System.out.println("-----秒杀 V3.0--------");
    //     //===================秒杀 v3.0 start =========================
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
    //     //解决重复抢购, 从 redis 中取出订单, 提高效率，如果有则已经抢购过了
    //     SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get
    //             ("order:" + user.getId() + ":" + goodsVo.getId());
    //     if(o != null){
    //         //则已经抢购了
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return "secKillFail";
    //     }
    //
    //
    //     //库存预减,如果在Redis中预减库存,发现秒杀商品已经没有了,就直接返回
    //     //从而减少去执行 orderService.seckill请求,防止线程堆积,优化秒杀/高并发
    //     //这个方法具有原子性[！！一个一个执行]
    //     Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
    //     if(decrement < 0){//说明这个商品已经没有库存
    //         //恢复库存为0
    //         redisTemplate.opsForValue().increment("seckillGoods:" +goodsId);
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return  "secKillFail";//错误页面
    //     }
    //
    //     //抢购
    //     Order order = orderService.seckill(user, goodsVo);
    //     if (order == null) {
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return "secKillFail";
    //     }
    //     model.addAttribute("order", order);
    //     model.addAttribute("goods", goodsVo);
    //     return "orderDetail";
    //     //===================秒杀 v3.0 end... =========================
    //
    // }

    // @RequestMapping(value = "/doSeckill")
    // public String doSeckill(Model model, User user, Long goodsId) {
    //     System.out.println("-----秒杀 v4.0--------");
    //     //===================秒杀 v4.0 start =========================
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
    //     //解决重复抢购, 从 redis 中取出订单, 提高效率，如果有则已经抢购过了
    //     SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get
    //             ("order:" + user.getId() + ":" + goodsVo.getId());
    //     if(o != null){
    //         //则已经抢购了
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return "secKillFail";
    //     }
    //
    //
    //     //如果库存为空，避免总是到 reids 去查询库存，给 redis 增加负担（内存标记）
    //     if (entryStockMap.get(goodsId)) {//如果当前这个秒杀商品已经是空库存，则直接返回.
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return "secKillFail";
    //     }
    //
    //     //库存预减,如果在Redis中预减库存,发现秒杀商品已经没有了,就直接返回
    //     //从而减少去执行 orderService.seckill请求,防止线程堆积,优化秒杀/高并发
    //     //这个方法具有原子性[！！一个一个执行]
    //     Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
    //     if(decrement < 0){//说明这个商品已经没有库存
    //         //这里使用内存标记，避免多次操作 redis, true 表示空库存了.
    //         entryStockMap.put(goodsId, true);
    //         //恢复库存为0
    //         redisTemplate.opsForValue().increment("seckillGoods:" +goodsId);
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return  "secKillFail";//错误页面
    //     }
    //
    //     //抢购
    //     Order order = orderService.seckill(user, goodsVo);
    //     if (order == null) {
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return "secKillFail";
    //     }
    //     model.addAttribute("order", order);
    //     model.addAttribute("goods", goodsVo);
    //     return "orderDetail";
    //     //===================秒杀 v4.0 end... =========================
    //
    // }

    // @RequestMapping(value = "/doSeckill")
    // public String doSeckill(Model model, User user, Long goodsId) {
    //     // System.out.println("-----秒杀 v5采用rabbitmq队列异步访问.0--------");
    //     //===================秒杀 v5.0 start =========================
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
    //     //解决重复抢购, 从 redis 中取出订单, 提高效率，如果有则已经抢购过了
    //     SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get
    //             ("order:" + user.getId() + ":" + goodsVo.getId());
    //     if(o != null){
    //         //则已经抢购了
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return "secKillFail";
    //     }
    //
    //
    //     //如果库存为空，避免总是到 reids 去查询库存，给 redis 增加负担（内存标记）
    //     if (entryStockMap.get(goodsId)) {//如果当前这个秒杀商品已经是空库存，则直接返回.
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return "secKillFail";
    //     }
    //
    //     //库存预减,如果在Redis中预减库存,发现秒杀商品已经没有了,就直接返回
    //     //从而减少去执行 orderService.seckill请求,防止线程堆积,优化秒杀/高并发
    //     //这个方法具有原子性[！！一个一个执行]
    //     Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
    //     if(decrement < 0){//说明这个商品已经没有库存
    //         //这里使用内存标记，避免多次操作 redis, true 表示空库存了.
    //         entryStockMap.put(goodsId, true);
    //         //恢复库存为0
    //         redisTemplate.opsForValue().increment("seckillGoods:" +goodsId);
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return  "secKillFail";//错误页面
    //     }
    //
    //     //rabbitmq队列异步访问
    //     SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
    //     mqSenderMessage.senderMessage(JSONUtil.toJsonStr(seckillMessage));//异步
    //     model.addAttribute("errmsg","排队中...");
    //     return "secKillFail";
    //     //===================秒杀 v5.0 end... =========================
    //
    // }

    @RequestMapping(value = "/{path}/doSeckill")
    @ResponseBody
    public RespBean  doSeckill(@PathVariable String path, Model model
            , User user, Long goodsId) {
        //===================秒杀 v6.0 start =========================
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        System.out.println("从客户端发来的 path=" + path);
        //检查秒杀生成的路径是否和服务器是一直的
        boolean b = orderService.checkPath(user, goodsId, path);
        if(!b){
            //如果生成的路径不对,就返回错误页面
            //请求非法 用的软件抢购商品
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
            }
        //解决重复抢购, 从 redis 中取出订单, 提高效率，如果有则已经抢购过了
        SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get
                ("order:" + user.getId() + ":" + goodsVo.getId());
        if(o != null){
            //则已经抢购了
            model.addAttribute("errmsg",
                    RespBeanEnum.REPEAT_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }


        //如果库存为空，避免总是到 reids 去查询库存，给 redis 增加负担（内存标记）
        if (entryStockMap.get(goodsId)) {//如果当前这个秒杀商品已经是空库存，则直接返回.
            model.addAttribute("errmsg",
                    RespBeanEnum.ENTRY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        //库存预减,如果在Redis中预减库存,发现秒杀商品已经没有了,就直接返回
        //从而减少去执行 orderService.seckill请求,防止线程堆积,优化秒杀/高并发
        //这个方法具有原子性[！！一个一个执行]
        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        if(decrement < 0){//说明这个商品已经没有库存
            //这里使用内存标记，避免多次操作 redis, true 表示空库存了.
            entryStockMap.put(goodsId, true);
            //恢复库存为0
            redisTemplate.opsForValue().increment("seckillGoods:" +goodsId);
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        //rabbitmq队列异步访问
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSenderMessage.senderMessage(JSONUtil.toJsonStr(seckillMessage));//异步
        model.addAttribute("errmsg","排队中...");
        return RespBean.error(RespBeanEnum.SEC_KILL_WAIT);
        //===================秒杀 v6.0 end... =========================

    }

    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user,Long goodsId){
        if(user == null || goodsId < 0){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);//用户信息有误

        }
        //创建真正的地址
        String url = orderService.createPath(user, goodsId);
        return RespBean.success(url);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //查询所有的秒杀商品
        List<GoodsVo> list = goodsService.findGoodsVo();
        //判断List是否为空
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        //遍历list,然后将秒杀商品的库存量,放入到Redis
        //秒杀商品库存量对应key : seckillGoods:商品id
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),
                    goodsVo.getStockCount());
            entryStockMap.put(goodsVo.getId(), false);
        });
    }
}
