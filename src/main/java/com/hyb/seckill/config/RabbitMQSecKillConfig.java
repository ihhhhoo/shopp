package com.hyb.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author hyb
 * @Date 2025/3/12 16:58
 * @Version 1.0
 */
@Configuration
public class RabbitMQSecKillConfig {
    private static final  String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";

    @Bean
    public Queue queue_seckill(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange topicExchange_seckill() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding_seckill(){
        return BindingBuilder.bind(queue_seckill()).
                to(topicExchange_seckill()).with("seckill.#");
    }
}
