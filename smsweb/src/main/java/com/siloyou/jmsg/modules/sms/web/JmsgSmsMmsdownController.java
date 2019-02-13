/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMmsdown;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsMmsdownService;

/**
 * 短信彩信下载Controller
 * @author zhukc
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsMmsdown")
public class JmsgSmsMmsdownController extends BaseController {

	@Autowired
	private JmsgSmsMmsdownService jmsgSmsMmsdownService;
	
	@ModelAttribute
	public JmsgSmsMmsdown get(@RequestParam(required=false) String id) {
		JmsgSmsMmsdown entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsMmsdownService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsMmsdown();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsMmsdown:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsMmsdown jmsgSmsMmsdown, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsMmsdown> page = jmsgSmsMmsdownService.findPage(new Page<JmsgSmsMmsdown>(request, response), jmsgSmsMmsdown); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsMmsdownList";
	}

	@RequiresPermissions("sms:jmsgSmsMmsdown:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsMmsdown jmsgSmsMmsdown, Model model) {
		model.addAttribute("jmsgSmsMmsdown", jmsgSmsMmsdown);
		return "modules/sms/jmsgSmsMmsdownForm";
	}

	@RequiresPermissions("sms:jmsgSmsMmsdown:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsMmsdown jmsgSmsMmsdown, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsMmsdown)){
			return form(jmsgSmsMmsdown, model);
		}
		jmsgSmsMmsdownService.save(jmsgSmsMmsdown);
		addMessage(redirectAttributes, "保存短信彩信下载成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsMmsdown/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsMmsdown:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsMmsdown jmsgSmsMmsdown, RedirectAttributes redirectAttributes) {
		jmsgSmsMmsdownService.delete(jmsgSmsMmsdown);
		addMessage(redirectAttributes, "删除短信彩信下载成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsMmsdown/?repage";
	}

}