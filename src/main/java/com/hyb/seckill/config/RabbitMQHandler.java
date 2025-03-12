package com.hyb.seckill.config;

import com.hyb.seckill.rabbitmq.MQSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author hyb
 * @Date 2025/3/12 8:52
 * @Version 1.0
 */
@Controller
public class RabbitMQHandler {

    @Resource
    private MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.send("hello");
    }

    //fanout 模式
    @GetMapping("/mq/fanout")
    @ResponseBody
    public void fanout() {
        mqSender.sendFanout("hello~");
    }


    //direct 模式
    @GetMapping("/mq/direct01")
    @ResponseBody
    public void direct01() {
        mqSender.sendDirect1("hello:red");
    }
    @GetMapping("/mq/direct02")
    @ResponseBody
    public void direct02() {
        mqSender.sendDirect2("hello:green");
    }


    @RequestMapping(value = "/mq/topic01", method = RequestMethod.GET)
    @ResponseBody
    public void mqtopic01() {
        mqSender.sendTopic1("Hello Red");
    }
    @RequestMapping(value = "/mq/topic02", method = RequestMethod.GET)
    @ResponseBody
    public void mqtopic02() {
        mqSender.sendTopic2("Hello Green");
    }


    @RequestMapping(value = "/mq/header01", method = RequestMethod.GET)
    @ResponseBody
    public void header01() {
        mqSender.sendHeader01("Hello ABC");
    }
    @RequestMapping(value = "/mq/header02", method = RequestMethod.GET)
    @ResponseBody
    public void header02() {
        mqSender.sendHeader02("Hello Hsp");
    }
}
