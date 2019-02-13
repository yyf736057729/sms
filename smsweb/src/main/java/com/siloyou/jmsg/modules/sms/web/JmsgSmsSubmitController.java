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
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSubmit;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSubmitService;

/**
 * 网关状态Controller
 * @author zhukc
 * @version 2016-08-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsSubmit")
public class JmsgSmsSubmitController extends BaseController {

	@Autowired
	private JmsgSmsSubmitService jmsgSmsSubmitService;
	
	@ModelAttribute
	public JmsgSmsSubmit get(@RequestParam(required=false) String id) {
		JmsgSmsSubmit entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsSubmitService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsSubmit();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsSubmit:view")
    @RequestMapping(value = "findErrorForReSend")
    public String findErrorForReSend(JmsgSmsSubmit jmsgSmsSubmit, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<JmsgSmsSubmit> page = jmsgSmsSubmitService.findErrorForReSend(new Page<JmsgSmsSubmit>(request, response), jmsgSmsSubmit); 
        model.addAttribute("page", page);
        return "modules/sms/jmsgSmsSubmitEList";
    }
	
	@RequiresPermissions("sms:jmsgSmsSubmit:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsSubmit jmsgSmsSubmit, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsSubmit> page = jmsgSmsSubmitService.findPage(new Page<JmsgSmsSubmit>(request, response), jmsgSmsSubmit); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsSubmitList";
	}

	@RequiresPermissions("sms:jmsgSmsSubmit:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsSubmit jmsgSmsSubmit, Model model) {
		model.addAttribute("jmsgSmsSubmit", jmsgSmsSubmit);
		return "modules/sms/jmsgSmsSubmitForm";
	}

	@RequiresPermissions("sms:jmsgSmsSubmit:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsSubmit jmsgSmsSubmit, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsSubmit)){
			return form(jmsgSmsSubmit, model);
		}
		jmsgSmsSubmitService.save(jmsgSmsSubmit);
		addMessage(redirectAttributes, "保存网关状态成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsSubmit/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsSubmit:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsSubmit jmsgSmsSubmit, RedirectAttributes redirectAttributes) {
		jmsgSmsSubmitService.delete(jmsgSmsSubmit);
		addMessage(redirectAttributes, "删除网关状态成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsSubmit/?repage";
	}

}