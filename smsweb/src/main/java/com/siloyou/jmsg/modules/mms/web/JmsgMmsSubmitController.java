/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.web;

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
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsSubmit;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsSubmitService;

/**
 * 网关状态Controller
 * @author zhukc
 * @version 2016-05-28
 */
@Controller
@RequestMapping(value = "${adminPath}/mms/jmsgMmsSubmit")
public class JmsgMmsSubmitController extends BaseController {

	@Autowired
	private JmsgMmsSubmitService jmsgMmsSubmitService;
	
	@ModelAttribute
	public JmsgMmsSubmit get(@RequestParam(required=false) String id) {
		JmsgMmsSubmit entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgMmsSubmitService.get(id);
		}
		if (entity == null){
			entity = new JmsgMmsSubmit();
		}
		return entity;
	}
	
	@RequiresPermissions("mms:jmsgMmsSubmit:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgMmsSubmit jmsgMmsSubmit, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgMmsSubmit> page = jmsgMmsSubmitService.findPage(new Page<JmsgMmsSubmit>(request, response), jmsgMmsSubmit); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsSubmitList";
	}

	@RequiresPermissions("mms:jmsgMmsSubmit:view")
	@RequestMapping(value = "form")
	public String form(JmsgMmsSubmit jmsgMmsSubmit, Model model) {
		model.addAttribute("jmsgMmsSubmit", jmsgMmsSubmit);
		return "modules/mms/jmsgMmsSubmitForm";
	}

	@RequiresPermissions("mms:jmsgMmsSubmit:edit")
	@RequestMapping(value = "save")
	public String save(JmsgMmsSubmit jmsgMmsSubmit, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgMmsSubmit)){
			return form(jmsgMmsSubmit, model);
		}
		jmsgMmsSubmitService.save(jmsgMmsSubmit);
		addMessage(redirectAttributes, "保存网关状态成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsSubmit/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsSubmit:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgMmsSubmit jmsgMmsSubmit, RedirectAttributes redirectAttributes) {
		jmsgMmsSubmitService.delete(jmsgMmsSubmit);
		addMessage(redirectAttributes, "删除网关状态成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsSubmit/?repage";
	}

}