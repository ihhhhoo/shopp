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

}
