/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.util.List;

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
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonthReport;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayDayReportService;

/**
 * 通道日发送报表Controller
 * @author zhukc
 * @version 2016-08-04
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgGatewayDayReport")
public class JmsgGatewayDayReportController extends BaseController {

	@Autowired
	private JmsgGatewayDayReportService jmsgGatewayDayReportService;
	
	@ModelAttribute
	public JmsgGatewayDayReport get(@RequestParam(required=false) String id) {
		JmsgGatewayDayReport entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgGatewayDayReportService.get(id);
		}
		if (entity == null){
			entity = new JmsgGatewayDayReport();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgGatewayDayReport:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgGatewayDayReport jmsgGatewayDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgGatewayDayReport.setQueryDay("day");
		Page<JmsgGatewayDayReport> page = jmsgGatewayDayReportService.findPage(new Page<JmsgGatewayDayReport>(request, response), jmsgGatewayDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgGatewayDayReportList";
	}
	
	@RequiresPermissions("sms:jmsgGatewayDayReport:view")
	@RequestMapping(value = "listByMonth")
	public String listByMonth(JmsgGatewayDayReport jmsgGatewayDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgGatewayDayReport.setQueryDay("month");
		Page<JmsgGatewayDayReport> page = jmsgGatewayDayReportService.findMonthPage(new Page<JmsgGatewayDayReport>(request, response), jmsgGatewayDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgGatewayMonthReportList";
	}	
	
	@RequiresPermissions("sms:jmsgGatewayDayReport:view")
	@RequestMapping(value ="exportByDay")
	public String exportByDay(JmsgGatewayDayReport jmsgGatewayDayReport, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "通道统计日报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<JmsgGatewayDayReport> list = jmsgGatewayDayReportService.findListReport(jmsgGatewayDayReport);
    		new ExportExcel("通道统计日报表数据", JmsgGatewayDayReport.class).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出通道统计日报表数据！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayDayReport/list";
	}
	
	@RequiresPermissions("sms:jmsgGatewayDayReport:view")
	@RequestMapping(value ="exportByMonth")
	public String exportByMonth(JmsgGatewayDayReport jmsgGatewayDayReport, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "通道统计月报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<JmsgGatewayMonthReport> list = jmsgGatewayDayReportService.findMonthListReport(jmsgGatewayDayReport);
    		new ExportExcel("通道统计月报表数据", JmsgGatewayMonthReport.class).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出通道统计月报表数据！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayDayReport/listByMonth";
	}	

	@RequiresPermissions("sms:jmsgGatewayDayReport:view")
	@RequestMapping(value = "form")
	public String form(JmsgGatewayDayReport jmsgGatewayDayReport, Model model) {
		model.addAttribute("jmsgGatewayDayReport", jmsgGatewayDayReport);
		return "modules/sms/jmsgGatewayDayReportForm";
	}

	@RequiresPermissions("sms:jmsgGatewayDayReport:edit")
	@RequestMapping(value = "save")
	public String save(JmsgGatewayDayReport jmsgGatewayDayReport, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgGatewayDayReport)){
			return form(jmsgGatewayDayReport, model);
		}
		jmsgGatewayDayReportService.save(jmsgGatewayDayReport);
		addMessage(redirectAttributes, "保存通道日发送报表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayDayReport/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGatewayDayReport:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgGatewayDayReport jmsgGatewayDayReport, RedirectAttributes redirectAttributes) {
		jmsgGatewayDayReportService.delete(jmsgGatewayDayReport);
		addMessage(redirectAttributes, "删除通道日发送报表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayDayReport/?repage";
	}

}