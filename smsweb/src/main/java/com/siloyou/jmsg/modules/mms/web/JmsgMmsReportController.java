/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.web;

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
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsReport;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsReportService;

/**
 * 状态报告Controller
 * @author zhukc
 * @version 2016-05-28
 */
@Controller
@RequestMapping(value = "${adminPath}/mms/jmsgMmsReport")
public class JmsgMmsReportController extends BaseController {

	@Autowired
	private JmsgMmsReportService jmsgMmsReportService;
	
	@ModelAttribute
	public JmsgMmsReport get(@RequestParam(required=false) String id) {
		JmsgMmsReport entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgMmsReportService.get(id);
		}
		if (entity == null){
			entity = new JmsgMmsReport();
		}
		return entity;
	}
	
	@RequiresPermissions("mms:jmsgMmsReport:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgMmsReport jmsgMmsReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgMmsReport> page = jmsgMmsReportService.findPage(new Page<JmsgMmsReport>(request, response), jmsgMmsReport); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsReportList";
	}

	@RequiresPermissions("mms:jmsgMmsReport:view")
	@RequestMapping(value = "form")
	public String form(JmsgMmsReport jmsgMmsReport, Model model) {
		model.addAttribute("jmsgMmsReport", jmsgMmsReport);
		return "modules/mms/jmsgMmsReportForm";
	}

	@RequiresPermissions("mms:jmsgMmsReport:edit")
	@RequestMapping(value = "save")
	public String save(JmsgMmsReport jmsgMmsReport, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgMmsReport)){
			return form(jmsgMmsReport, model);
		}
		jmsgMmsReportService.save(jmsgMmsReport);
		addMessage(redirectAttributes, "保存状态报告成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsReport/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsReport:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgMmsReport jmsgMmsReport, RedirectAttributes redirectAttributes) {
		jmsgMmsReportService.delete(jmsgMmsReport);
		addMessage(redirectAttributes, "删除状态报告成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsReport/?repage";
	}

}