package com.zx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.zx.gulimall.product.vo.BrandsRelationListVo;
import com.zx.gulimall.product.vo.RelationDeleteVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-21 12:28:02
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    List<BrandsRelationListVo> getBrandsList(Long catId);
}

