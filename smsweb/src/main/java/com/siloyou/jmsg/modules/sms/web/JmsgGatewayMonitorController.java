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
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonitor;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayMonitorService;

/**
 * 网关告警Controller
 * @author zj
 * @version 2016-10-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgGatewayMonitor")
public class JmsgGatewayMonitorController extends BaseController {

	@Autowired
	private JmsgGatewayMonitorService jmsgGatewayMonitorService;
	
	@ModelAttribute
	public JmsgGatewayMonitor get(@RequestParam(required=false) String id) {
		JmsgGatewayMonitor entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgGatewayMonitorService.get(id);
		}
		if (entity == null){
			entity = new JmsgGatewayMonitor();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgGatewayMonitor:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgGatewayMonitor jmsgGatewayMonitor, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgGatewayMonitor> page = jmsgGatewayMonitorService.findPage(new Page<JmsgGatewayMonitor>(request, response), jmsgGatewayMonitor); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgGatewayMonitorList";
	}

	@RequiresPermissions("sms:jmsgGatewayMonitor:view")
	@RequestMapping(value = "form")
	public String form(JmsgGatewayMonitor jmsgGatewayMonitor, Model model) {
		model.addAttribute("jmsgGatewayMonitor", jmsgGatewayMonitor);
		return "modules/sms/jmsgGatewayMonitorForm";
	}

	@RequiresPermissions("sms:jmsgGatewayMonitor:edit")
	@RequestMapping(value = "save")
	public String save(JmsgGatewayMonitor jmsgGatewayMonitor, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgGatewayMonitor)){
			return form(jmsgGatewayMonitor, model);
		}
		jmsgGatewayMonitorService.save(jmsgGatewayMonitor);
		addMessage(redirectAttributes, "保存网关告警成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayMonitor/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGatewayMonitor:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgGatewayMonitor jmsgGatewayMonitor, RedirectAttributes redirectAttributes) {
		jmsgGatewayMonitorService.delete(jmsgGatewayMonitor);
		addMessage(redirectAttributes, "删除网关告警成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayMonitor/?repage";
	}

}