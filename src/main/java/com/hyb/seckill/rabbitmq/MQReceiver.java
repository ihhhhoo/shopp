package com.hyb.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 消息接受者/消费者
 */
@Service
@Slf4j
public class MQReceiver {
    /**
     * 监听队列 queue
     * @param msg
     */
    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接收到消息--" + msg);
    }

    /**
     * 监听队列 queue_fanout01
     */
    @RabbitListener(queues = "queue_fanout01")
    public void receive1(Object msg) {
        log.info("从 queue_fanout01 接收消息-" + msg);
    }

    /**
     * 监听队列 queue_fanout02
     */
    @RabbitListener(queues = "queue_fanout02")
    public void receive2(Object msg) {
        log.info("从 queue_fanout02 接收消息-" + msg);
    }


    @RabbitListener(queues = "queue_direct01")
    public void queue_direct1(Object msg) {
        log.info("从 queue_direct01 接收消息-" + msg);
    }
    @RabbitListener(queues = "queue_direct02")
    public void queue_direct2(Object msg) {
        log.info("从 queue_direct02 接收消息-" + msg);
    }


    @RabbitListener(queues = "queue_topic01")
    public void receive_topic01(Object msg) {
        log.info("从 QUEUE01 接收消息-" + msg);
    }
    @RabbitListener(queues = "queue_topic02")
    public void receive_topic02(Object msg) {
        log.info("从 QUEUE02 接收消息-" + msg);
    }

    @RabbitListener(queues = "queue_header01")
    public void receive07(Message message) {
        log.info("QUEUE01 接收消息 message 对象" + message);
        log.info("QUEUE01 接收消息" + new String(message.getBody()));
    }
    @RabbitListener(queues = "queue_header02")
    public void receive08(Message message) {
        log.info("QUEUE02 接收消息 message 对象" + message);
        log.info("QUEUE02 接收消息" + new String(message.getBody()));
    }
}
