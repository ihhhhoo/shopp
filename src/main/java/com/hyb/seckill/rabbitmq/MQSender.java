package com.hyb.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 消息发送者/生产者
 */
@Service
@Slf4j
public class MQSender {
    @Resource
    private RabbitTemplate rabbitTemplate;
    //装配RabbitTemplate --> 操作RabbitMQ
    public void send(Object msg) {
        log.info("发送消息-" + msg);
        //向 queue 队列发送消息
        rabbitTemplate.convertAndSend("queue", msg);
    }

    public void sendFanout(Object msg) {
        log.info("发送消息" + msg);
        //消息发送到交换机
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }

    public void sendDirect1(Object msg){
        log.info("发送消息" + msg);
        //将消息发送到directExchange交换机,同时指定路由
        rabbitTemplate.convertAndSend
                ("directExchange","queue.red",msg);
    }

    public void sendDirect2(Object msg){
        log.info("发送消息" + msg);
        //将消息发送到directExchange交换机,同时指定路由
        rabbitTemplate.convertAndSend
                ("directExchange","queue.green",msg);
    }


    public void sendTopic1(Object msg) {
        log.info("发送消息(QUEUE01 接收)：" + msg);
        //发送消息到 topicExchange 队列，同时携带 routingKey queue.red.message
        rabbitTemplate.convertAndSend("topicExchange",
                "queue.red.message", msg);
    }
    public void sendTopic2(Object msg) {
        log.info("发送消息(QUEUE02 接收)：" + msg);
        //发送消息到 topicExchange 队列，同时携带 routingKey green.queue.green.message
        rabbitTemplate.convertAndSend("topicExchange",
                "green.queue.green.message", msg);
    }

    public void sendHeader01(String msg) {
        log.info("发送消息(QUEUE01 和 QUEUE02 接收)：" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "fast");
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headersExchange",
                "", message);
    }
    public void sendHeader02(String msg) {
        log.info("发送消息(QUEUE01 接收)：" + msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("color", "red");
        properties.setHeader("speed", "normal");
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headersExchange",
                "", message);
    }
}
