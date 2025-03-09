package com.hyb.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyb.seckill.pojo.Goods;
import com.hyb.seckill.vo.GoodsVo;

import java.util.List;

/**
 * @Author hyb
 * @Date 2025/3/9 15:40
 * @Version 1.0
 */
public interface GoodsService extends IService<Goods> {
    //商品列表
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
