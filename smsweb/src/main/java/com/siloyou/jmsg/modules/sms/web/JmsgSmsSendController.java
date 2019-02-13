/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.*;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.sanerzone.common.support.utils.Base64Util;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.common.utils.Constants;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.service.JmsgCustomTaskService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsMmsdownService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsReportService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSendService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSubmitService;

/**
 * 短信发送Controller
 * @author zhukc
 * @version 2016-07-16
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsSend")
public class JmsgSmsSendController extends BaseController {

	@Autowired
	private JmsgSmsSendService jmsgSmsSendService;
	
	@Autowired
	private JmsgSmsReportService jmsgSmsReportService;
	
	@Autowired
	private JmsgSmsSubmitService jmsgSmsSubmitService;
	
	@Autowired
	private JmsgCustomTaskService jmsgCustomTaskService;
	@Autowired
	private JmsgSmsMmsdownService jmsgSmsMmsdownService;


	@ModelAttribute
	public JmsgSmsSend get(@RequestParam(required=false) String id) {
		JmsgSmsSend entity = null;
		if (StringUtils.isNotBlank(id)){
			//entity = jmsgSmsSendService.get(id);
			String tableIndex = TableNameUtil.getTableIndex(id);// 解析jmsg_sms_send_1, jmsg_sms_send_2, ..., jmsg_sms_send_31
			if (Integer.parseInt(tableIndex) > 31 || Integer.parseInt(tableIndex) < 1)
			{
				Calendar current = Calendar.getInstance();
				tableIndex = String.valueOf(current.get(Calendar.DAY_OF_MONTH));
			}
			
			JmsgSmsSend param = new JmsgSmsSend();
			//param.setTableName("jmsg_sms_send_" + tableIndex); 列表是从jmsg_sms_send查询的, 分表之后不知道怎么查询, 先不分表, 等量起来了再说
			param.setTableName("jmsg_sms_send");
			param.setId(id);
			entity = jmsgSmsSendService.getV2(param);
		}
		if (entity == null){
			entity = new JmsgSmsSend();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "resultInit")
	public String resultInit( HttpServletRequest request, HttpServletResponse response, Model model){
		model.addAttribute("queryType", "detail");
		return "modules/sms/jmsgSmsSendResult";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "sendResultOld")
	public String sendResult(String taskId,String phone,Model model) {
	
		JmsgSmsSend param = new JmsgSmsSend();
		param.setTaskId(taskId);
		param.setPhone(phone);
		JmsgSmsSend entity = jmsgSmsSendService.queryJmsgSmsSend(param);
		
		if(entity !=null){
			if(PowerUtils.adminFlag()){
				
			}else if(PowerUtils.agencyFlag()){
				if(!StringUtils.equals(entity.getCompanyId(), UserUtils.getUser().getCompany().getId())){
					entity = null;
				}
				
			}else{
				if(!StringUtils.equals(entity.getUser().getId(), UserUtils.getUser().getId())){
					entity = null;
				}
			}
		}
		
		if(entity != null){
			String result = "状态未知";
			if("T0".equals(entity.getSendStatus())){
				String payType = entity.getPayType();//0:提交 2：网关状态
				if("0".equals(payType)){//提交
					result = "成功";
				}else if("2".equals(payType)){//状态
					JmsgSmsReport jmsgSmsReport = new JmsgSmsReport();
					jmsgSmsReport.setBizid(entity.getId());
					jmsgSmsReport.setDestTerminalId(phone);
					jmsgSmsReport.setTaskid(taskId);
					List<JmsgSmsReport> list = jmsgSmsReportService.findList(jmsgSmsReport);
					boolean flag = false;
					if(list != null && list.size() > 0){
						for (JmsgSmsReport bo : list) {
							if("DELIVRD".equals(bo.getStat())){
								flag = true;
							}
						}
						if(flag){
							result = "成功";
						}else{
							result = "失败";
						}
					}
				}
			}else{
				result = DictUtils.getDictLabel(entity.getSendStatus(), "mms_send_status", entity.getSendStatus());
			}
			entity.setSendResult(result);
		}else{
			addMessage(model, "查无此信息!");
		}
		
		model.addAttribute("jmsgSmsSend", entity);
		model.addAttribute("taskId", taskId);
		model.addAttribute("phone", phone);
			
		return "modules/sms/jmsgSmsSendResult";
	}
	
	/**@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "sendResult")
	public String sendResult(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		String payMode = UserUtils.getPayMode(user.getId(), "sms");
		jmsgSmsSend.setUser(user);
		Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend); 
//		Page<JmsgSmsSend> page = null;
//		if("0".equals(payMode)){//提交扣费
//			page = jmsgSmsSendService.findSendDetailPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend); 
//		}else if("2".equals(payMode)){//状态成功
//			page = jmsgSmsSendService.findSendReportPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
//		}
//		if(page == null || page.getList() == null || page.getList().size() <=0){
//			addMessage(model, "查无此信息!");
//		}
		model.addAttribute("page", page);
		model.addAttribute("payMode", payMode);
		model.addAttribute("queryType", "detail");
		return "modules/sms/jmsgSmsSendResult";
	}**/
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "sendResult")
	public String sendResult(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.adminFlag()){
			
		}else{
			User user = UserUtils.getUser();
			jmsgSmsSend.setUser(user);
		}

