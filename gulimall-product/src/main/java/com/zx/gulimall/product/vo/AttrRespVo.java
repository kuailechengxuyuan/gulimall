package com.zx.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zx.gulimall.product.entity.AttrVo;
import lombok.Data;

@Data
public class AttrRespVo extends AttrVo {
    /**
     * "catelogName": "手机/数码/手机",//所属分类名字
     * "groupName": "主体", //所属分组名字
     */
    private String catelogName;

    private String groupName;

    private long[] catelogPath;



}
