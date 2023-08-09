package com.zx.gulimall.product.agent;

import com.zx.common.utils.R;
import com.zx.gulimall.product.feign.CouponFeignService;
import com.zx.gulimall.product.to.SkuReductionTo;
import com.zx.gulimall.product.to.SpuBoundTo;
import com.zx.gulimall.product.vo.Bounds;
import com.zx.gulimall.product.vo.Skus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponAgentService {

    @Autowired
    private CouponFeignService couponFeignService;

    public R saveSpuBounds(Long spuId, Bounds bounds) {
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuId);
        return couponFeignService.saveSpuBounds(spuBoundTo);


    }

    public R saveSkuReduction(Long skuId, Skus sku) {
        SkuReductionTo skuReductionTo = new SkuReductionTo();
        BeanUtils.copyProperties(sku,skuReductionTo);
        skuReductionTo.setSkuId(skuId);
        return couponFeignService.saveReduceMessage(skuReductionTo);
    }
}
