package com.hyb.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author hyb
 * @Date 2025/3/12 8:47
 * @Version 1.0
 */
@Configuration
public class RabbitMQConfig {
    private static final String QUEUE = "queue";
    private static final String QUEUE1 = "queue_fanout01";
    private static final String QUEUE2 = "queue_fanout02";
    private static final String exchange = "fanoutExchange";


    private static final String QUEUE_Direct1 = "queue_direct01";
    private static final String QUEUE_Direct2 = "queue_direct02";
    private static final String EXCHANGE_DIRECT = "directExchange";
    private static final String ROUNTINGKEY01 = "queue.red";
    private static final String ROUNTINGKEY02 = "queue.green";
    /**
     * 1. 配置队列
     * 2. 队列名为 queue
     * 3. true 表示: 持久化
     * durable： 队列是否持久化。 队列默认是存放到内存中的，rabbitmq 重启则丢失，
     * 若想重启之后还存在则队列要持久化，
     * 保存到 Erlang 自带的 Mnesia 数据库中，当 rabbitmq 重启之后会读取该数据库
     */

    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }
    @Bean
    public Queue queue1() {
        return new Queue(QUEUE1);
    }
    @Bean
    public Queue queue2() {
        return new Queue(QUEUE2);
    }
    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(exchange);
    }

    /**将 queue_fanout01 队列绑定到交换机 fanoutExchange*/
    @Bean
    public Binding binding01() {
        return BindingBuilder.bind(queue1()).to(exchange());
    }
    /**将 queue_fanout02 队列绑定到交换机 fanoutExchange*/
    @Bean
    public Binding binding02() {
        return BindingBuilder.bind(queue2()).to(exchange());
    }

    @Bean
    public Queue queue_direct1() {
        return new Queue(QUEUE_Direct1);
    }

    @Bean
    public Queue queue_direct2() {
        return new Queue(QUEUE_Direct2);
    }


    @Bean
    public DirectExchange exchange_direct() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    @Bean
    public Binding binding_direct1() {
        return BindingBuilder
                .bind(queue_direct1()).to(exchange_direct())
                .with(ROUNTINGKEY01);
    }
    /**
     * 将队列绑定到 指定交换机，并指定 路由
     * queue_direct2(): 队列
     * exchange_direct(): 交换机
     * rountingkey02: 路由
     * @return
     */
    @Bean
    public Binding binding_direct2() {
        return BindingBuilder
                .bind(queue_direct2()).to(exchange_direct())
                .with(ROUNTINGKEY02);
    }


}
