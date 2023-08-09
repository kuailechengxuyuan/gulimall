package com.zx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-25 09:09:57
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

