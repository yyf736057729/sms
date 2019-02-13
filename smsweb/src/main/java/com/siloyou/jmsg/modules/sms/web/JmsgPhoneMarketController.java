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
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneMarket;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneMarketService;

/**
 * 营销黑名单Controller
 * @author zhukc
 * @version 2017-12-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhoneMarket")
public class JmsgPhoneMarketController extends BaseController {

	@Autowired
	private JmsgPhoneMarketService jmsgPhoneMarketService;
	
	@ModelAttribute
	public JmsgPhoneMarket get(@RequestParam(required=false) String id) {
		JmsgPhoneMarket entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgPhoneMarketService.get(id);
		}
		if (entity == null){
			entity = new JmsgPhoneMarket();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgPhoneMarket:view")
	@RequestMapping(value = "init")
	public String init(JmsgPhoneMarket jmsgPhoneMarket, Model model) {
		return "modules/sms/jmsgPhoneMarketList";
	}
	
	@RequiresPermissions("sms:jmsgPhoneMarket:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgPhoneMarket jmsgPhoneMarket, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgPhoneMarket> page = jmsgPhoneMarketService.findPage(new Page<JmsgPhoneMarket>(request, response), jmsgPhoneMarket); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgPhoneMarketList";
	}

	@RequiresPermissions("sms:jmsgPhoneMarket:view")
	@RequestMapping(value = "form")
	public String form(JmsgPhoneMarket jmsgPhoneMarket, Model model) {
		model.addAttribute("jmsgPhoneMarket", jmsgPhoneMarket);
		return "modules/sms/jmsgPhoneMarketForm";
	}

	@RequiresPermissions("sms:jmsgPhoneMarket:edit")
	@RequestMapping(value = "save")
	public String save(JmsgPhoneMarket jmsgPhoneMarket, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgPhoneMarket)){
			return form(jmsgPhoneMarket, model);
		}
		jmsgPhoneMarketService.save(jmsgPhoneMarket);
		addMessage(redirectAttributes, "保存营销黑名单成功");
		//return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneMarket/init";
		return "redirect:" + Global.getAdminPath() + "/sms/jmsgPhoneMarket/?repage";
	}
	
	@RequiresPermissions("sms:jmsgPhoneMarket:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgPhoneMarket jmsgPhoneMarket, RedirectAttributes redirectAttributes) {
		jmsgPhoneMarketService.deleteByPhone(jmsgPhoneMarket.getPhone());
		addMessage(redirectAttributes, "删除营销黑名单成功");
		//return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneMarket/init";
		return "redirect:" + Global.getAdminPath() + "/sms/jmsgPhoneMarket/?repage";
	}

}