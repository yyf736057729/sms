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
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReportSign;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsReportSignService;

/**
 * 签名统计报表Controller
 * @author zhukc
 * @version 2017-05-25
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsReportSign")
public class JmsgSmsReportSignController extends BaseController {

	@Autowired
	private JmsgSmsReportSignService jmsgSmsReportSignService;
	
	@ModelAttribute
	public JmsgSmsReportSign get(@RequestParam(required=false) String id) {
		JmsgSmsReportSign entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsReportSignService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsReportSign();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsReportSign:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsReportSign jmsgSmsReportSign, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsReportSign> page = jmsgSmsReportSignService.findPage(new Page<JmsgSmsReportSign>(request, response), jmsgSmsReportSign); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsReportSignList";
	}
	
	@RequiresPermissions("sms:jmsgSmsReportSign:view")
	@RequestMapping(value = "usedList")
	public String usedList(JmsgSmsReportSign jmsgSmsReportSign, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsReportSign> page = jmsgSmsReportSignService.findUsedSignPage(new Page<JmsgSmsReportSign>(request, response), jmsgSmsReportSign); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsUsedSignList";
	}

	@RequiresPermissions("sms:jmsgSmsReportSign:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsReportSign jmsgSmsReportSign, Model model) {
		model.addAttribute("jmsgSmsReportSign", jmsgSmsReportSign);
		return "modules/sms/jmsgSmsReportSignForm";
	}

	@RequiresPermissions("sms:jmsgSmsReportSign:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsReportSign jmsgSmsReportSign, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsReportSign)){
			return form(jmsgSmsReportSign, model);
		}
		jmsgSmsReportSignService.save(jmsgSmsReportSign);
		addMessage(redirectAttributes, "保存签名统计报表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsReportSign/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsReportSign:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsReportSign jmsgSmsReportSign, RedirectAttributes redirectAttributes) {
		jmsgSmsReportSignService.delete(jmsgSmsReportSign);
		addMessage(redirectAttributes, "删除签名统计报表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsReportSign/?repage";
	}

}