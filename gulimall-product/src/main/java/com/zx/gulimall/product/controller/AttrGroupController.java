package com.zx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.zx.gulimall.product.entity.AttrEntity;
import com.zx.gulimall.product.entity.CategoryEntity;
import com.zx.gulimall.product.service.AttrAttrgroupRelationService;
import com.zx.gulimall.product.service.AttrService;
import com.zx.gulimall.product.service.CategoryService;
import com.zx.gulimall.product.vo.GroupWithAttr;
import com.zx.gulimall.product.vo.RelationDeleteVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zx.gulimall.product.entity.AttrGroupEntity;
import com.zx.gulimall.product.service.AttrGroupService;
import com.zx.common.utils.PageUtils;
import com.zx.common.utils.R;

import javax.websocket.server.PathParam;


/**
 * 属性分组
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2023-05-21 12:28:02
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        long[] path = categoryService.findCategoryPath(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R getAttrGroupRelation(@PathVariable Long attrgroupId){
        List<AttrEntity> attrEntities=  attrGroupService.getRelationAttr(attrgroupId);
        return R.ok().put("data",attrEntities);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getNoAttrRelation(@PathVariable Long attrgroupId,@RequestParam Map<String, Object> params){
        PageUtils page = attrService.getNoAttrRelationList(attrgroupId,params);
        return R.ok().put("page",page);
    }

    @PostMapping("/attr/relation/delete")
    public R relationDelete(@RequestBody RelationDeleteVo[] relationDeleteVos){

        attrAttrgroupRelationService.deleteRelation(relationDeleteVos);

        return R.ok();
    }

    @PostMapping("/attr/relation")
    public R relationAdd(@RequestBody RelationDeleteVo[] relationDeleteVos){
        attrAttrgroupRelationService.addRelation(relationDeleteVos);
        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R getCatelogByAttr(@PathVariable("catelogId") Long catelogId){
        List<GroupWithAttr> groupWithAttrList = attrGroupService.getGroupWithAttr(catelogId);
        return R.ok().put("data",groupWithAttrList);
    }


}
