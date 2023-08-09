package com.zx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zx.common.utils.PageUtils;
import com.zx.gulimall.product.entity.AttrEntity;
import com.zx.gulimall.product.entity.AttrGroupEntity;
import com.zx.gulimall.product.vo.GroupWithAttr;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-21 12:28:02
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrEntity> getRelationAttr(Long attrgroupId);


    List<GroupWithAttr> getGroupWithAttr(Long catelogId);

}

