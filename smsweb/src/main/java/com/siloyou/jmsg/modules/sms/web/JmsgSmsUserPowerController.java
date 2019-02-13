/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsUserPower;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsUserPowerService;

/**
 * 用户短信能力Controller
 * @author zhukc
 * @version 2016-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsUserPower")
public class JmsgSmsUserPowerController extends BaseController {

	@Autowired
	private JmsgSmsUserPowerService jmsgSmsUserPowerService;
	
	@ModelAttribute
	public JmsgSmsUserPower get(@RequestParam(required=false) String id) {
		JmsgSmsUserPower entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsUserPowerService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsUserPower();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsUserPower:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsUserPower jmsgSmsUserPower, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsUserPower> page = jmsgSmsUserPowerService.findPage(new Page<JmsgSmsUserPower>(request, response), jmsgSmsUserPower); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsUserPowerList";
	}

	@RequiresPermissions("sms:jmsgSmsUserPower:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsUserPower jmsgSmsUserPower, Model model) {
		model.addAttribute("jmsgSmsUserPower", jmsgSmsUserPower);
		return "modules/sms/jmsgSmsUserPowerForm";
	}

	@RequiresPermissions("sms:jmsgSmsUserPower:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsUserPower jmsgSmsUserPower, Model model, RedirectAttributes redirectAttributes)  {
		if (!beanValidator(model, jmsgSmsUserPower)){
			return form(jmsgSmsUserPower, model);
		}
		String msg = "保存用户短信能力成功";
		try{
			jmsgSmsUserPowerService.save(jmsgSmsUserPower);
		}catch(DuplicateKeyException e){
			msg = "用户短信能力已经存在";
			e.printStackTrace();
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserPower/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsUserPower:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsUserPower jmsgSmsUserPower, RedirectAttributes redirectAttributes) {
		jmsgSmsUserPowerService.delete(jmsgSmsUserPower);
		addMessage(redirectAttributes, "删除用户短信能力成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserPower/?repage";
	}

}