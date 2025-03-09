package com.hyb.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyb.seckill.pojo.Goods;
import com.hyb.seckill.vo.GoodsVo;

import java.util.List;

/**
 * @Author hyb
 * @Date 2025/3/9 15:37
 * @Version 1.0
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    //获取商品列表
    List<GoodsVo> findGoodsVo();

    //获取商品详细
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
