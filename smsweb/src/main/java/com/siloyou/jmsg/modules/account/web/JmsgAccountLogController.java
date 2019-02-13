/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.web;

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
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.account.entity.JmsgAccountLog;
import com.siloyou.jmsg.modules.account.service.JmsgAccountLogService;

/**
 * 资金变动日志Controller
 * @author zhukc
 * @version 2016-05-17
 */
@Controller
@RequestMapping(value = "${adminPath}/account/jmsgAccountLog")
public class JmsgAccountLogController extends BaseController {

	@Autowired
	private JmsgAccountLogService jmsgAccountLogService;
	
	@ModelAttribute
	public JmsgAccountLog get(@RequestParam(required=false) String id) {
		JmsgAccountLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgAccountLogService.get(id);
		}
		if (entity == null){
			entity = new JmsgAccountLog();
		}
		return entity;
	}
	
	@RequiresPermissions("account:jmsgAccountLog:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgAccountLog jmsgAccountLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!PowerUtils.adminFlag()){//不是管理员权限
			jmsgAccountLog.setUser(UserUtils.getUser());
		}
		Page<JmsgAccountLog> page = jmsgAccountLogService.findPage(new Page<JmsgAccountLog>(request, response), jmsgAccountLog); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountLogList";
	}
	
	@RequiresPermissions("account:jmsgAccountLog:view")
	@RequestMapping(value = "detailedListAgency")
	public String detailedListAgency(JmsgAccountLog jmsgAccountLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.adminFlag()){
			
		}else if(PowerUtils.agencyFlag()){//代理商权限
			jmsgAccountLog.setCompanyId(UserUtils.getUser().getCompany().getId());
		}else{
			jmsgAccountLog.setUser(UserUtils.getUser());
		}
		Page<JmsgAccountLog> page = jmsgAccountLogService.findDetailPage(new Page<JmsgAccountLog>(request, response), jmsgAccountLog); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountDetailAgency";
	}
	
	@RequiresPermissions("account:jmsgAccountLog:view")
	@RequestMapping(value = "detailedList")
	public String detailedList(JmsgAccountLog jmsgAccountLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgAccountLog> page = jmsgAccountLogService.findDetailPage(new Page<JmsgAccountLog>(request, response), jmsgAccountLog); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountDetail";
	}
	
	@RequiresPermissions("account:jmsgAccountLog:view")
	@RequestMapping(value = "mmsList")
	public String mmsList(JmsgAccountLog jmsgAccountLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!PowerUtils.agencyFlag()){//不是代理权限
			jmsgAccountLog.setUser(UserUtils.getUser());
		}else{
			jmsgAccountLog.setCompanyId(UserUtils.getUser().getCompany().getId());//公司ID
		}
		jmsgAccountLog.setAppType("mms");
		Page<JmsgAccountLog> page = jmsgAccountLogService.findPage(new Page<JmsgAccountLog>(request, response), jmsgAccountLog); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountLogMmsList";
	}	

	@RequiresPermissions("account:jmsgAccountLog:view")
	@RequestMapping(value = "form")
	public String form(JmsgAccountLog jmsgAccountLog, Model model) {
		model.addAttribute("jmsgAccountLog", jmsgAccountLog);
		return "modules/account/jmsgAccountLogForm";
	}

	@RequiresPermissions("account:jmsgAccountLog:edit")
	@RequestMapping(value = "save")
	public String save(JmsgAccountLog jmsgAccountLog, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgAccountLog)){
			return form(jmsgAccountLog, model);
		}
		jmsgAccountLogService.save(jmsgAccountLog);
		addMessage(redirectAttributes, "保存资金变动日志成功");
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccountLog/?repage";
	}
	
	@RequiresPermissions("account:jmsgAccountLog:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgAccountLog jmsgAccountLog, RedirectAttributes redirectAttributes) {
		jmsgAccountLogService.delete(jmsgAccountLog);
		addMessage(redirectAttributes, "删除资金变动日志成功");
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccountLog/?repage";
	}

}