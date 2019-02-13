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
import com.siloyou.jmsg.modules.sms.entity.JmsgGroup;
import com.siloyou.jmsg.modules.sms.service.JmsgGroupService;

/**
 * 分组信息Controller
 * @author zhukc
 * @version 2016-07-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgGroup")
public class JmsgGroupController extends BaseController {

	@Autowired
	private JmsgGroupService jmsgGroupService;
	
	@ModelAttribute
	public JmsgGroup get(@RequestParam(required=false) String id) {
		JmsgGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgGroupService.get(id);
		}
		if (entity == null){
			entity = new JmsgGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgGroup jmsgGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgGroup> page = jmsgGroupService.findPage(new Page<JmsgGroup>(request, response), jmsgGroup);
		model.addAttribute("page", page);
		return "modules/sms/jmsgGroupList";
	}

	@RequiresPermissions("sms:jmsgGroup:view")
	@RequestMapping(value = "form")
	public String form(JmsgGroup jmsgGroup, Model model) {
		model.addAttribute("jmsgGroup", jmsgGroup);
		return "modules/sms/jmsgGroupForm";
	}

	@RequiresPermissions("sms:jmsgGroup:edit")
	@RequestMapping(value = "save")
	public String save(JmsgGroup jmsgGroup, String oldName,Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgGroup)){
			return form(jmsgGroup, model);
		}
		
		if (!"true".equals(checkName(oldName, jmsgGroup.getName()))){
			addMessage(model, "操作失败，分组名称已存在");
			return form(jmsgGroup, model);
		}
		
		jmsgGroupService.save(jmsgGroup);
		addMessage(redirectAttributes, "保存分组信息成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGroup/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgGroup jmsgGroup, RedirectAttributes redirectAttributes) {
		jmsgGroupService.delete(jmsgGroup);
		addMessage(redirectAttributes, "删除分组信息成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGroup/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGroup:edit")
	@RequestMapping(value = "updateStatus")
	public String updateStatus(JmsgGroup jmsgGroup, String oldStatus,RedirectAttributes redirectAttributes) {
		String msg = "禁用分组信息成功";
		String status = "0";
		if("0".equals(oldStatus)){
			status = "1";
			msg = "启用分组信息成功";
		}
		jmsgGroup.setStatus(status);
		jmsgGroupService.updateStatus(jmsgGroup);
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGroup/?repage";
	}
	
	public String checkName(String oldName, String name) {
		if (name !=null && name.equals(oldName)) {
			return "true";
		} else if (name !=null && jmsgGroupService.getByParam(name) == null) {
			return "true";
		}
		return "false";
	}

}