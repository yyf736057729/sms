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
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliver;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsDeliverService;

/**
 * 短信上行Controller
 * @author zhukc
 * @version 2016-08-13
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsDeliver")
public class JmsgSmsDeliverController extends BaseController {

	@Autowired
	private JmsgSmsDeliverService jmsgSmsDeliverService;
	
	@ModelAttribute
	public JmsgSmsDeliver get(@RequestParam(required=false) String id) {
		JmsgSmsDeliver entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsDeliverService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsDeliver();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsDeliver:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsDeliver jmsgSmsDeliver, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsDeliver> page = jmsgSmsDeliverService.findPage(new Page<JmsgSmsDeliver>(request, response), jmsgSmsDeliver); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDeliverList";
	}

	@RequiresPermissions("sms:jmsgSmsDeliver:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsDeliver jmsgSmsDeliver, Model model) {
		model.addAttribute("jmsgSmsDeliver", jmsgSmsDeliver);
		return "modules/sms/jmsgSmsDeliverForm";
	}

	@RequiresPermissions("sms:jmsgSmsDeliver:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsDeliver jmsgSmsDeliver, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsDeliver)){
			return form(jmsgSmsDeliver, model);
		}
		jmsgSmsDeliverService.save(jmsgSmsDeliver);
		addMessage(redirectAttributes, "保存短信上行成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsDeliver/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsDeliver:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsDeliver jmsgSmsDeliver, RedirectAttributes redirectAttributes) {
		jmsgSmsDeliverService.delete(jmsgSmsDeliver);
		addMessage(redirectAttributes, "删除短信上行成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsDeliver/?repage";
	}

}