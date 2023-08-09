package com.zx.gulimall.product.service.impl;

import com.zx.gulimall.product.agent.CouponAgentService;
import com.zx.gulimall.product.service.*;
import com.zx.gulimall.product.vo.BaseAttrs;
import com.zx.gulimall.product.vo.Bounds;
import com.zx.gulimall.product.vo.Skus;
import com.zx.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.SpuInfoDao;
import com.zx.gulimall.product.entity.SpuInfoEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private CouponAgentService couponAgentService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * todo 高级部分继续完善
     * @param spuInfo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {

        // 1 保存spu基本信息pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        Date date = new Date();
        spuInfoEntity.setCreateTime(date);
        spuInfoEntity.setUpdateTime(date);
        this.saveSpuInfoEntity(spuInfoEntity);

        // 2 保存Spu图片描述信息pms_spu_info_desc
        Long spuId = spuInfoEntity.getId();
        List<String> decript = spuInfo.getDecript();
        spuInfoDescService.saveSpuDesc(spuId,decript);

        // 3 保存spu图片集pms_spu_images
        List<String> images = spuInfo.getImages();
        spuImagesService.saveImages(spuId,images);
        // 4 保存规格参数信息pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        productAttrValueService.saveAttrValue(spuId,baseAttrs);

        // 5 保存spu的积分信息sms_spu_bounds
        Bounds bounds = spuInfo.getBounds();
        couponAgentService.saveSpuBounds(spuId,bounds);

        // 6 保存spu对应sku信息
        // 6.1） sku的基本信息pms_sku_info
        // 6.2）sku的图片信息pms_sku_images
        // 6.3）sku的销售属性信息pms_sku_sale_attr_value
        // 6.4）sku的优惠满减等信息gulimalltwice_sms——》sms_sku_ladder  sms_sku_full_reduction  sms_member_price
        List<Skus> skus = spuInfo.getSkus();
        skuInfoService.saveSkuInfo(spuId,skus,spuInfo);

    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && Long.parseLong(catelogId)!=0L) {
            wrapper.eq("catelog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && Long.parseLong(brandId)!=0L) {
            wrapper.eq("brand_id", brandId);
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    private void saveSpuInfoEntity(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}