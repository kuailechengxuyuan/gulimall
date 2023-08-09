package com.zx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.product.entity.SkuInfoEntity;
import com.zx.gulimall.product.vo.Skus;
import com.zx.gulimall.product.vo.SpuSaveVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-21 12:28:02
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveSkuInfo(Long spuId, List<Skus> skus, SpuSaveVo spuInfo);

    PageUtils queryPageByCondition(Map<String, Object> params);

}

