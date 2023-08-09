package com.zx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.product.entity.AttrEntity;
import com.zx.gulimall.product.entity.AttrVo;
import com.zx.gulimall.product.vo.AttrRespVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-21 12:28:02
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrAndRelation(AttrVo attr);

    PageUtils queryPageWithKey(Map<String, Object> params, Long catelogId);

    AttrRespVo getDetail(Long attrId);

    void updateDeatil(AttrVo attr);

    PageUtils queryPageOfSale(Map<String, Object> params, Long catelogId);

    PageUtils getNoAttrRelationList(Long attrgroupId, Map<String, Object> params);
}

