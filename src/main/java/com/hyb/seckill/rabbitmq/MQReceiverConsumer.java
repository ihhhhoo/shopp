package com.hyb.seckill.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.hyb.seckill.pojo.SeckillMessage;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.service.GoodsService;
import com.hyb.seckill.service.OrderService;
import com.hyb.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author hyb
 * @Date 2025/3/12 17:08
 * @Version 1.0
 */
@Service
@Slf4j
public class MQReceiverConsumer {
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    //进行下单操作
    @RabbitListener(queues = "seckillQueue")//监听队列为seckillQueue的
    public void queue(String message){
        log.info("接收到的消息：" + message);
        SeckillMessage seckillMessage = JSONUtil.toBean(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //获取采购的商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //下单
        orderService.seckill(user,goodsVo);
    }
}
