package com.zx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购单
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-25 09:37:41
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