//		String payMode = UserUtils.getPayMode(user.getId(), "sms");

		Page<JmsgSmsSend> page = null;
		
		//page = jmsgSmsSendService.findSendDetailPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
		page = jmsgSmsSendService.findSendDetailPageV2(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		
//		if("0".equals(payMode)){//提交扣费
//			page = jmsgSmsSendService.findSendDetailPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend); 
//		}else if("2".equals(payMode)){//状态成功
//			page = jmsgSmsSendService.findSendReportPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
//		}
		if(page == null || page.getList() == null || page.getList().size() <=0){
			addMessage(model, "查无此信息!");
		}
		model.addAttribute("page", page);
//		model.addAttribute("payMode", payMode);
		model.addAttribute("queryType", "detail");
		return "modules/sms/jmsgSmsSendResult";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "userPendingInit")
	public String userPendingInit(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("queryType", "pending");
		return "modules/sms/jmsgSmsSendResult";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "pendingList")
	public String pendingList(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		jmsgSmsSend.setUser(user);
		jmsgSmsSend.setSendStatus("P0");
		//Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
		Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPageV2(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		if(page.getList() == null || page.getList().size() <=0){
			addMessage(model, "查无此信息!");
		}
		model.addAttribute("page", page);
		model.addAttribute("queryType", "pending");
		return "modules/sms/jmsgSmsSendResult";
	}
	
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "detailInit")
	public String detailInit(){
		return "modules/sms/jmsgSmsSendDetail";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "errorDetailInit")
	public String errorDetailInit(){
		return "modules/sms/jmsgSmsSendErrorDetail";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "detailList")
	public String detailList(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
		//Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPageV2(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsSendDetail";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "errorDetailList")
	public String errorDetailList(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
		Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPageV2(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsSendErrorDetail";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "detailListReport")
	public String detailListReport(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
	        String fileName = "下行短信明细"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
	        jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
	        List<JmsgSmsSendReport> list = jmsgSmsSendService.findSendDetailReportV2(jmsgSmsSend);
			new ExportExcel("下行短信明细", JmsgSmsSendReport.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出下行短信明细失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sms/jmsgSmsSend/detailList";
	}	
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "detailView")
	public String detailView(String taskid,String bizid,Date createDatetime,String smsType,HttpServletRequest request, HttpServletResponse response, Model model) {
		String tableIndex = TableNameUtil.getTableIndex(bizid);
		JmsgSmsSend param = new JmsgSmsSend();
		//param.setTableName(getTableName(createDatetime));
		//param.setTableName("jmsg_sms_send_" + tableIndex);
		param.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndexNew(bizid));
		param.setId(bizid);
		JmsgSmsSend entity = jmsgSmsSendService.getV2(param);
		//JmsgSmsSend entity = jmsgSmsSendService.get(bizid);
		JmsgSmsSubmit jmsgSmsSubmit = new JmsgSmsSubmit();
		jmsgSmsSubmit.setBizid(bizid);
		jmsgSmsSubmit.setTaskid(taskid);
		//jmsgSmsSubmit.setTableName(getSubmitTableName(createDatetime));
		jmsgSmsSubmit.setTableName("jmsg_sms_submit_" + tableIndex);
		//List<JmsgSmsSubmit> submitList = jmsgSmsSubmitService.findList(jmsgSmsSubmit);
		List<JmsgSmsSubmit> submitList = jmsgSmsSubmitService.findListV2(jmsgSmsSubmit);
		JmsgSmsReport jmsgSmsReport = new JmsgSmsReport();
		jmsgSmsReport.setBizid(bizid);
		jmsgSmsReport.setTaskid(taskid);
		//jmsgSmsReport.setTableName(getReportTableName(createDatetime));
		jmsgSmsReport.setTableName("jmsg_sms_report_" + tableIndex);
		//List<JmsgSmsReport> reportList = jmsgSmsReportService.findList(jmsgSmsReport);
		List<JmsgSmsReport> reportList = jmsgSmsReportService.findListV2(jmsgSmsReport);
		JmsgSmsPush jmsgSmsPush = new JmsgSmsPush();
		jmsgSmsPush.setId(bizid);
		//jmsgSmsPush.setTableName(getPushTableName(createDatetime));
		jmsgSmsPush.setTableName("jmsg_sms_push_" + tableIndex);
		//List<JmsgSmsPush> pushList = jmsgSmsSendService.queryJmsgSmsPush(bizid);
		List<JmsgSmsPush> pushList = jmsgSmsSendService.queryJmsgSmsPushV2(jmsgSmsPush);
		
		JmsgSmsMmsdown downEntity = null;
		if("3".equals(smsType)){
			JmsgSmsMmsdown downParam = new JmsgSmsMmsdown();
			downParam.setTableName("jmsg_sms_mmsdown_"+tableIndex);
			downParam.setId(bizid);
			downEntity = jmsgSmsMmsdownService.get(downParam);
		}
		
		model.addAttribute("entity", entity);
		model.addAttribute("submitList", submitList);
		model.addAttribute("reportList", reportList);
		model.addAttribute("pushList", pushList);
		model.addAttribute("smsType", smsType);
		if(downEntity != null){
			model.addAttribute("mmsDownList", downEntity);
		}
		return "modules/sms/jmsgSmsSendDetailView";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "historyDetailInit")
	public String historyDetailInit(){
		return "modules/sms/jmsgSmsSendHistoryDetail";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "historyDetailList")
	public String historyDetailList(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
		//Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		Page<JmsgSmsSend> page = jmsgSmsSendService.findSendDetailPageV2(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsSendHistoryDetail";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsSend> page = jmsgSmsSendService.findPage(new Page<JmsgSmsSend>(request, response), jmsgSmsSend); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsSendList";
	}

	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsSend jmsgSmsSend, Model model) {
		model.addAttribute("jmsgSmsSend", jmsgSmsSend);
		return "modules/sms/jmsgSmsSendForm";
	}

	@RequiresPermissions("sms:jmsgSmsSend:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsSend jmsgSmsSend, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsSend)){
			return form(jmsgSmsSend, model);
		}
		jmsgSmsSendService.save(jmsgSmsSend);
		addMessage(redirectAttributes, "保存短信发送成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsSend/list/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsSend jmsgSmsSend, RedirectAttributes redirectAttributes) {
		jmsgSmsSendService.delete(jmsgSmsSend);
		addMessage(redirectAttributes, "删除短信发送成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsSend/list/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "userDetailListReport")
	public String userDetailListReport(JmsgSmsSend jmsgSmsSend, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
	        String fileName = "已发短信"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
	        jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
	        List<JmsgSmsSendUserReport> list = jmsgSmsSendService.queryJmsgSmsSendUserReportV2(jmsgSmsSend);
			new ExportExcel("已发短信", JmsgSmsSendUserReport.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出已发短信失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sms/jmsgSmsSend/sendResult";
	}	
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "batchPush")
	public String batchPush(JmsgSmsSend jmsgSmsSend, String ids, HttpServletRequest request, HttpServletResponse response)
	{
		StringBuilder result = new StringBuilder();
		int successNo = 0;
		int failNo = 0;
		String[] array = ids.split(";"); 
        
        for (String param : array) 
        {
        	jmsgSmsSend = this.get(param);
        	
        	if (jmsgSmsSendService.push(jmsgSmsSend))
        	{
        		successNo ++;
        	}
        	else
        	{
        		failNo ++;
        	}
        }
        result.append("推送 ：" + array.length + " 条，成功 ：" + successNo + " 条，失败：" + failNo + " 条。");
		return renderString(response, result.toString());
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "taskPush")
	public String taskPush(JmsgSmsSend jmsgSmsSend, String taskName,HttpServletRequest request, HttpServletResponse response)
	{
		jmsgSmsSend.setTableName(getTableName(jmsgSmsSend.getCreateDatetimeQ()));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tableName", jmsgSmsSend.getTableName());
		param.put("phone", jmsgSmsSend.getPhone());
		param.put("taskId", jmsgSmsSend.getTaskId());
		param.put("id", jmsgSmsSend.getId());
		param.put("userId", jmsgSmsSend.getUser().getId());
		param.put("phoneType", jmsgSmsSend.getPhoneType());
		param.put("sendStatus", jmsgSmsSend.getSendStatus());
		param.put("reportStatus", jmsgSmsSend.getReportStatus());
		param.put("channelCode", jmsgSmsSend.getChannelCode());
		param.put("createDatetimeQ", jmsgSmsSend.getCreateDatetimeQ());
		param.put("createDatetimeZ", jmsgSmsSend.getCreateDatetimeZ());
		param.put("pushFlag", jmsgSmsSend.getPushFlag());
		
		if (null != jmsgSmsSend.getCreateDatetimeQ())
		{
			param.put("createDatetimeQ", DateUtils.formatDate(jmsgSmsSend.getCreateDatetimeQ(), "yyyy-MM-dd HH:mm:ss"));
		}
		if (null != jmsgSmsSend.getCreateDatetimeZ())
		{
			param.put("createDatetimeZ", DateUtils.formatDate(jmsgSmsSend.getCreateDatetimeZ(), "yyyy-MM-dd HH:mm:ss"));
		}
		
		try {
			taskName = URLDecoder.decode(taskName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		JmsgCustomTask customTask = new JmsgCustomTask();
		customTask.setTaskName(taskName);
		customTask.setType("1");
		customTask.setParamJson(JSON.toJSONString(param));
		customTask.setExecuteClass("com.siloyou.jmsg.modules.sms.task.impl.SmsCustomTaskPushExecutor");
		customTask.setStatus("1");
		customTask.setVersion("1");
		
		jmsgCustomTaskService.save(customTask);
		
		return renderString(response, "任务推送已创建！");
	}
	
	/**
	 * 根据查询时间获取表名
	 * @param createDatetimeQ
	 * @return
	 */
	private String getTableName(Date createDatetimeQ)
	{
		String tableName = null;
		if (null == createDatetimeQ) {
			createDatetimeQ = new Date();
		}
		Calendar current = Calendar.getInstance();

		Calendar today = Calendar.getInstance(); // 今天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		Calendar yesterday = Calendar.getInstance();    //3天前  
        
        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));  
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));  
        yesterday.set(Calendar.DAY_OF_MONTH, yesterday.get(Calendar.DAY_OF_MONTH)-3);  
        yesterday.set( Calendar.HOUR_OF_DAY, 0);  
        yesterday.set( Calendar.MINUTE, 0);  
        yesterday.set(Calendar.SECOND, 0);  
        yesterday.set(Calendar.MILLISECOND, 0);

		current.setTime(createDatetimeQ);
		
		String monthStr = "";
		int month = current.get(Calendar.MONTH) + 1;
		if (month < 10)
		{
			monthStr = "0" + month;
		}
		else
		{
			monthStr = "" + month;
		}

		if (current.after(today) || current.compareTo(today) == 0) {
			tableName = "jmsg_sms_send_" + current.get(Calendar.DAY_OF_MONTH);
		} else if (current.before(today) && (current.after(yesterday) || current.compareTo(yesterday) == 0)) {

			tableName = "jmsg_sms_send_" + current.get(Calendar.DAY_OF_MONTH);
			//tableName = "jmsg_sms_send_" + monthStr + current.get(Calendar.DAY_OF_MONTH);
		} else {
			tableName = "jmsg_sms_send_history_" + current.get(Calendar.YEAR) + monthStr;
		}

		return tableName;
	}
	
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "testGateway")
	public String testGateway(String userId,String phone,String smsContent, HttpServletRequest request, HttpServletResponse response){
		try{
			smsContent = URLDecoder.decode(smsContent, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		jmsgSmsSendService.testGateway(userId, phone, smsContent);
		
		return renderString(response, "ok");
	}
	
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "gatewayResult")
	public String gatewayResult(HttpServletRequest request, HttpServletResponse response){
		String msg = "";
		Map<String, String> map = JedisClusterUtils.getJedisInstance().hgetAll("testGateway");
		if(map != null && map.size() >0){
			for (Map.Entry<String, String> entry: map.entrySet()) {
				msg =msg+"应用ID："+entry.getKey()+"<br/>"+entry.getValue();
			}
		}
		return renderString(response, msg);
	}


	/**
	* @Description: 短信下行明细查询
	* @param:
	* @return:
	* @author: yuyunfeng
	* @Date: 2019/1/23
	*/
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "index")
	public String index(HttpServletRequest request, HttpServletResponse response){
		return "modules/sms/jmsgSmsSendIndex";
	}

	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "search")
	public String search(JmsgSmsSend jmsgSmsSend,HttpServletRequest request, HttpServletResponse response, Model model){
		if(!",".equals(jmsgSmsSend.getUser().getId())&&!"".equals(jmsgSmsSend.getUser().getId())){
			jmsgSmsSend.getUser().setId(jmsgSmsSend.getUser().getId().replaceAll(",",""));
		}else {
			jmsgSmsSend.getUser().setId(null);
		}

		if(!",".equals(jmsgSmsSend.getUserId())&&!"".equals(jmsgSmsSend.getUserId())){
			jmsgSmsSend.setUserId(jmsgSmsSend.getUserId().replaceAll(",",""));
		}else {
			jmsgSmsSend.setUserId(null);
		}


        Page<JmsgSmsSend> page;
		if(DateUtils.isSameDay(jmsgSmsSend.getCreateDatetimeQ(),jmsgSmsSend.getCreateDatetimeZ())){
            //如果时间再同一天就不进行分表查询
            jmsgSmsSend.setTableName("jmsg_sms_send_"+DateUtils.getDay(jmsgSmsSend.getCreateDatetimeQ()).replaceFirst("^0*", ""));
		    page = jmsgSmsSendService.getSmsListByTime(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
        }else {
            page = jmsgSmsSendService.getSmsList(new Page<JmsgSmsSend>(request, response), jmsgSmsSend);
        }
        if(page.getList().size()<1){
            addMessage(model, "查无此信息!");
        }
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsSendIndex";
	}

	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "searchDetail" ,method = RequestMethod.GET)
	public String searchDetail(HttpServletRequest request, HttpServletResponse response, Model model){
		JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
		JmsgSmsReport jmsgSmsReport = new JmsgSmsReport();
		jmsgSmsReport.setBizid(request.getParameter("bizid"));
		jmsgSmsSend.setTaskId(request.getParameter("task_id"));
        jmsgSmsSend.setPhone(request.getParameter("phone_number"));
		model.addAttribute("jmsgSmsSend",jmsgSmsSendService.getSmsById(jmsgSmsSend));
        JmsgSmsReport jmsgSmsReport1;
		if("-1".equals(request.getParameter("send_status"))){
            jmsgSmsReport1 = new JmsgSmsReport();
        }else {
             jmsgSmsReport1 = jmsgSmsReportService.findByBizid(jmsgSmsReport);
        }
		model.addAttribute("jmsgSmsReport",jmsgSmsReport1);
		return "modules/sms/jmsgSmsSendDetails";
	}

	/**
	* @Description: 短信明细发送下载任务
	* @param:
	* @return:
	* @author: yuyunfeng
	* @Date: 2019/1/30
	*/
	@RequiresPermissions("sms:jmsgSmsSend:view")
	@RequestMapping(value = "sendTask")
	public String sendTask(JmsgSmsSend jmsgSmsSend,HttpServletRequest request, HttpServletResponse response, Model model){
		String taskName = request.getParameter("task_name");
		jmsgSmsSend.setPage(null);
		JmsgCustomTask jmsgCustomTask = new JmsgCustomTask();
		jmsgCustomTask.setDayQ(new Date());
		jmsgCustomTask.setTaskName(taskName);
		jmsgCustomTask.setType("6");
		jmsgCustomTask.setStatus("1");
		jmsgCustomTask.setExecuteClass("com.siloyou.jmsg.modules.sms.task.impl.JmsgSearchSendListTask");
		String jsonString = JSON.toJSONString(jmsgSmsSend);
		System.out.println(jsonString);
		jmsgCustomTask.setParamJson(jsonString);
		jmsgCustomTaskService.save(jmsgCustomTask);
		addMessage(model, "短信明细导出任务已创建！");
		return "modules/sms/jmsgSmsSendIndex";
	}

	/**
	* @Description: 下载短信明细
	* @param:
	* @return:
	* @author: yuyunfeng
	* @Date: 2019/1/30
	*/
	@RequestMapping(value = "download")
	public void downloadDetail(String path,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		try{
			File file = new File(new String(Base64Util.decode(path), "UTF-8"));
			InputStream fis = new BufferedInputStream(new FileInputStream(new String(Base64Util.decode(path), "UTF-8")));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			response.reset();

			response.addHeader("Content-Type", "text/html; charset=utf-8");
			String downLoadName = new String("短信下行记录.xls".getBytes("gbk"), "iso8859-1");
			response.addHeader("Content-Disposition", "attachment;filename="+downLoadName);
			//response.setHeader("Content-Disposition","attachment; filename=tongji.txt"); //设置返回的文件类型
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream()); // 得到向客户端输出二进制数据的对象
			//response.setContentType("text/plain"); // 设置返回的文件类型
			response.setContentType("octets/stream");
			toClient.write(buffer); // 输出数据
			toClient.flush();
			toClient.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}