/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.siloyou.jmsg.common.utils.MQUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneInfo;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneInfoService;

/**
 * 号段管理Controller
 * @author zhukc
 * @version 2016-07-30
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhoneInfo")
public class JmsgPhoneInfoController extends BaseController {

	@Autowired
	private JmsgPhoneInfoService jmsgPhoneInfoService;
	
	@ModelAttribute
	public JmsgPhoneInfo get(@RequestParam(required=false) String id) {
		JmsgPhoneInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgPhoneInfoService.get(id);
		}
		if (entity == null){
			entity = new JmsgPhoneInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgPhoneInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgPhoneInfo jmsgPhoneInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgPhoneInfo> page = jmsgPhoneInfoService.findPage(new Page<JmsgPhoneInfo>(request, response), jmsgPhoneInfo); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgPhoneInfoList";
	}

	@RequiresPermissions("sms:jmsgPhoneInfo:view")
	@RequestMapping(value = "form")
	public String form(JmsgPhoneInfo jmsgPhoneInfo, Model model) {
		model.addAttribute("jmsgPhoneInfo", jmsgPhoneInfo);
		return "modules/sms/jmsgPhoneInfoForm";
	}

	@RequiresPermissions("sms:jmsgPhoneInfo:edit")
	@RequestMapping(value = "save")
	public String save(JmsgPhoneInfo jmsgPhoneInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgPhoneInfo)){
			return form(jmsgPhoneInfo, model);
		}
		jmsgPhoneInfoService.delete(jmsgPhoneInfo);
		jmsgPhoneInfoService.save(jmsgPhoneInfo);
		addMessage(redirectAttributes, "保存号段管理成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneInfo/?repage";
	}
	
	@RequiresPermissions("sms:jmsgPhoneInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgPhoneInfo jmsgPhoneInfo, RedirectAttributes redirectAttributes) {
		jmsgPhoneInfoService.delete(jmsgPhoneInfo);
		addMessage(redirectAttributes, "删除号段管理成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneInfo/?repage";
	}

	@Autowired
	private MQUtils mQUtils;
	@RequiresPermissions("sms:jmsgPhoneInfo:edit")
	@RequestMapping(value = "onCache")
	public String onCache(RedirectAttributes redirectAttributes) {
		String str ="onCache";
		String s = mQUtils.sendSmsMT("onCacheJmsgPhoneInfo", "onCache", "onCache", str.getBytes());
		if(s.equals("-1")){
			addMessage(redirectAttributes, "缓存失败");
		}else {
			addMessage(redirectAttributes, "缓存成功！");
		}
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneInfo/";
	}
}