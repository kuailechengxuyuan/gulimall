package com.zx.gulimall.product.service.impl;

import com.mysql.cj.util.StringUtils;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.zx.common.constant.ProductConstant;
import com.zx.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.zx.gulimall.product.dao.AttrGroupDao;
import com.zx.gulimall.product.dao.CategoryDao;
import com.zx.gulimall.product.entity.*;
import com.zx.gulimall.product.entity.AttrVo;
import com.zx.gulimall.product.service.AttrAttrgroupRelationService;
import com.zx.gulimall.product.service.CategoryService;
import com.zx.gulimall.product.vo.AttrRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.AttrDao;
import com.zx.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttrAndRelation(AttrVo attrVo) {

        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        boolean save = this.save(attrEntity);
        if(attrEntity.getAttrType()== ProductConstant.AttrEnum.AttR_TYPE_BASE.getCode() && attrVo.getAttrGroupId()!=null){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity
                    = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }

    }

    /**
     * 查询分类id对应的基本属性
     * @param params
     * @param catelogId
     * @return
     */
    @Override
    public PageUtils queryPageWithKey(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        // 查询基本属性
        attrEntityQueryWrapper.eq("attr_type",ProductConstant.AttrEnum.AttR_TYPE_BASE.getCode());
        // 添加对应分类id
        if(catelogId!=0){
            attrEntityQueryWrapper.eq("catelog_id",catelogId);
        }
        // 模糊匹配
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
            attrEntityQueryWrapper.and((e)->{
                e.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        //分页
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );

        PageUtils utils = new PageUtils(page);
        //将分页对象中的属性转化为需要的属性
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attr_id = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity,attrRespVo);
            //获取分组名
            Long attrId = attrEntity.getAttrId();
            AttrAttrgroupRelationEntity relationEntity
                    = attrAttrgroupRelationDao.selectOne(
                            new QueryWrapper<AttrAttrgroupRelationEntity>()
                                    .eq("attr_id", attrId)
                    );
            if(relationEntity!=null && relationEntity.getAttrId()!=null && relationEntity.getAttrGroupId()!=null){
                AttrGroupEntity attrGroupEntity
                        = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            //获取分类名
            CategoryEntity categoryEntity
                    = categoryDao.selectById(attrEntity.getCatelogId());
            if(categoryEntity!=null){
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        })).collect(Collectors.toList());
        utils.setList(attr_id);
        return utils;

    }

    @Override
    public AttrRespVo getDetail(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);
        //分类
        long[] categoryPath = categoryService.findCategoryPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(categoryPath);
        //分组
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity
                = attrAttrgroupRelationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", attrId)
        );
        if(attrAttrgroupRelationEntity!=null){
            attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
        }
        return attrRespVo;
    }

    @Override
    @Transactional
    public void updateDeatil(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attr.getAttrId());
        Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attr.getAttrId()));
        if(count>0){
            attrAttrgroupRelationDao.update(
                    relationEntity,
                    new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id",attr.getAttrId())
            );
        }else{
            attrAttrgroupRelationDao.insert(relationEntity);
        }

    }

    @Override
    public PageUtils queryPageOfSale(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<>();
        attrEntityQueryWrapper.eq("attr_type",ProductConstant.AttrEnum.AttR_TYPE_SALE.getCode());
        if(catelogId!=0){
            attrEntityQueryWrapper.eq("catelog_id",catelogId);
        }

        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
            attrEntityQueryWrapper.and((e)->{
                e.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityQueryWrapper
        );

        PageUtils utils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> attr_id = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity,attrRespVo);
            //获取分组名
            /*Long attrId = attrEntity.getAttrId();
            AttrAttrgroupRelationEntity relationEntity
                    = attrAttrgroupRelationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrId)
            );
            if(relationEntity!=null){
                AttrGroupEntity attrGroupEntity
                        = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }*/
            //获取分类名
            CategoryEntity categoryEntity
                    = categoryDao.selectById(attrEntity.getCatelogId());
            if(categoryEntity!=null){
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        })).collect(Collectors.toList());
        utils.setList(attr_id);
        return utils;
    }

    @Override
    public PageUtils getNoAttrRelationList(Long attrgroupId, Map<String, Object> params) {
        // 1 得到catelogId
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 2 得到分组对应的所有已关联属性id
        List<AttrAttrgroupRelationEntity> list
                = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        //.eq("attr_group_id", attrgroupId)
        );
        List<Long> attrIds = list.stream().map(entity -> {
            return entity.getAttrId();
        }).collect(Collectors.toList());
        // 3
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type",ProductConstant.AttrEnum.AttR_TYPE_BASE.getCode());
        // 模糊匹配
        String key = (String) params.get("key");
        if(!StringUtils.isNullOrEmpty(key)){
            wrapper.and((attrEntityQueryWrapper)-> {
                attrEntityQueryWrapper.eq("attr_id",key).or().like("attr_name",key);
                });
        }
        if(attrIds!=null  && attrIds.size()!=0){
            wrapper.notIn("attr_id",attrIds);
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}