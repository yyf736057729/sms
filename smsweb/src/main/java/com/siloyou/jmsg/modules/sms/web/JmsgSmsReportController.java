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
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReport;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsReportService;

/**
 * 状态报告Controller
 * @author zhukc
 * @version 2016-08-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsReport")
public class JmsgSmsReportController extends BaseController {

	@Autowired
	private JmsgSmsReportService jmsgSmsReportService;
	
	@ModelAttribute
	public JmsgSmsReport get(@RequestParam(required=false) String id) {
		JmsgSmsReport entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsReportService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsReport();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsReport:view")
	@RequestMapping(value = "init")
	public String init() {
		return "modules/sms/jmsgSmsReportList";
	}
	
	@RequiresPermissions("sms:jmsgSmsReport:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsReport jmsgSmsReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsReport> page = jmsgSmsReportService.findPage(new Page<JmsgSmsReport>(request, response), jmsgSmsReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsReportList";
	}
	
	@RequestMapping(value = "listBytaskId")
	public String listBytaskId(JmsgSmsReport jmsgSmsReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsReport> page = jmsgSmsReportService.findPage(new Page<JmsgSmsReport>(request, response), jmsgSmsReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsReportDetail";
	}

	@RequiresPermissions("sms:jmsgSmsReport:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsReport jmsgSmsReport, Model model) {
		model.addAttribute("jmsgSmsReport", jmsgSmsReport);
		return "modules/sms/jmsgSmsReportForm";
	}

	@RequiresPermissions("sms:jmsgSmsReport:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsReport jmsgSmsReport, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsReport)){
			return form(jmsgSmsReport, model);
		}
		jmsgSmsReportService.save(jmsgSmsReport);
		addMessage(redirectAttributes, "保存状态报告成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsReport/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsReport:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsReport jmsgSmsReport, RedirectAttributes redirectAttributes) {
		jmsgSmsReportService.delete(jmsgSmsReport);
		addMessage(redirectAttributes, "删除状态报告成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsReport/?repage";
	}

}