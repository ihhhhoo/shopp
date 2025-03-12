package com.hyb.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author hyb
 * @Date 2025/3/12 10:58
 * @Version 1.0
 */
@Configuration
public class RabbitMQHeadersConfig {
    private static final String QUEUE01 = "queue_header01";
    private static final String QUEUE02 = "queue_header02";
    private static final String EXCHANGE = "headersExchange";

    @Bean
    public Queue queue_header01() {
        return new Queue(QUEUE01);
    }

    @Bean
    public Queue queue_header02() {
        return new Queue(QUEUE02);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(EXCHANGE);
    }

    @Bean
    public Binding binding_header01() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "low");
        System.out.println("yy=" + headersExchange().hashCode());
        return BindingBuilder.bind(queue_header01())
                .to(headersExchange()).whereAny(map).match();
    }
    /**
     * queue_header02(): 指定要绑定的队列
     * headersExchange(): 指定交换机
     * whereAll(map): 发送的消息的属性 MessageProperties 要全部匹配才 OK
     * @return
     */
    @Bean
    public Binding binding_header02() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "fast");
        System.out.println("xx=" + headersExchange().hashCode());
        return BindingBuilder.bind(queue_header02())
                .to(headersExchange()).whereAll(map).match();
    }
}
