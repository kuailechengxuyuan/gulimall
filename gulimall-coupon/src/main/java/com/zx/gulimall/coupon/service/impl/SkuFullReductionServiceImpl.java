package com.zx.gulimall.coupon.service.impl;

import com.zx.gulimall.coupon.entity.MemberPriceEntity;
import com.zx.gulimall.coupon.entity.SkuLadderEntity;
import com.zx.gulimall.coupon.service.MemberPriceService;
import com.zx.gulimall.coupon.service.SkuLadderService;
import com.zx.gulimall.coupon.to.SkuReductionTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.coupon.dao.SkuFullReductionDao;
import com.zx.gulimall.coupon.entity.SkuFullReductionEntity;
import com.zx.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveReduceInfo(SkuReductionTo skuReductionTo) {
        // 1.sku的打折（买几件打几折）sms_sku_ladder【剔除满减信息为0的】
        if(skuReductionTo.getFullCount()>0){
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }
        if(skuReductionTo.getFullPrice()!=null  && skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO)==1){
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
            skuFullReductionEntity.setAddOther(skuReductionTo.getPriceStatus());
            this.save(skuFullReductionEntity);
        }
        Long skuId = skuReductionTo.getSkuId();
        List<MemberPriceEntity> memberPriceEntityList = skuReductionTo.getMemberPrice().stream()
                .filter(memberPrice -> memberPrice.getPrice().compareTo(BigDecimal.ZERO) == 1)
                .map(memberPrice -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(skuId);
                    memberPriceEntity.setMemberLevelName(memberPrice.getName());
                    memberPriceEntity.setMemberLevelId(memberPrice.getId());
                    memberPriceEntity.setAddOther(1);
                    memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                    return memberPriceEntity;
                }).collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntityList);

    }

}