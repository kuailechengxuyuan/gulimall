package com.zx.gulimall.product.to;

import com.zx.gulimall.product.vo.MemberPrice;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;// 满件数
    private BigDecimal discount;// 折扣
    private int countStatus;
    private BigDecimal fullPrice;// 满金额
    private BigDecimal reducePrice;// 优惠金额
    private int priceStatus;
    private List<MemberPrice> memberPrice;// 会员价格
}
