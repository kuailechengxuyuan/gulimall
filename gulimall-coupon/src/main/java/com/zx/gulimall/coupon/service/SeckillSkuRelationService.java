package com.zx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-25 09:09:58
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

