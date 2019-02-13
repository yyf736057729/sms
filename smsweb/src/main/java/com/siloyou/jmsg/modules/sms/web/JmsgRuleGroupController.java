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
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleGroup;
import com.siloyou.jmsg.modules.sms.service.JmsgRuleGroupService;

/**
 * 规则分组Controller
 * @author zj
 * @version 2017-03-26
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgRuleGroup")
public class JmsgRuleGroupController extends BaseController {

	@Autowired
	private JmsgRuleGroupService jmsgRuleGroupService;
	
	@ModelAttribute
	public JmsgRuleGroup get(@RequestParam(required=false) String id) {
		JmsgRuleGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgRuleGroupService.get(id);
		}
		if (entity == null){
			entity = new JmsgRuleGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgRuleGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgRuleGroup jmsgRuleGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgRuleGroup> page = jmsgRuleGroupService.findPage(new Page<JmsgRuleGroup>(request, response), jmsgRuleGroup); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgRuleGroupList";
	}

	@RequiresPermissions("sms:jmsgRuleGroup:view")
	@RequestMapping(value = "form")
	public String form(JmsgRuleGroup jmsgRuleGroup, Model model) {
		model.addAttribute("jmsgRuleGroup", jmsgRuleGroup);
		return "modules/sms/jmsgRuleGroupForm";
	}

	@RequiresPermissions("sms:jmsgRuleGroup:edit")
	@RequestMapping(value = "save")
	public String save(JmsgRuleGroup jmsgRuleGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgRuleGroup)){
			return form(jmsgRuleGroup, model);
		}
		jmsgRuleGroupService.save(jmsgRuleGroup);
		addMessage(redirectAttributes, "保存规则分组成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgRuleGroup/?repage";
	}
	
	@RequiresPermissions("sms:jmsgRuleGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgRuleGroup jmsgRuleGroup, RedirectAttributes redirectAttributes) {
		jmsgRuleGroupService.delete(jmsgRuleGroup);
		addMessage(redirectAttributes, "删除规则分组成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgRuleGroup/?repage";
	}

}