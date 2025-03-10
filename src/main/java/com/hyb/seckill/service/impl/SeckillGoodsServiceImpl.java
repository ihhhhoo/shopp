package com.hyb.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyb.seckill.mapper.SeckillGoodsMapper;
import com.hyb.seckill.pojo.SeckillGoods;
import com.hyb.seckill.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

/**
 * @Author hyb
 * @Date 2025/3/9 15:42
 * @Version 1.0
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
        implements SeckillGoodsService {
}
