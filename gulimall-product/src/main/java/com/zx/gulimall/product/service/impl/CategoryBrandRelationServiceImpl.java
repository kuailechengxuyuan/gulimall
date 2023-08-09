package com.zx.gulimall.product.service.impl;

import com.zx.gulimall.product.dao.BrandDao;
import com.zx.gulimall.product.dao.CategoryDao;
import com.zx.gulimall.product.entity.BrandEntity;
import com.zx.gulimall.product.entity.CategoryEntity;
import com.zx.gulimall.product.vo.BrandsRelationListVo;
import com.zx.gulimall.product.vo.RelationDeleteVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.CategoryBrandRelationDao;
import com.zx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.zx.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {

        Long brandId = categoryBrandRelation.getBrandId();
        BrandEntity brandEntity = brandDao.selectById(brandId);
        categoryBrandRelation.setBrandName(brandEntity.getName());

        Long catelogId = categoryBrandRelation.getCatelogId();
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        this.save(categoryBrandRelation);

    }

    @Override
    public List<BrandsRelationListVo> getBrandsList(Long catId) {
        List<CategoryBrandRelationEntity> entityList = this.baseMapper.selectList(
                new QueryWrapper<CategoryBrandRelationEntity>()
                        .eq("catelog_id", catId));
        List<BrandsRelationListVo> brandsRelationListVos = entityList.stream().map(entity -> {
            BrandsRelationListVo brandsRelationListVo = new BrandsRelationListVo();
            BeanUtils.copyProperties(entity, brandsRelationListVo);
            return brandsRelationListVo;
        }).collect(Collectors.toList());
        return brandsRelationListVos;
    }

}