package com.hyb.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyb.seckill.pojo.Order;
import com.hyb.seckill.pojo.User;
import com.hyb.seckill.vo.GoodsVo;

/**
 * @Author hyb
 * @Date 2025/3/9 17:13
 * @Version 1.0
 */
public interface OrderService extends IService<Order> {

    //秒杀
    Order seckill(User user, GoodsVo goodsVo);

    //生成秒杀地址
    String createPath(User user, Long goodsId);
    //判断redis是否有秒杀地址
    boolean checkPath(User user, Long goodsId, String path);

    //验证用户输入的验证码是否正确
    boolean checkCaptcha(User user, Long goodsId, String captcha);


}
