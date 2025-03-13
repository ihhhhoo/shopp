package com.hyb.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hyb.seckill.config.AccessLimit;
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
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    // @RequestMapping(value = "/{path}/doSeckill")
    // @ResponseBody
    // public RespBean  doSeckill(@PathVariable String path, Model model
    //         , User user, Long goodsId) {
    //     //===================秒杀 v6.0 start =========================
    //     if (user == null) {
    //         return RespBean.error(RespBeanEnum.SESSION_ERROR);
    //     }
    //     System.out.println("从客户端发来的 path=" + path);
    //     //检查秒杀生成的路径是否和服务器是一直的
    //     boolean b = orderService.checkPath(user, goodsId, path);
    //     if(!b){
    //         //如果生成的路径不对,就返回错误页面
    //         //请求非法 用的软件抢购商品
    //         return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
    //     }
    //     // model.addAttribute("user", user);
    //     GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
    //     //判断库存
    //     if (goodsVo.getStockCount() < 1) {
    //         model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return RespBean.error(RespBeanEnum.ENTRY_STOCK);
    //         }
    //     //解决重复抢购, 从 redis 中取出订单, 提高效率，如果有则已经抢购过了
    //     SeckillOrder o = (SeckillOrder) redisTemplate.opsForValue().get
    //             ("order:" + user.getId() + ":" + goodsVo.getId());
    //     if(o != null){
    //         //则已经抢购了
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.REPEAT_ERROR.getMessage());
    //         return RespBean.error(RespBeanEnum.REPEAT_ERROR);
    //     }
    //
    //
    //     //如果库存为空，避免总是到 reids 去查询库存，给 redis 增加负担（内存标记）
    //     if (entryStockMap.get(goodsId)) {//如果当前这个秒杀商品已经是空库存，则直接返回.
    //         model.addAttribute("errmsg",
    //                 RespBeanEnum.ENTRY_STOCK.getMessage());
    //         return RespBean.error(RespBeanEnum.ENTRY_STOCK);
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
    //         return RespBean.error(RespBeanEnum.ENTRY_STOCK);
    //     }
    //
    //     //rabbitmq队列异步访问
    //     SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
    //     mqSenderMessage.senderMessage(JSONUtil.toJsonStr(seckillMessage));//异步
    //     model.addAttribute("errmsg","排队中...");
    //     return RespBean.error(RespBeanEnum.SEC_KILL_WAIT);
    //     //===================秒杀 v6.0 end... =========================
    //
    // }


    //装配 RedisScript
    @Resource
    private RedisScript<Long> script;
    @RequestMapping(value = "/{path}/doSeckill")
    @ResponseBody//7.0============使用 Redis 分布式锁=========
    public RespBean  doSeckill(@PathVariable String path, Model model
            , User user, Long goodsId) {
        //===================秒杀 v7.0 start =========================
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        System.out.println("从客户端发来的 path=" + path);
        //检查秒杀生成的路径是否和服务器是一直的
        boolean b = orderService.checkPath(user, goodsId, path);
        if (!b) {
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
        if (o != null) {
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

        //============使用 Redis 分布式锁=========
        //说明
        //1. 对于当前项目,使用 redisTemplate.opsForValue().decrement()就可以控制
        //2. 考虑到如果有比较多的操作，需要保证隔离性, 和对 Redis 操作也不是简单的-1，而是有 多个操作, 我们给小伙伴讲解一下如何使用 redis 分布式锁来控制
        //1 获取锁，setnx
        //得到一个 uuid 值，作为锁的值
        String uuid = UUID.randomUUID().toString();
        Boolean lock =
                redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        //2 获取锁成功、就执行相应业务
        if (lock) {
            //释放锁
            //定义 lua 脚本
            //执行你自己的业务
            Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);

            if (decrement < 0) {//说明这个商品已经没有库存
                //这里使用内存标记，避免多次操作 redis, true 表示空库存了.
                entryStockMap.put(goodsId, true);
                //恢复库存为0
                redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
                //redisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
                redisTemplate.execute(script, Arrays.asList("lock"), uuid);
                model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
                return RespBean.error(RespBeanEnum.ENTRY_STOCK);
            }
            //释放分布式锁
            redisTemplate.execute(script, Arrays.asList("lock"), uuid);
        }else {
                //3.获取锁失败.返回信息,这次抢购失败,请继续抢购
                model.addAttribute("errmsg", RespBeanEnum.SEC_KILL_RETRY);
                return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }
        //rabbitmq队列异步访问
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSenderMessage.senderMessage(JSONUtil.toJsonStr(seckillMessage));//异步
        model.addAttribute("errmsg", "排队中...");
        return RespBean.error(RespBeanEnum.SEC_KILL_WAIT);
        //===================秒杀 v7.0 end... =========================
    }
    @GetMapping("/captcha")
    public void captcha(User user,
                        Long goodsId,
                        HttpServletRequest req,HttpServletResponse res){
        //生成验证码并输出
        //warning! 该验证码默认就保存到session中, key默认是happy-captcha
        HappyCaptcha.require(req,res)
                .style(CaptchaStyle.ANIM)			//设置展现样式为动画
                .type(CaptchaType.CHINESE)			//设置验证码内容为汉字
                .length(6)							//设置字符长度为6
                .width(220)							//设置动画宽度为220
                .height(80)							//设置动画高度为80
                .font(Fonts.getInstance().zhFont())	//设置汉字的字体
                .build().finish();      			//生成并输出验证码
        //把验证码的值存放到 Redis 中,userId+goodsId, 有效时间为 100s
        redisTemplate.opsForValue().set("captcha:" + user.getId() +
                ":" + goodsId,(String)req.getSession().
                getAttribute("happy-captcha"),100, TimeUnit.SECONDS);

    }


    @AccessLimit(second = 5,maxCount = 5,needLogin = true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user,Long goodsId , String captcha ,
                            HttpServletRequest request){
        if(user == null || goodsId < 0 || !StringUtils.hasText(captcha)){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);//用户信息有误

        }
        // //7.0计数器redis，5秒内访问超过5次,就认为是刷接口
        // //也可以用注解提高使用通用性
        // String uri = request.getRequestURI();
        // ValueOperations valueOperations = redisTemplate.opsForValue();
        // Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        // if(count == null){
        //     valueOperations.set(uri + ":" + user.getId(),1,
        //             5, TimeUnit.SECONDS);
        // }else if(count < 5){
        //     valueOperations.increment(uri + ":" + user.getId());
        // }else {
        //     return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHE);
        // }


        //增加一个业务逻辑-校验用户输入的验证码是否正确
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if(!check){
            //如果校验失败
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
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
