package com.zx.gulimall.product.feign;

import com.zx.common.utils.R;
import com.zx.gulimall.product.to.SkuReductionTo;
import com.zx.gulimall.product.to.SpuBoundTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 新增积分信息（当前spu商品购买新增的积分规则信息）
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo boundTo);

    /**
     * 新增满减信息
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveReduceMessage(@RequestBody SkuReductionTo skuReductionTo);

}
