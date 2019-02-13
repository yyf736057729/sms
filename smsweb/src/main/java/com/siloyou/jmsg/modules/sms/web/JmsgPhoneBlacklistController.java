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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneBlacklist;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneBlacklistService;

/**
 * 黑名单Controller
 * @author zhukc
 * @version 2016-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhoneBlacklist")
public class JmsgPhoneBlacklistController extends BaseController {

	@Autowired
	private JmsgPhoneBlacklistService jmsgPhoneBlacklistService;
	
	@ModelAttribute
	public JmsgPhoneBlacklist get(@RequestParam(required=false) String id) {
		JmsgPhoneBlacklist entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgPhoneBlacklistService.get(id);
		}
		if (entity == null){
			entity = new JmsgPhoneBlacklist();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgPhoneBlacklist:view")
	@RequestMapping(value = "init")
	public String init(){
		return "modules/sms/jmsgPhoneBlacklistList";
	}
	
	@RequiresPermissions("sms:jmsgPhoneBlacklist:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgPhoneBlacklist jmsgPhoneBlacklist, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgPhoneBlacklist> page = jmsgPhoneBlacklistService.findPage(new Page<JmsgPhoneBlacklist>(request, response), jmsgPhoneBlacklist); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgPhoneBlacklistList";
	}

	@RequiresPermissions("sms:jmsgPhoneBlacklist:view")
	@RequestMapping(value = "form")
	public String form(JmsgPhoneBlacklist jmsgPhoneBlacklist, Model model) {
		model.addAttribute("jmsgPhoneBlacklist", jmsgPhoneBlacklist);
		return "modules/sms/jmsgPhoneBlacklistForm";
	}

	@RequiresPermissions("sms:jmsgPhoneBlacklist:edit")
	@RequestMapping(value = "save")
	public String save(JmsgPhoneBlacklist jmsgPhoneBlacklist, Model model, RedirectAttributes redirectAttributes) {
		jmsgPhoneBlacklistService.save(jmsgPhoneBlacklist);
		addMessage(redirectAttributes, "保存黑名单成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneBlacklist/?repage";
	}
	
	@RequiresPermissions("sms:jmsgPhoneBlacklist:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgPhoneBlacklist jmsgPhoneBlacklist, RedirectAttributes redirectAttributes) {
		jmsgPhoneBlacklistService.deleteByPhone(jmsgPhoneBlacklist.getPhone());
		addMessage(redirectAttributes, "删除黑名单成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneBlacklist/?repage";
	}
	
	/**
	 * 验证手机号码是否有效
	 * @param oldPhone
	 * @param phone
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sms:jmsgPhoneBlacklist:edit")
	@RequestMapping(value = "checkPhone")
	public String checkPhone(String oldPhone, String phone) {
		if (phone !=null && phone.equals(oldPhone)) {
			return "true";
		} else if (phone !=null && jmsgPhoneBlacklistService.getByPhone(phone) == null) {
			return "true";
		}
		return "false";
	}

}