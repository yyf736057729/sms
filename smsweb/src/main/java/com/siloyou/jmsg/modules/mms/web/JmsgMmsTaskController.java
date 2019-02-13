/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsTaskDetailService;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsTaskService;

/**
 * 彩信发送管理Controller
 * @author zhukc
 * @version 2016-05-20
 */
@Controller
@RequestMapping(value = "${adminPath}/mms/jmsgMmsTask")
public class JmsgMmsTaskController extends BaseController {

	@Autowired
	private JmsgMmsTaskService jmsgMmsTaskService;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgMmsTaskDetailService jmsgMmsTaskDetailService;
	
	@ModelAttribute
	public JmsgMmsTask get(@RequestParam(required=false) String id) {
		JmsgMmsTask entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgMmsTaskService.get(id);
		}
		if (entity == null){
			entity = new JmsgMmsTask();
		}
		return entity;
	}
	
	@RequiresPermissions("mms:jmsgMmsTask:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgMmsTask jmsgMmsTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.adminFlag()){//管理员

		}else if(PowerUtils.agencyFlag()){
			jmsgMmsTask.setCompanyId(UserUtils.getUser().getCompany().getId());//公司ID
		}else{
			jmsgMmsTask.setCreateUserId(UserUtils.getUser().getId());
		}
		
		if(jmsgMmsTask.getUser() !=null){
			jmsgMmsTask.setCreateUserId(jmsgMmsTask.getUser().getId());
		}
		
		Page<JmsgMmsTask> page = jmsgMmsTaskService.findPage(new Page<JmsgMmsTask>(request, response), jmsgMmsTask); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsTaskList";
	}
	
	@RequiresPermissions("mms:jmsgMmsTask:view")
	@RequestMapping(value = "mmsSendTongji")
	public String mmsSendTongji(JmsgMmsTask jmsgMmsTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.adminFlag()){//管理员

		}else if(PowerUtils.agencyFlag()){
			jmsgMmsTask.setCompany(UserUtils.getUser().getCompany());//公司ID
		}else{
			jmsgMmsTask.setCreateUserId(UserUtils.getUser().getId());
		}
		
		if(jmsgMmsTask.getUser() !=null){
			jmsgMmsTask.setCreateUserId(jmsgMmsTask.getUser().getId());
		}
		
		Page<JmsgMmsTask> page = jmsgMmsTaskService.findMmsSendTongjiPage(new Page<JmsgMmsTask>(request, response), jmsgMmsTask); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsSendReportList";
	}
	
	//消费明细清单
	@RequiresPermissions("mms:jmsgMmsTask:view")
	@RequestMapping(value = "xiaofeiDetailList")	
	public String xiaofeiDetailList(JmsgMmsTask jmsgMmsTask, HttpServletRequest request, HttpServletResponse response, Model model){
		if(PowerUtils.adminFlag()){//管理员

		}else if(PowerUtils.agencyFlag()){//代理商
			jmsgMmsTask.setCompany(UserUtils.getUser().getCompany());//公司ID
		}else{//普通用户
			jmsgMmsTask.setCreateUserId(UserUtils.getUser().getId());
		}
		
		if(jmsgMmsTask.getUser() !=null){
			jmsgMmsTask.setCreateUserId(jmsgMmsTask.getUser().getId());
		}
		
		Page<JmsgMmsTask> page = jmsgMmsTaskService.findXFDetailListPage(new Page<JmsgMmsTask>(request, response), jmsgMmsTask); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgXFDetailList";
	}

	@RequiresPermissions("mms:jmsgMmsTask:view")
	@RequestMapping(value = "form")
	public String form(JmsgMmsTask jmsgMmsTask, Model model) {
		model.addAttribute("jmsgMmsTask", jmsgMmsTask);
		return "modules/mms/jmsgMmsTaskForm";
	}

	@RequiresPermissions("mms:jmsgMmsTask:edit")
	@RequestMapping(value = "save")
	public String save(JmsgMmsTask jmsgMmsTask, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgMmsTask)){
			return form(jmsgMmsTask, model);
		}
		jmsgMmsTaskService.save(jmsgMmsTask);
		addMessage(redirectAttributes, "保存彩信发送管理成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsTask/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsTask:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgMmsTask jmsgMmsTask, RedirectAttributes redirectAttributes) {
		jmsgMmsTaskService.delete(jmsgMmsTask);
		addMessage(redirectAttributes, "删除彩信发送管理成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsTask/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value = "sendOneInit")
	public String sendOneInit(JmsgMmsTask jmsgMmsTask, Model model) {
		model.addAttribute("jmsgMmsTask", jmsgMmsTask);
		return "modules/mms/jmsgMmsDataSendOne";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value = "phoneView")
	public String phoneView(String taskId, Model model) {
		List<String> list = jmsgMmsTaskDetailService.findPhone(taskId);
		StringBuffer result = new StringBuffer();
		for (String phone : list) {
			result = result.append(phone);
		}
		
//		String phone = jmsgMmsTaskDetailService.findPhone(taskId);
		model.addAttribute("phone", result.toString());
		return "modules/mms/jmsgMmsTaskPhoneView";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:edit")
	@RequestMapping(value = "sendOne")
	public String sendOne(JmsgMmsTask jmsgMmsTask, Model model, RedirectAttributes redirectAttributes) {
		String msg = "发送成功!";
		Set<String> phoneList = new HashSet<String>();
		phoneList.add(jmsgMmsTask.getPhone());
		Long money = jmsgAccountService.findUserMoeny(UserUtils.getUser().getId(), "mms");
		if(money >= 1){
			jmsgMmsTaskService.insertTask(jmsgMmsTask.getMmsId(),phoneList,1,jmsgMmsTask.getSendDatetime());
		}else{
			msg = "账号余额不足";
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsData/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value = "sendMoreInit")
	public String sendMoreInit(JmsgMmsTask jmsgMmsTask, Model model) {
		model.addAttribute("jmsgMmsTask", jmsgMmsTask);
		return "modules/mms/jmsgMmsDataSendMore";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:edit")
	@RequestMapping(value = "updateStatus")
	public String updateStatus(JmsgMmsTask jmsgMmsTask, Model model, RedirectAttributes redirectAttributes){
		String msg = "";
		boolean flag = true;
		String status = jmsgMmsTask.getStatus();
		JmsgMmsTask entity = jmsgMmsTaskService.get(jmsgMmsTask.getId());
		
		if("3".equals(entity.getStatus())){
			flag = false;
			msg = "彩信已经发送完成!";
		}else if("9".equals(entity.getStatus())){
			flag = false;
			msg = "已经停止发送彩信";
		}else{
			if("9".equals(status)){
				msg = "停止发送彩信成功!";
			}else if("5".equals(status)){
				msg = "暂停发送彩信成功!";
			}else if("1".equals(status)){
				msg = "继续发送彩信成功!";
			}
		}
			
		if(flag)jmsgMmsTaskService.updateStatus(jmsgMmsTask);
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsTask/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:edit")
	@RequestMapping(value = "sendMore")
	public String sendMore(MultipartFile phoneFile, JmsgMmsTask jmsgMmsTask, Model model, RedirectAttributes redirectAttributes) {
		long startTime = System.currentTimeMillis();
		Set<String> phoneList = new HashSet<String>();
		String msg = "发送成功!";
		if (StringUtils.isNotBlank(phoneFile.getName())) {
			try {
				InputStreamReader reader = new InputStreamReader(phoneFile.getInputStream());
				BufferedReader br = new BufferedReader(reader);
				int count = 0;
				for(String line = br.readLine();line !=null;line=br.readLine()){
					if(StringUtils.isNotBlank(line)){
						count++;
						phoneList.add(StringUtils.trim(line));
					}
				}
				if(phoneList.size() >0){
					Long money = jmsgAccountService.findUserMoeny(UserUtils.getUser().getId(), "mms");
					if(money >= phoneList.size()){
						msg = jmsgMmsTaskService.insertTask(jmsgMmsTask.getMmsId(),phoneList,count,jmsgMmsTask.getSendDatetime());
					}else{
						msg = "账号余额不足";
					}
				}else{
					msg = "号码文件为空不符合要求";
				}
			}catch(Exception e){
				msg = "号码文件格式不符合要求";
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("endTime:"+(endTime - startTime)/1000);
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsData/?repage";
	}

	@RequestMapping(value = "downloadDetail")
	public void downloadDetail(String taskId,String userId,String createTime,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		String path = Global.getConfig("mms.detail.send.path")+"/"+createTime+"/"+taskId+".txt";
		try{
			File file = new File(path);
			InputStream fis = new BufferedInputStream(new FileInputStream(path));  
	        byte[] buffer = new byte[fis.available()];  
	        fis.read(buffer);  
	        fis.close();
	        response.reset();  
	        response.setHeader("Content-Disposition","attachment; filename=" + taskId + ".txt"); //设置返回的文件类型  
	        response.addHeader("Content-Length", "" + file.length());
	        OutputStream toClient = new BufferedOutputStream(response.getOutputStream()); // 得到向客户端输出二进制数据的对象  
	        response.setContentType("text/plain"); // 设置返回的文件类型  
	        toClient.write(buffer); // 输出数据  
	        toClient.flush();  
	        toClient.close();   
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }
	}

}