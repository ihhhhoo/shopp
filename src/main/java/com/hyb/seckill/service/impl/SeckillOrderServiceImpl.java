package com.hyb.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyb.seckill.mapper.SeckillOrderMapper;
import com.hyb.seckill.pojo.SeckillOrder;
import com.hyb.seckill.service.SeckillOrderService;
import org.springframework.stereotype.Service;

/**
 * @Author hyb
 * @Date 2025/3/9 22:36
 * @Version 1.0
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
        implements SeckillOrderService {
}
