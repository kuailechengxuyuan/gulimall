package com.zx.gulimall.product.service.impl;

import com.zx.gulimall.product.dao.AttrDao;
import com.zx.gulimall.product.entity.AttrEntity;
import com.zx.gulimall.product.service.AttrService;
import com.zx.gulimall.product.vo.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.ProductAttrValueDao;
import com.zx.gulimall.product.entity.ProductAttrValueEntity;
import com.zx.gulimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttrValue(Long spuId, List<BaseAttrs> baseAttrs) {
        List<Long> list = baseAttrs.stream().map(e -> {
            return e.getAttrId();
        }).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(list);
        HashMap<Long,String> table = new HashMap<>();
        for(AttrEntity a:attrEntities){
            Long attrId = a.getAttrId();
            String attrName = a.getAttrName();
            table.put(attrId,attrName);
        }
        List<ProductAttrValueEntity> entityList = baseAttrs.stream().map(e -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuId);
            Long attrId = e.getAttrId();
            productAttrValueEntity.setAttrId(attrId);
            productAttrValueEntity.setAttrName(table.get(attrId));
            productAttrValueEntity.setAttrValue(e.getAttrValues());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        this.saveBatch(entityList);
    }

}