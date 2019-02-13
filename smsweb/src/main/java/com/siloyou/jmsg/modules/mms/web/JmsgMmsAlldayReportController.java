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
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsAlldayReport;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsAlldayReportService;

/**
 * 彩信总日报Controller
 * @author zhukc
 * @version 2016-06-08
 */
@Controller
@RequestMapping(value = "${adminPath}/mms/jmsgMmsAlldayReport")
public class JmsgMmsAlldayReportController extends BaseController {

	@Autowired
	private JmsgMmsAlldayReportService jmsgMmsAlldayReportService;
	
	@ModelAttribute
	public JmsgMmsAlldayReport get(@RequestParam(required=false) String id) {
		JmsgMmsAlldayReport entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgMmsAlldayReportService.get(id);
		}
		if (entity == null){
			entity = new JmsgMmsAlldayReport();
		}
		return entity;
	}
	
	@RequiresPermissions("mms:jmsgMmsAlldayReport:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgMmsAlldayReport jmsgMmsAlldayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgMmsAlldayReport> page = jmsgMmsAlldayReportService.findPage(new Page<JmsgMmsAlldayReport>(request, response), jmsgMmsAlldayReport); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsAlldayReportList";
	}
	
	//任务状态报告报表
	@RequiresPermissions("mms:jmsgMmsAlldayReport:view")
	@RequestMapping(value = "reportBylist")
	public String reportBylist(JmsgMmsAlldayReport jmsgMmsAlldayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		if(jmsgMmsAlldayReport.getDayQ() == null){
			jmsgMmsAlldayReport.setDayQ(DateUtils.getDay(0, 0, 0));
		}
		if(jmsgMmsAlldayReport.getDayZ() == null){
			jmsgMmsAlldayReport.setDayZ(DateUtils.getDay(23, 59, 59));
		}
		
		Page<JmsgMmsAlldayReport> page = jmsgMmsAlldayReportService.reportFindPage(new Page<JmsgMmsAlldayReport>(request, response), jmsgMmsAlldayReport); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsReportList";
	}
	
	//任务状态报告报表明细
	@RequiresPermissions("mms:jmsgMmsAlldayReport:view")
	@RequestMapping(value = "reportBylistDetail")
	public String reportBylistDetail(JmsgMmsAlldayReport jmsgMmsAlldayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<JmsgMmsAlldayReport> page = jmsgMmsAlldayReportService.reportFindPageDetail(new Page<JmsgMmsAlldayReport>(request, response), jmsgMmsAlldayReport); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsReportDetailList";
	}	

	@RequiresPermissions("mms:jmsgMmsAlldayReport:view")
	@RequestMapping(value = "form")
	public String form(JmsgMmsAlldayReport jmsgMmsAlldayReport, Model model) {
		model.addAttribute("jmsgMmsAlldayReport", jmsgMmsAlldayReport);
		return "modules/mms/jmsgMmsAlldayReportForm";
	}

	@RequiresPermissions("mms:jmsgMmsAlldayReport:edit")
	@RequestMapping(value = "save")
	public String save(JmsgMmsAlldayReport jmsgMmsAlldayReport, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgMmsAlldayReport)){
			return form(jmsgMmsAlldayReport, model);
		}
		jmsgMmsAlldayReportService.save(jmsgMmsAlldayReport);
		addMessage(redirectAttributes, "保存彩信总日报成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsAlldayReport/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsAlldayReport:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgMmsAlldayReport jmsgMmsAlldayReport, RedirectAttributes redirectAttributes) {
		jmsgMmsAlldayReportService.delete(jmsgMmsAlldayReport);
		addMessage(redirectAttributes, "删除彩信总日报成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsAlldayReport/?repage";
	}
	
	@RequestMapping(value = "taskStatusCount")
	public String taskStatusCount(String taskId,HttpServletResponse response){
		String result = "";
		Long submitCount = jmsgMmsAlldayReportService.submitCountByTaskId(taskId);
		Long reportCount = jmsgMmsAlldayReportService.reportCountByTaskId(taskId);
		result = "{\"submitCount:\""+submitCount+"\",\"reprotCount\":\""+reportCount+"\"}";
		return renderString(response, result, "application/json");
	}

}