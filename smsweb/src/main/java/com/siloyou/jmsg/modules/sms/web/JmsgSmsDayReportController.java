/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.alibaba.fastjson.JSON;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.modules.sms.entity.JmsgCustomTask;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMonthReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.service.JmsgCustomTaskService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsCreateDayReportService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsDayReportService;
/**
 * 短信日报表Controller
 * @author zhukc
 * @version 2016-08-04
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsDayReport")
public class JmsgSmsDayReportController extends BaseController {

	@Autowired
	private JmsgSmsDayReportService jmsgSmsDayReportService;
	
	@Autowired
	private JmsgSmsCreateDayReportService jmsgSmsCreateDayReportService;
	
	@Autowired
	private JmsgCustomTaskService jmsgCustomTaskService;
	
	@ModelAttribute
	public JmsgSmsDayReport get(@RequestParam(required=false) String id) {
		JmsgSmsDayReport entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsDayReportService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsDayReport();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "index")
	public String index() {
		return "modules/sms/dayReportIndex";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "createDayReportInit")
	public String createDayReportInit() {
		return "modules/sms/createDayReport";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "createDayReport")
	public String createDayReport(Date day,RedirectAttributes redirectAttributes) {
		String msg = "手动生成日报表成功";
		try{
			jmsgSmsCreateDayReportService.createDayReport(day, false);
		}catch(Exception e){
			msg = "手动生成日报表失败！失败信息："+e.getMessage();
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsDayReport/createDayReportInit";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsDayReport.setQueryType("day");
		
		User user = UserUtils.getUser();
		if(PowerUtils.adminFlag()){
			
		}else if(PowerUtils.agencyFlag()){
			jmsgSmsDayReport.setCompanyId(user.getCompany().getId());
		}else{
			jmsgSmsDayReport.setUser(user);
		}
		
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findPage(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDayReportList";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "listByGateway")
	public String listByGateway(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsDayReport.setQueryType("day");
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findPage(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDayReportByGatewayList";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "listByPhoneType")
	public String listByPhoneType(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsDayReport.setQueryType("day");
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findListReportPhoneType(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDayReportByPhoneTypeList";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "listByMonth")
	public String listByMonth(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsDayReport.setQueryType("month");
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findPage(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsMonthReportList";
	}	
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value ="exportByDay")
	public String exportByDay(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户统计日报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<JmsgSmsDayReport> list = jmsgSmsDayReportService.findListReport(jmsgSmsDayReport);
    		new ExportExcel("用户统计日报表数据", JmsgSmsDayReport.class).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户统计日报表数据失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayDayReport/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value ="exportByMonth")
	public String exportByMonth(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户统计月报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<JmsgSmsMonthReport> list = jmsgSmsDayReportService.findMonthReport(jmsgSmsDayReport);
    		new ExportExcel("用户统计月报表数据", JmsgSmsMonthReport.class).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户统计月报表数据失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayDayReport/listByMonth";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value ="exportByGateway")
	public String exportByGateway(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "通道统计日报表数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<JmsgSmsGatewayReport> list = jmsgSmsDayReportService.findGatewayReport(jmsgSmsDayReport);
    		new ExportExcel("通道统计日报表数据", JmsgSmsGatewayReport.class).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出通道统计日报表数据失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayDayReport/listByGateway";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "onView")
	public String onView(JmsgSmsSend jmsgSmsSend,HttpServletRequest request, HttpServletResponse response, Model model) {
		
		
		jmsgSmsSend.setSendDatetimeQ(DateUtils.getDay(jmsgSmsSend.getSendDatetime(), 0, 0, 0));
		jmsgSmsSend.setSendDatetimeZ(DateUtils.getDay(jmsgSmsSend.getSendDatetime(), 23, 59, 59));
		
		Page<JmsgSmsSend> page = jmsgSmsDayReportService.findViewPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDayReportView";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "viewByPhoneType")
	public String viewByPhoneType(JmsgSmsDayReport jmsgSmsDayReport, String flag, HttpServletRequest request, HttpServletResponse response, Model model) {
		String  redirect = "jmsgSmsDayReportViewPhoneType";
		List<JmsgSmsDayReport> list = jmsgSmsDayReportService.findListPhoneType(jmsgSmsDayReport);
		model.addAttribute("list", list);
		if("NEW".equals(flag)){
			redirect = "jmsgSmsDayReportViewPhoneTypeNew";
		}
		return "modules/sms/"+redirect;
	}

	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsDayReport jmsgSmsDayReport, Model model) {
		model.addAttribute("jmsgSmsDayReport", jmsgSmsDayReport);
		return "modules/sms/jmsgSmsDayReportForm";
	}

	@RequiresPermissions("sms:jmsgSmsDayReport:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsDayReport jmsgSmsDayReport, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsDayReport)){
			return form(jmsgSmsDayReport, model);
		}
		jmsgSmsDayReportService.save(jmsgSmsDayReport);
		addMessage(redirectAttributes, "保存短信日报表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsDayReport/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsDayReport:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsDayReport jmsgSmsDayReport, RedirectAttributes redirectAttributes) {
		jmsgSmsDayReportService.delete(jmsgSmsDayReport);
		addMessage(redirectAttributes, "删除短信日报表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsDayReport/?repage";
	}
	
	//用户通道报表
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "listByUserGateway")
	public String listByUserGateway(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String redirect = "jmsgSmsUserGatewayDayReport";
		if("month".equals(jmsgSmsDayReport.getQueryType())){
			redirect = "jmsgSmsUserGatewayMonthReport";
		}
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findUserGatewayPage(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/"+redirect;
	}
	
	//通道报表
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "gatewayReport")
	public String gatewayReport(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		String redirect = "jmsgSmsGatewayDayReport";
		if("month".equals(jmsgSmsDayReport.getQueryType())){
			redirect = "jmsgSmsGatewayMonthReport";
		}
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findGatewayPage(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/"+redirect;
	}
	
	//日报表
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "allDayExport")
	public String allDayExport(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findDayReprotPage(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsAllDayReport";
	}
	
	//用户状态报告监控管理
	@RequiresPermissions("sms:jmsgSmsDayReport:view")
	@RequestMapping(value = "cmppUserReportStatus")
	public String cmppUserReportStatus(JmsgSmsDayReport jmsgSmsDayReport, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsDayReport.setQueryType("day");
		jmsgSmsDayReport.setUserCategory("1");
		Page<JmsgSmsDayReport> page = jmsgSmsDayReportService.findPage(new Page<JmsgSmsDayReport>(request, response), jmsgSmsDayReport); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsUserReportList";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "taskPush")
	public String taskPush(Date createDatetime,String userId,String name, String pushFlag,HttpServletResponse response){
		String tableName = "jmsg_sms_send_"+DateUtils.getTableIndex(createDatetime);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tableName", tableName);
		param.put("userId", userId);
		param.put("createDatetimeQ", DateUtils.formatDate(DateUtils.getDay(createDatetime, 0, 0, 0), "yyyy-MM-dd HH:mm:ss"));
		param.put("createDatetimeZ", DateUtils.formatDate(DateUtils.getDay(createDatetime, 23, 59, 59),"yyyy-MM-dd HH:mm:ss"));
		param.put("pushFlag", pushFlag);
		
		try {
			name = URLDecoder.decode(name,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String day = DateUtils.formatDate(createDatetime, "yyyy-MM-dd");
		String taskName = day +"_"+name+"_"+("9".equals(pushFlag)?"失败补退":"全部补推");
		
		JmsgCustomTask customTask = new JmsgCustomTask();
		customTask.setTaskName(taskName);
		customTask.setType("1");
		customTask.setParamJson(JSON.toJSONString(param));
		customTask.setExecuteClass("com.siloyou.jmsg.modules.sms.task.impl.SmsCustomTaskPushExecutor");
		customTask.setStatus("1");
		customTask.setVersion("1");
		
		jmsgCustomTaskService.save(customTask);
		String key = CacheKeys.getPushReportStatus(userId, day);
		JedisClusterUtils.set(key, "1", 259200);//缓存3天
		
		return renderString(response, "创建推送用户状态任务成功");
	}


}