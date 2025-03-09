package com.hyb.seckill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author hyb
 * @Date 2025/3/9 15:35
 * @Version 1.0
 */
@Data
@TableName("t_goods")
public class Goods implements Serializable {
    /**
     * 商品 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String goodsName;
    /**
     * 商品标题
     */
    private String goodsTitle;
    /**
     * 商品图片
     */
    private String goodsImg;
    /**
     * 商品详情
     */
    private String goodsDetail;
    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;
    /**
     * 商品库存
     */
    private Integer goodsStock;

}
