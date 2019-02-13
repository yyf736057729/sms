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
import com.siloyou.jmsg.modules.sms.entity.JmsgPhone;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneService;

/**
 * 动态黑名单Controller
 * @author zj
 * @version 2016-09-26
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhone")
public class JmsgPhoneController extends BaseController {

	@Autowired
	private JmsgPhoneService jmsgPhoneService;
	
	@ModelAttribute
	public JmsgPhone get(@RequestParam(required=false) String id) {
		JmsgPhone entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgPhoneService.get(id);
		}
		if (entity == null){
			entity = new JmsgPhone();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgPhone:view")
	@RequestMapping(value = "init")
	public String init(JmsgPhone jmsgPhone, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/sms/jmsgPhoneList";
	}
	
	@RequiresPermissions("sms:jmsgPhone:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgPhone jmsgPhone, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgPhone> page = jmsgPhoneService.findPage(new Page<JmsgPhone>(request, response), jmsgPhone); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgPhoneList";
	}

	@RequiresPermissions("sms:jmsgPhone:view")
	@RequestMapping(value = "form")
	public String form(JmsgPhone jmsgPhone, Model model) {
		model.addAttribute("jmsgPhone", jmsgPhone);
		return "modules/sms/jmsgPhoneForm";
	}

	@RequiresPermissions("sms:jmsgPhone:edit")
	@RequestMapping(value = "save")
	public String save(JmsgPhone jmsgPhone, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgPhone)){
			return form(jmsgPhone, model);
		}
        jmsgPhoneService.save(jmsgPhone);
		addMessage(redirectAttributes, "保存动态黑名单成功");
		//return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhone/init";
		return "redirect:" + Global.getAdminPath() + "/sms/jmsgPhone/?repage";
	}
	
	@RequiresPermissions("sms:jmsgPhone:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgPhone jmsgPhone, RedirectAttributes redirectAttributes) {
		jmsgPhoneService.deleteByPhone(jmsgPhone.getPhone());
		addMessage(redirectAttributes, "删除动态黑名单成功");
		//return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhone/init";
		return "redirect:" + Global.getAdminPath() + "/sms/jmsgPhone/?repage";
	}

}