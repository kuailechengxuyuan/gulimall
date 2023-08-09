package com.zx.gulimall.product.dao;

import com.zx.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-21 12:28:02
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
