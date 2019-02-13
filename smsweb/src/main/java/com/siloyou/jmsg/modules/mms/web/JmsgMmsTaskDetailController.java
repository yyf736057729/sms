/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsReport;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsSubmit;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTaskDetail;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsReportService;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsSubmitService;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsTaskDetailService;

/**
 * 彩信发送明细Controller
 * @author zhukc
 * @version 2016-05-20
 */
@Controller
@RequestMapping(value = "${adminPath}/mms/jmsgMmsTaskDetail")
public class JmsgMmsTaskDetailController extends BaseController {

	@Autowired
	private JmsgMmsTaskDetailService jmsgMmsTaskDetailService;
	
	@Autowired
	private JmsgMmsSubmitService jmsgMmsSubmitService;
	
	@Autowired
	private JmsgMmsReportService jmsgMmsReportService;
	
	@ModelAttribute
	public JmsgMmsTaskDetail get(@RequestParam(required=false) String id) {
		JmsgMmsTaskDetail entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgMmsTaskDetailService.get(id);
		}
		if (entity == null){
			entity = new JmsgMmsTaskDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("mms:jmsgMmsTaskDetail:view")
	@RequestMapping(value = "init")
	public String init(){
		return "modules/mms/jmsgMmsTaskDetailList";
	}
	
	@RequiresPermissions("mms:jmsgMmsTaskDetail:view")
	@RequestMapping(value = "list")
	public String list(String taskId,String phone,Model model) {
	
		JmsgMmsTaskDetail param = new JmsgMmsTaskDetail();
		param.setTaskId(taskId);
		param.setPhone(phone);
		JmsgMmsTaskDetail entity = jmsgMmsTaskDetailService.getByTaskIdAndPhone(param);
		
		if(entity !=null){
			if(PowerUtils.adminFlag()){
				
			}else if(PowerUtils.agencyFlag()){
				if(!StringUtils.equals(UserUtils.get(entity.getCreateUserId()).getCompany().getId(), UserUtils.getUser().getCompany().getId())){
					entity = null;
				}
				
			}else{
				if(!StringUtils.equals(entity.getCreateUserId(), UserUtils.getUser().getId())){
					entity = null;
				}
			}
		}
		
		if(entity != null){
			String result = "状态未知";
			if("T0".equals(entity.getSendStatus())){
				String payMode = entity.getPayMode();
				
				if("1".equals(payMode)){//网关
					JmsgMmsSubmit jmsgMmsSubmit = jmsgMmsSubmitService.getByBizid(entity.getId());
					if(jmsgMmsSubmit != null){
						if("0".equals(jmsgMmsSubmit.getResult())){
							result = "成功";
						}else{
							result = "失败";
						}
					}
				}else if("2".equals(payMode)){//状态
					JmsgMmsReport jmsgMmsReport = jmsgMmsReportService.getByBizid(entity.getId());
					if(jmsgMmsReport != null){
						if("DELIVRD".equals(jmsgMmsReport.getStat())){
							result = "成功";
						}else{
							result = "失败";
						}
					}
				}else if("3".equals(payMode)){//下载
					if(entity.getReceiveDatetime() != null){
						result = "成功";
					}
				}
			}else{
				result = DictUtils.getDictLabel(entity.getSendStatus(), "mms_send_status", entity.getSendStatus());
			}
			entity.setSendResult(result);
		}else{
			addMessage(model, "查无此信息!");
		}
		
		model.addAttribute("jmsgMmsTaskDetail", entity);
		model.addAttribute("taskId", taskId);
		model.addAttribute("phone", phone);
			
		return "modules/mms/jmsgMmsTaskDetailList";
	}
	
//	@RequiresPermissions("mms:jmsgMmsTaskDetail:view")
//	@RequestMapping(value = {"list", ""})
//	public String list(JmsgMmsTaskDetail jmsgMmsTaskDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
//		if(PowerUtils.adminFlag()){
//			if(jmsgMmsTaskDetail.getUser() !=null){
//				jmsgMmsTaskDetail.setCreateUserId(jmsgMmsTaskDetail.getUser().getId());
//			}
//		}else{
//			jmsgMmsTaskDetail.setCreateUserId(UserUtils.getUser().getId());
//		}
//		
//		Page<JmsgMmsTaskDetail> page = jmsgMmsTaskDetailService.findPage(new Page<JmsgMmsTaskDetail>(request, response), jmsgMmsTaskDetail); 
//		model.addAttribute("page", page);
//		return "modules/mms/jmsgMmsTaskDetailList";
//	}
	
	@RequiresPermissions("mms:jmsgMmsTaskDetail:view")
	@RequestMapping(value = "listStatus")
	public String listStatus(String taskId,String phone,Model model) {
		
		JmsgMmsTaskDetail param = new JmsgMmsTaskDetail();
		param.setTaskId(taskId);
		param.setPhone(phone);
		
		JmsgMmsTaskDetail entity = jmsgMmsTaskDetailService.getByTaskIdAndPhone(param);
		JmsgMmsSubmit jmsgMmsSubmit = null;
		JmsgMmsReport jmsgMmsReport = null;
		if(entity != null){
			jmsgMmsSubmit = jmsgMmsSubmitService.getByBizid(entity.getId());
//			if(jmsgMmsSubmit != null){
			jmsgMmsReport = jmsgMmsReportService.getByBizid(entity.getId());
//			}
		}else{
			addMessage(model, "查无此彩信信息!");
		}
		
		model.addAttribute("jmsgMmsTaskDetail", entity);
		model.addAttribute("jmsgMmsSubmit", jmsgMmsSubmit);
		model.addAttribute("jmsgMmsReport", jmsgMmsReport);
		model.addAttribute("taskId", taskId);
		model.addAttribute("phone", phone);
		return "modules/mms/jmsgMmsTaskStatus";
	}
	
	@RequiresPermissions("mms:jmsgMmsTaskDetail:view")
	@RequestMapping(value = "listStatusInit")
	public String listStatusInit(JmsgMmsTaskDetail jmsgMmsTaskDetail, Model model){
		model.addAttribute("jmsgMmsTaskDetail", jmsgMmsTaskDetail);
		return "modules/mms/jmsgMmsTaskStatus";
	}

	@RequiresPermissions("mms:jmsgMmsTaskDetail:view")
	@RequestMapping(value = "form")
	public String form(JmsgMmsTaskDetail jmsgMmsTaskDetail, Model model) {
		model.addAttribute("jmsgMmsTaskDetail", jmsgMmsTaskDetail);
		return "modules/mms/jmsgMmsTaskDetailForm";
	}

	@RequiresPermissions("mms:jmsgMmsTaskDetail:edit")
	@RequestMapping(value = "save")
	public String save(JmsgMmsTaskDetail jmsgMmsTaskDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgMmsTaskDetail)){
			return form(jmsgMmsTaskDetail, model);
		}
		jmsgMmsTaskDetailService.save(jmsgMmsTaskDetail);
		addMessage(redirectAttributes, "保存彩信发送明细成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsTaskDetail/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsTaskDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgMmsTaskDetail jmsgMmsTaskDetail, RedirectAttributes redirectAttributes) {
		jmsgMmsTaskDetailService.delete(jmsgMmsTaskDetail);
		addMessage(redirectAttributes, "删除彩信发送明细成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsTaskDetail/?repage";
	}

}