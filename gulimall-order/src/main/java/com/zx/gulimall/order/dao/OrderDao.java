package com.zx.gulimall.order.dao;

import com.zx.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-25 09:30:42
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
