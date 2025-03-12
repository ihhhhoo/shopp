package com.hyb.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author hyb
 * @Date 2025/3/12 17:06
 * @Version 1.0
 */
@Service
@Slf4j
public class MQSenderMessage {
    @Resource
    private RabbitTemplate rabbitTemplate;
    //发送秒杀信息
    public void senderMessage(String message){
        System.out.println("发送消息了=" +message);
        log.info("发送消息: " + message);
        rabbitTemplate.convertAndSend("seckillExchange"
                ,"seckill.message",message);
    }
}
