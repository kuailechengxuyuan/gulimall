package com.zx.gulimall.product.service.impl;

import com.mysql.cj.util.StringUtils;
import com.zx.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.zx.gulimall.product.dao.AttrDao;
import com.zx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.zx.gulimall.product.entity.AttrEntity;
import com.zx.gulimall.product.vo.GroupWithAttr;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.AttrGroupDao;
import com.zx.gulimall.product.entity.AttrGroupEntity;
import com.zx.gulimall.product.service.AttrGroupService;
import org.w3c.dom.Attr;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrDao attrDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
            wrapper.and((entity) -> {
                entity.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if(catelogId==0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }else{
            // select * from pms_attr_group where catelog_id = ? and  (arrr_group_id= key or attr_group_name =%key%)
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> list
                = attrAttrgroupRelationDao.selectList(
                        new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_group_id", attrgroupId
                        )
        );
        List<Long> attrIds = list.stream().map(entity -> {
            return entity.getAttrId();
        }).collect(Collectors.toList());
        if(attrIds==null || attrIds.size()==0){
            return null;
        }
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
        return attrEntities;
    }

    @Override
    public List<GroupWithAttr> getGroupWithAttr(Long catelogId) {
        // 1 第一次查询，查出分类对应的所有分组
        List<AttrGroupEntity> groupList
                = this.baseMapper.selectList(
                        new QueryWrapper<AttrGroupEntity>()
                                .eq("catelog_id", catelogId)
        );
        List<Long> groupIdList = groupList.stream().map((entity) -> {
            return entity.getAttrGroupId();
        }).collect(Collectors.toList());
        if(groupList==null || groupList.size()==0){
            return new ArrayList<GroupWithAttr>();
        }
        // 1.1 将查询结果存入Map中
        HashMap<Long, AttrGroupEntity> attrGroupEntityMap = new HashMap<Long, AttrGroupEntity>();
        for (AttrGroupEntity g:groupList
             ){
            attrGroupEntityMap.put(g.getAttrGroupId(),g);
        }

        // 2 第二次查询，查出所有分组对应的所有关系
        List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .in("attr_group_id", groupIdList)
        );
        List<Long> attrIdList = relationEntityList.stream().map((e) -> {
            return e.getAttrId();
        }).collect(Collectors.toList());

        // 3 第三次查询，查出所有属性的详细信息
        List<AttrEntity> attrEntities=new ArrayList<>();
        if(attrIdList!=null && attrIdList.size()!=0){
            attrEntities = attrDao.selectBatchIds(attrIdList);
        }

        // 3.1 将查询结果放入Map
        HashMap<Long, AttrEntity> attrEntityHashMap = new HashMap<>();
        for (AttrEntity a:attrEntities
             ) {
            attrEntityHashMap.put(a.getAttrId(),a);
        }
        // 4 整理查询结果，将查询结果以GroupWithAttr的形式返回
        // 4.1
        HashMap<Long, ArrayList<Long>> tableOfRelation = new HashMap<>();
        for(AttrAttrgroupRelationEntity aar:relationEntityList){
            Long attrId = aar.getAttrId();
            Long attrGroupId = aar.getAttrGroupId();
            if(tableOfRelation.containsKey(attrGroupId)){
                tableOfRelation.get(attrGroupId).add(attrId);
            }else{
                ArrayList<Long> longs = new ArrayList<>();
                longs.add(attrId);
                tableOfRelation.put(attrGroupId,longs);
            }
        }
        // 4.2
        ArrayList<GroupWithAttr> groupWithAttrs = new ArrayList<>();
        List<GroupWithAttr> gwas = groupWithAttrs.stream().map(e -> {
            GroupWithAttr group = new GroupWithAttr();
            BeanUtils.copyProperties(e, group);
            return group;
        }).collect(Collectors.toList());
        if(attrEntities==null || attrEntityHashMap==null || attrEntities.size()==0 || attrEntityHashMap.size()==0){
            return gwas;
        }
        for (AttrGroupEntity a:groupList
             ) {
            Long attrGroupId = a.getAttrGroupId();
            GroupWithAttr groupWithAttr = new GroupWithAttr();
            BeanUtils.copyProperties(a,groupWithAttr);
            if(tableOfRelation.containsKey(attrGroupId)){
                ArrayList<Long> longs = tableOfRelation.get(attrGroupId);
                ArrayList<AttrEntity> entityArrayList = new ArrayList<>();
                for (Long l:longs
                     ) {
                    entityArrayList.add(attrEntityHashMap.get(l));
                }
                groupWithAttr.setAttrs(entityArrayList);
            }
            groupWithAttrs.add(groupWithAttr);
        }
        return groupWithAttrs;
    }


}