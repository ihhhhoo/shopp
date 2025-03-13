package com.hyb.seckill.service.impl;

import ch.qos.logback.core.util.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyb.seckill.mapper.OrderMapper;
import com.hyb.seckill.pojo.Order;
import com.hyb.seckill.pojo.SeckillGoods;
import com.hyb.seckill.pojo.SeckillOrder;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.OrderService;
import com.hyb.seckill.service.SeckillGoodsService;
import com.hyb.seckill.service.SeckillOrderService;
import com.hyb.seckill.util.MD5Util;
import com.hyb.seckill.util.UUIDUtil;
import com.hyb.seckill.vo.GoodsVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author hyb
 * @Date 2025/3/9 17:20
 * @Version 1.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements
        OrderService {

    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private RedisTemplate redisTemplate;
    //秒杀商品，减少库存


    //秒杀商品减少库存
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        //查询后端的库存量进行减一
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
        .eq("goods_id",goodsVo.getId()));
        //下面这句和判断库存不是原子性操作，会出现问题后面优化
        // seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        // seckillGoodsService.updateById(seckillGoods);

        //seckillGoodsService.updateById(seckillGoods);
        //在默认的事务隔离级别 REPEATABLE READ 中，
        //UPDATE 语句会在事务中锁定要更新的行，
        //这可以防止其他会话在同一行上执行 UPDATE 或 DELETE 操作
        boolean update = seckillGoodsService.update
                (new UpdateWrapper<SeckillGoods>().
                        setSql("stock_count = stock_count-1").
                        eq("goods_id", goodsVo.getId()).
                        gt("stock_count", 0));
        if (!update) {//如果更新失败,说明已经没有库存了
            return null;
        }


        //生成普通订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrderService.save(seckillOrder);
        //将生成的秒杀订单,存入 redis, 这样在查询某个用户是否已经秒杀过该商品 时,
        //就不用到数据库去查询
        //而是直接从 redis 去查询，起到优化的作用
        redisTemplate.opsForValue().set("order:" + user.getId() +
                ":" + goodsVo.getId(), seckillOrder);
        return order;



    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid());
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() +
                ":" + goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(path)) {
            return false;
        }
        //path 判断
        String str = (String) redisTemplate.opsForValue().get("seckillPath:" +
                user.getId() + ":" + goodsId);
        return path.equals(str);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user == null || goodsId < 0 || !StringUtils.hasText(captcha)){
            return false;
        }
        String redisCaptcha = (String)redisTemplate.opsForValue().
                get("captcha:" + user.getId() + ":" + goodsId);

        return captcha.equals(redisCaptcha);
    }
}
