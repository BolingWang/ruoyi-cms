package com.ruoyi.cms.web.controller;

import com.ruoyi.cms.system.mapper.CmsTagMapper;
import com.ruoyi.cms.system.model.CmsConstants;
import com.ruoyi.cms.system.model.po.CmsTag;
import com.ruoyi.cms.system.model.vo.TagCountVo;
import com.ruoyi.cms.system.service.impl.CmsTagServiceImpl;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.util.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import java.util.List;

@Controller
@RequestMapping("/cms/tag")
public class CmsTagController extends BaseController {

   private String prefix = "blog/admin/tag";

   @Autowired
   private CmsTagServiceImpl cmsTagService;

   @RequiresPermissions("cms:tag:view")
   @GetMapping()
   public String tag()
   {
      return prefix + "/tag";
   }

   @RequiresPermissions("cms:tag:list")
   @PostMapping("/list")
   @ResponseBody
   public TableDataInfo list(CmsTag cmsTag){
      startPage();
      List<CmsTag> cmsTags=cmsTagService.listCmsTag(cmsTag);
      return  getDataTable(cmsTags);
   }

   @RequiresPermissions("cms:tag:list")
   @GetMapping("/listTag")
   @ResponseBody
   public List<CmsTag> listNoHide(){
      return cmsTagService.listCmsTag();

   }

   @RequiresPermissions("cms:tag:list")
   @GetMapping("/listTagCountNoHide")
   @ResponseBody
   public List<TagCountVo> listCountVoNoHide(){

      return  cmsTagService.listTagCountVo();
   }

   @RequiresPermissions("cms:tag:list")
   @PostMapping("/listTagCountVo")
   @ResponseBody
   public List<TagCountVo> listTagCountVo(CmsTag cmsTag){
      startPage();
      return  cmsTagService.listTagCountVoByTag(cmsTag);
   }


   @GetMapping("/add")
   @RequiresPermissions("cms:tag:add")
   public String add(){
      return  prefix + "/add";
   }

   @Log(title = "内容标签管理", businessType = BusinessType.INSERT)
   @RequiresPermissions("cms:tag:add")
   @PostMapping("/add")
   @ResponseBody
   public AjaxResult addSave(@Validated CmsTag cmsTag){
        if (CmsConstants.UNIQUE.equals(cmsTagService.checkTagNameUnique(cmsTag))&&CmsConstants.UNIQUE.equals(cmsTagService.checkTagUrlUnique(cmsTag)))
        { cmsTag.setCreateBy(ShiroUtils.getUserId());
       return toAjax( cmsTagService.insertCmsTag(cmsTag));
        }

      return error("标签名重复|路径重复");
   }

   @Log(title = "内容标签管理", businessType = BusinessType.DELETE)
   @RequiresPermissions("cms:tag:remove")
   @PostMapping("/remove")
   @ResponseBody
   public AjaxResult remove(String ids){

       Long[] tagIds = Convert.toLongArray(ids);
       int sc=  cmsTagService.deleteCmsTagByIds(tagIds);
       if (tagIds.length==sc) {

          return success("共:" + tagIds.length + "个，成功删除：" + sc + "个");
       }
       else{
          return error("共:" + tagIds.length + "个，成功删除：" + sc + "个.<br/>未成功删除的可能是与文章关联");
       }

   }

   /**
    * 修改标签
    */
   @GetMapping("/edit/{tagId}")
   @RequiresPermissions("cms:tag:edit")
   public String edit(@PathVariable("tagId") Long tagId, ModelMap mmap)
   {
      mmap.put("tag", cmsTagService.getCmsTagById(tagId));
      return prefix + "/edit";
   }

   /**
    * 标签状态操作
    * @param tagIds
    * @return
    */
   @PostMapping("/editTagVisible")
   @ResponseBody
   public AjaxResult editTagVisible(String tagIds,Byte visible) {
      Long[] ids = Convert.toLongArray(tagIds);
      return toAjax(cmsTagService.updateTagVisible(ids,visible));
   }

   /**
    * 修改保存菜单
    */
   @Log(title = "内容标签管理", businessType = BusinessType.UPDATE)
   @RequiresPermissions("cms:tag:edit")
   @PostMapping("/edit")
   @ResponseBody
   public AjaxResult editSave(@Validated CmsTag cmsTag)
   {
      if (CmsConstants.UNIQUE.equals(cmsTagService.checkTagNameUnique(cmsTag))&&CmsConstants.UNIQUE.equals(cmsTagService.checkTagUrlUnique(cmsTag)))
      {
         cmsTag.setUpdateBy(ShiroUtils.getUserId());
         return toAjax(cmsTagService.updateCmsTagById(cmsTag));

      }
      return error("修改标签'" + cmsTag.getTagName() + "'失败，标签名称|路径已存在");
   }



   /**
    * 校验标签名称
    */
   @PostMapping("/checkTagNameUnique")
   @ResponseBody
   public String checkCmsTagNameUnique( CmsTag cmsTag)
   {

      return cmsTagService.checkTagNameUnique(cmsTag);
   }

   /**
    * 校验标签名称
    */
   @PostMapping("/checkTagUrlUnique")
   @ResponseBody
   public String checkCmsTagUrlUnique(CmsTag cmsTag)
   {
      return cmsTagService.checkTagUrlUnique(cmsTag);
   }
}
