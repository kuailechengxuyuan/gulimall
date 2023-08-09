package com.zx.gulimall.product.service.impl;

import com.zx.gulimall.product.dao.CategoryBrandRelationDao;
import com.zx.gulimall.product.entity.CategoryBrandRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.CategoryDao;
import com.zx.gulimall.product.entity.CategoryEntity;
import com.zx.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> allCategoryEntries = baseMapper.selectList(null);

        // 1、找到一级条目
        List<CategoryEntity> oneLevelCategoryEntries = allCategoryEntries.stream().filter(entity -> entity.getCatLevel() == 1).collect(Collectors.toList());
        // 2、找到一级条目的子条目
        List<CategoryEntity> categoryEntriesWithSubentities = oneLevelCategoryEntries.stream().map(entity -> {
            entity.setChildren(getChildren(entity, allCategoryEntries));
            return entity;
        }).collect(Collectors.toList());

        return categoryEntriesWithSubentities;
    }

    @Override
    public long[] findCategoryPath(Long catelogId) {
        List<Long> subPath = new ArrayList<>();
        List<Long> path = findPath(catelogId, subPath);
        Collections.reverse(path);
        long[] ans = new long[path.size()];
        for(int i = 0;i<path.size();i++){
            ans[i]= path.get(i);
        }
        return ans;
    }

    @Override
    @Transactional
    public void updateDetail(CategoryEntity category) {
        CategoryEntity oldvalue = this.getById(category.getCatId());
        this.updateById(category);
        if(!oldvalue.getName().equals(category.getName())){
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setBrandName(category.getName());
            categoryBrandRelationDao.update(categoryBrandRelationEntity,new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",category.getCatId()));
        }
    }

    private List<Long> findPath(Long catelogId,List<Long> subPath){
        subPath.add(catelogId);
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        Long parentCid = categoryEntity.getParentCid();
        Integer catLevel = categoryEntity.getCatLevel();
        if(catLevel>1){
            findPath(parentCid,subPath);
        }
        return subPath;
    }

    private List<CategoryEntity> getChildren(CategoryEntity entity,List<CategoryEntity> allCategoryEntries){
        List<CategoryEntity> collect = allCategoryEntries.stream().filter(entity1 -> entity1.getParentCid() == entity.getCatId()).map(entity1 -> {
            entity1.setChildren(getChildren(entity1, allCategoryEntries));
            return entity1;
        }).collect(Collectors.toList());

        return collect;
    }

}