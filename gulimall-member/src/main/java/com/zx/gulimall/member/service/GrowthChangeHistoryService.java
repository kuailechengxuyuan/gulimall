package com.zx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-25 09:20:21
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

