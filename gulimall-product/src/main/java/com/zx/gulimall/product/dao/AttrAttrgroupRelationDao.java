package com.zx.gulimall.product.dao;

import com.zx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zx.gulimall.product.vo.RelationDeleteVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-21 12:28:02
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {


    void deleteBatchRelation(@Param("collect") List<AttrAttrgroupRelationEntity> collect);
}
