package com.zx.gulimall.product.service.impl;

import com.zx.common.constant.ObjectConstant;
import com.zx.common.utils.R;
import com.zx.gulimall.product.agent.CouponAgentService;
import com.zx.gulimall.product.entity.SkuImagesEntity;
import com.zx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.zx.gulimall.product.service.SkuImagesService;
import com.zx.gulimall.product.service.SkuSaleAttrValueService;
import com.zx.gulimall.product.vo.Images;
import com.zx.gulimall.product.vo.Skus;
import com.zx.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.Query;

import com.zx.gulimall.product.dao.SkuInfoDao;
import com.zx.gulimall.product.entity.SkuInfoEntity;
import com.zx.gulimall.product.service.SkuInfoService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponAgentService couponAgentService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * todo 对循环查表进行进一步改进
     * @param spuId
     * @param skus
     * @param spuInfo
     */
    @Override
    public void saveSkuInfo(Long spuId, List<Skus> skus, SpuSaveVo spuInfo) {
        if(CollectionUtils.isEmpty(skus)){
            return;
        }

        skus.forEach(s->{
            //6.1） sku的基本信息pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            String defaultImage = null;
            for (Images img : s.getImages()) {
                if (img.getDefaultImg() == ObjectConstant.BooleanIntEnum.YES.getCode()) {
                    defaultImage = img.getImgUrl();
                    break;
                }
            }
            skuInfoEntity.setSkuDefaultImg(defaultImage);
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
            skuInfoEntity.setBrandId(spuInfo.getBrandId());
            skuInfoEntity.setSaleCount(0L);
            BeanUtils.copyProperties(s, skuInfoEntity);
            this.baseMapper.insert(skuInfoEntity);
            // 6.2）sku的图片信息pms_sku_images
            List<SkuImagesEntity> skuImagesEntityList = s.getImages()
                    .stream()
                    .filter(image -> !StringUtils.isEmpty(image.getImgUrl()))
                    .map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                        BeanUtils.copyProperties(image, skuImagesEntity);
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
            skuImagesService.saveBatch(skuImagesEntityList);
            // 6.3）sku的销售属性信息pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = s.getAttr().stream().map(a -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);

            // 6.4）sku的优惠满减等信息gulimalltwice_sms——》sms_sku_ladder  sms_sku_full_reduction  sms_member_price
            if(s.getFullCount()>0 || s.getFullPrice().compareTo(BigDecimal.ZERO)==1){
                R r = couponAgentService.saveSkuReduction(skuInfoEntity.getSkuId(),s);
                if(r.getCode()!=0){
                    log.error("远程调用失败");
                }
            }


        });






    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(qw -> {
                qw.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_Id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        // 价格区间
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {
            }

        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }


}