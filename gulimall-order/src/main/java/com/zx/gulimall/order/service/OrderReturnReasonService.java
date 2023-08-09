package com.zx.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-25 09:30:42
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

