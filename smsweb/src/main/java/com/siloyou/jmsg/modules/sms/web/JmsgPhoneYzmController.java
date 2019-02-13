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
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneYzm;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneYzmService;

/**
 * 验证码黑名单Controller
 * @author zhukc
 * @version 2017-12-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhoneYzm")
public class JmsgPhoneYzmController extends BaseController {

	@Autowired
	private JmsgPhoneYzmService jmsgPhoneYzmService;
	
	@ModelAttribute
	public JmsgPhoneYzm get(@RequestParam(required=false) String id) {
		JmsgPhoneYzm entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgPhoneYzmService.get(id);
		}
		if (entity == null){
			entity = new JmsgPhoneYzm();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgPhoneYzm:view")
	@RequestMapping(value = "init")
	public String init(JmsgPhoneYzm jmsgPhoneYzm, Model model) {
		return "modules/sms/jmsgPhoneYzmList";
	}
	
	@RequiresPermissions("sms:jmsgPhoneYzm:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgPhoneYzm jmsgPhoneYzm, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgPhoneYzm> page = jmsgPhoneYzmService.findPage(new Page<JmsgPhoneYzm>(request, response), jmsgPhoneYzm); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgPhoneYzmList";
	}

	@RequiresPermissions("sms:jmsgPhoneYzm:view")
	@RequestMapping(value = "form")
	public String form(JmsgPhoneYzm jmsgPhoneYzm, Model model) {
		model.addAttribute("jmsgPhoneYzm", jmsgPhoneYzm);
		return "modules/sms/jmsgPhoneYzmForm";
	}

	@RequiresPermissions("sms:jmsgPhoneYzm:edit")
	@RequestMapping(value = "save")
	public String save(JmsgPhoneYzm jmsgPhoneYzm, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgPhoneYzm)){
			return form(jmsgPhoneYzm, model);
		}
		jmsgPhoneYzmService.save(jmsgPhoneYzm);
		addMessage(redirectAttributes, "保存验证码黑名单成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneYzm/init";
	}
	
	@RequiresPermissions("sms:jmsgPhoneYzm:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgPhoneYzm jmsgPhoneYzm, RedirectAttributes redirectAttributes) {
		jmsgPhoneYzmService.deleteByPhone(jmsgPhoneYzm.getPhone());
		addMessage(redirectAttributes, "删除验证码黑名单成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneYzm/init";
	}

}