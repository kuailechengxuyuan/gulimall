package com.zx.gulimall.product.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RelationDeleteVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long attrId;

    private Long attrGroupId;
}
