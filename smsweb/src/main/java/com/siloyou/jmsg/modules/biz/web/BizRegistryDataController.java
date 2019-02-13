/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.biz.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.biz.entity.BizRegistryData;
import com.siloyou.jmsg.modules.biz.service.BizRegistryDataService;

/**
 * 活动登录短信发送记录Controller
 * @author huangjie
 * @version 2017-07-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/bizRegistryData")
public class BizRegistryDataController extends BaseController {

	@Autowired
	private BizRegistryDataService bizRegistryDataService;
	
	@ModelAttribute
	public BizRegistryData get(@RequestParam(required=false) String id) {
		BizRegistryData entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bizRegistryDataService.get(id);
		}
		if (entity == null){
			entity = new BizRegistryData();
		}
		return entity;
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(BizRegistryData bizRegistryData, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<BizRegistryData> page = bizRegistryDataService.findPage(new Page<BizRegistryData>(request, response), bizRegistryData); 
		model.addAttribute("page", page);
		return "modules/biz/bizRegistryDataList";
	}

	@RequiresPermissions("sms:bizRegistryData:view")
	@RequestMapping(value = "form")
	public String form(BizRegistryData bizRegistryData, Model model) {
		model.addAttribute("bizRegistryData", bizRegistryData);
		return "modules/biz/bizRegistryDataForm";
	}

	@RequiresPermissions("sms:bizRegistryData:edit")
	@RequestMapping(value = "save")
	public String save(BizRegistryData bizRegistryData, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizRegistryData)){
			return form(bizRegistryData, model);
		}
		bizRegistryDataService.save(bizRegistryData);
		addMessage(redirectAttributes, "保存活动登记短信发送成功");
		return "redirect:"+Global.getAdminPath()+"/sms/bizRegistryData/?repage";
	}
	
	@RequiresPermissions("sms:bizRegistryData:edit")
	@RequestMapping(value = "delete")
	public String delete(BizRegistryData bizRegistryData, RedirectAttributes redirectAttributes) {
		bizRegistryDataService.delete(bizRegistryData);
		addMessage(redirectAttributes, "删除活动登记短信发送成功");
		return "redirect:"+Global.getAdminPath()+"/sms/bizRegistryData/?repage";
	}
	
	
    
	/**
	 * 导出数据
	 * @param user
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(BizRegistryData bizRegistryData, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "活动用户登记数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<BizRegistryData> page = bizRegistryDataService.findPage(new Page<BizRegistryData>(request, response, -1), bizRegistryData);
    		new ExportExcel("活动用户登记数据", BizRegistryData.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出活动登记数据失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sms/bizRegistryData/list?repage";
    }
	
	@RequestMapping(value="tes")
	public String cesi(){
        String data = "[{\"id\":\"9d9d0f4d1b864c129e299cc400c26306\",\"time\":\"20161011115655\",\"msgcontent\":\"ks\",\"srcid\":\"1069018889001\",\"mobile\":\"13419624389\"}]";
        
		bizRegistryDataService.sendActitiyCode(data);
		return null;
	}
}