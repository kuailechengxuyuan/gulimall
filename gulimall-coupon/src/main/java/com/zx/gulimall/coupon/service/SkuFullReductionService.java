package com.zx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.coupon.entity.SkuFullReductionEntity;
import com.zx.gulimall.coupon.to.SkuReductionTo;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-25 09:09:58
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveReduceInfo(SkuReductionTo skuReductionTo);
}

