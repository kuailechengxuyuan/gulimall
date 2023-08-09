package com.zx.gulimall.product.service.impl;

import com.mysql.cj.util.StringUtils;
import com.zx.gulimall.product.dao.CategoryBrandRelationDao;
import com.zx.gulimall.product.entity.CategoryBrandRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.BrandDao;
import com.zx.gulimall.product.entity.BrandEntity;
import com.zx.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> wrapper=new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
            wrapper = new QueryWrapper<BrandEntity>().eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        BrandEntity oldValue = this.getById(brand.getBrandId());
        this.updateById(brand);
        if(!oldValue.getName().equals(brand.getName())){
            //pms_category_brand_relation
            CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
            entity.setBrandName(brand.getName());
            categoryBrandRelationDao.update(entity,new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brand.getBrandId()));
        }
    }

}