/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import com.google.common.collect.Maps;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgCustomTask;
import com.siloyou.jmsg.modules.sms.entity.JmsgReportStatusTask;
import com.siloyou.jmsg.modules.sms.service.JmsgCustomTaskService;

/**
 * 自定义任务Controller
 * @author zj
 * @version 2017-04-07
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgCustomTask")
public class JmsgCustomTaskController extends BaseController {

	@Autowired
	private JmsgCustomTaskService jmsgCustomTaskService;
	
	@ModelAttribute
	public JmsgCustomTask get(@RequestParam(required=false) String id) {
		JmsgCustomTask entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgCustomTaskService.get(id);
		}
		if (entity == null){
			entity = new JmsgCustomTask();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgCustomTask:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgCustomTask jmsgCustomTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgCustomTask> page = jmsgCustomTaskService.findPage(new Page<JmsgCustomTask>(request, response), jmsgCustomTask); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgCustomTaskList";
	}

	@RequiresPermissions("sms:jmsgCustomTask:view")
	@RequestMapping(value = "form")
	public String form(JmsgCustomTask jmsgCustomTask, Model model) {
		model.addAttribute("jmsgCustomTask", jmsgCustomTask);
		return "modules/sms/jmsgCustomTaskForm";
	}

	@RequiresPermissions("sms:jmsgCustomTask:edit")
	@RequestMapping(value = "save")
	public String save(JmsgCustomTask jmsgCustomTask, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgCustomTask)){
			return form(jmsgCustomTask, model);
		}
		jmsgCustomTaskService.save(jmsgCustomTask);
		addMessage(redirectAttributes, "保存自定义任务成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgCustomTask/?repage";
	}
	
	@RequiresPermissions("sms:jmsgCustomTask:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgCustomTask jmsgCustomTask, RedirectAttributes redirectAttributes) {
		jmsgCustomTaskService.delete(jmsgCustomTask);
		addMessage(redirectAttributes, "删除自定义任务成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgCustomTask/?repage";
	}
	
	@RequiresPermissions("sms:jmsgCustomTask:view")
	@RequestMapping(value = "provinceReportIndex")
	public String provinceReportIndex() {
		return "modules/sms/jmsgProvinceReport";
	}
	
	@RequiresPermissions("sms:jmsgCustomTask:view")
	@RequestMapping(value = "modifyReportStatusInit")
	public String modifyReportStatusInit(JmsgReportStatusTask jmsgReportStatusTask, Model model) {
		jmsgReportStatusTask.setNullType("1");
		model.addAttribute("jmsgReportStatusTask", jmsgReportStatusTask);
		return "modules/sms/jmsgModifyReportStatus";
	}
	
	@RequiresPermissions("sms:jmsgCustomTask:view")
	@RequestMapping(value = "createReportStatus")
	public String createReportStatus(JmsgReportStatusTask jmsgReportStatusTask, RedirectAttributes redirectAttributes) {
		String msg = "创建任务成功";
		long indexQ = DateUtils.pastDays(DateUtils.parseDate(DateUtils.formatDate(jmsgReportStatusTask.getDayQ(), "yyyy-MM-dd")));
		boolean runFlag = true;
		if(indexQ > 3){
			runFlag = false;
			msg ="时间只能选择3天内的";
		}
		if(runFlag){
			jmsgReportStatusTask.setPageNo(0);
			jmsgReportStatusTask.setPageSize(500);
			String paramJson = JsonMapper.toJsonString(jmsgReportStatusTask);
			String type = "3";
			String status = "1";
			String executeClass= "com.siloyou.jmsg.modules.sms.task.impl.JmsgModifyReportStatusTask";
			JmsgCustomTask param = new JmsgCustomTask();
			param.setTaskName(jmsgReportStatusTask.getTaskName());
			param.setType(type);
			param.setParamJson(paramJson);
			param.setExecuteClass(executeClass);
			param.setStatus(status);
			param.setVersion("1");
			jmsgCustomTaskService.save(param);
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgCustomTask/modifyReportStatusInit";
	}
	
	//创建分省统计
	@RequestMapping(value = "createPovinceReport")
	public String createPovinceReport(JmsgCustomTask jmsgCustomTask, String gatewayId,RedirectAttributes redirectAttributes){
		String msg = "";
		long indexQ = DateUtils.pastDays(jmsgCustomTask.getDayQ());
		long indexZ = DateUtils.pastDays(jmsgCustomTask.getDayZ());
		boolean runFlag = true;
		if(indexQ > 3){
			if(indexZ <=3){
				runFlag = false;
				msg ="开始时间选择3天前的，则结束时间不能选择3天内的日期。";
			}
		}
		if(runFlag){//保存任务信息
			
			String tableName = "";
			if(indexQ <= 3){
				tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(jmsgCustomTask.getDayQ());
			}else{
				tableName = "jmsg_sms_send_history_"+DateUtils.formatDate(jmsgCustomTask.getDayQ(), "yyyyMM");
			}
			
			
			Map<String,Object> map = Maps.newHashMap();
			map.put("dayQ",  DateUtils.formatDate(jmsgCustomTask.getDayQ()));
			map.put("dayZ", DateUtils.formatDate(jmsgCustomTask.getDayZ()));
			map.put("tableName", tableName);
			if(StringUtils.isNotBlank(gatewayId)){
				map.put("gatewayId", gatewayId);
			}
			String paramJson = JsonMapper.toJsonString(map);
			String type = "2";
			String status = "1";
			//String taskName="通道分省统计";
			String executeClass= "com.siloyou.jmsg.modules.sms.task.impl.JmsgProvinceReportTask";
			JmsgCustomTask param = new JmsgCustomTask();
			param.setTaskName(jmsgCustomTask.getTaskName());
			param.setType(type);
			param.setParamJson(paramJson);
			param.setExecuteClass(executeClass);
			param.setStatus(status);
			param.setVersion("1");
			jmsgCustomTaskService.save(param);
			msg = "生成任务成功";
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgCustomTask/provinceReportIndex";
	}
	
	@RequestMapping(value = "download")
	public void downloadDetail(String path,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		try{
			File file = new File(path);
			InputStream fis = new BufferedInputStream(new FileInputStream(path));  
	        byte[] buffer = new byte[fis.available()];  
	        fis.read(buffer);  
	        fis.close();
	        response.reset();
	        
	        response.addHeader("Content-Type", "text/html; charset=utf-8");  
	        String downLoadName = new String("通道分省统计.txt".getBytes("gbk"), "iso8859-1");  
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