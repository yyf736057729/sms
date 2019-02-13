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
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.common.utils.IdGen;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsUserAttr;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsUserAttrService;

/**
 * 用户短信属性Controller
 * @author zhukc
 * @version 2016-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsUserAttr")
public class JmsgSmsUserAttrController extends BaseController {

	@Autowired
	private JmsgSmsUserAttrService jmsgSmsUserAttrService;
	
	@ModelAttribute
	public JmsgSmsUserAttr get(@RequestParam(required=false) String id) {
		JmsgSmsUserAttr entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsUserAttrService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsUserAttr();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsUserAttr:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsUserAttr jmsgSmsUserAttr, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsUserAttr> page = jmsgSmsUserAttrService.findPage(new Page<JmsgSmsUserAttr>(request, response), jmsgSmsUserAttr); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsUserAttrList";
	}

	@RequiresPermissions("sms:jmsgSmsUserAttr:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsUserAttr jmsgSmsUserAttr, Model model) {
		model.addAttribute("jmsgSmsUserAttr", jmsgSmsUserAttr);
		return "modules/sms/jmsgSmsUserAttrForm";
	}
	
	@RequiresPermissions("sms:jmsgSmsUserAttr:view")
	@RequestMapping(value = "view")
	public String view(JmsgSmsUserAttr jmsgSmsUserAttr, Model model) {
		model.addAttribute("jmsgSmsUserAttr", jmsgSmsUserAttr);
		return "modules/sms/jmsgSmsUserAttrView";
	}
	
	@RequiresPermissions("sms:jmsgSmsUserAttr:edit")
	@RequestMapping(value = "updateApisecret")
	public String updateApisecret(JmsgSmsUserAttr jmsgSmsUserAttr, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsUserAttr)){
			return form(jmsgSmsUserAttr, model);
		}
		jmsgSmsUserAttr.setApisecret(IdGen.randomBase62(32));
		jmsgSmsUserAttrService.save(jmsgSmsUserAttr);
		model.addAttribute("jmsgSmsUserAttr", jmsgSmsUserAttr);
		return "modules/sms/jmsgSmsUserAttrView";
	}		

	@RequiresPermissions("sms:jmsgSmsUserAttr:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsUserAttr jmsgSmsUserAttr, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsUserAttr)){
			return form(jmsgSmsUserAttr, model);
		}
		if(StringUtils.isBlank(jmsgSmsUserAttr.getApisecret())){
			jmsgSmsUserAttr.setApisecret(IdGen.randomBase62(32));
		}
		
		jmsgSmsUserAttrService.save(jmsgSmsUserAttr);
		addMessage(redirectAttributes, "保存用户短信属性成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserAttr/?repage";
	}
	
	
	@RequiresPermissions("sms:jmsgSmsUserAttr:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsUserAttr jmsgSmsUserAttr, RedirectAttributes redirectAttributes) {
		jmsgSmsUserAttrService.delete(jmsgSmsUserAttr);
		addMessage(redirectAttributes, "删除用户短信属性成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserAttr/?repage";
	}
	
	/**
	 * 验证登录名是否有效
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sms:jmsgSmsUserAttr:edit")
	@RequestMapping(value = "checkUserId")
	public String checkUserId(String oldUserId, String userId) {
		if (userId !=null && userId.equals(oldUserId)) {
			return "true";
		} else if (userId !=null && jmsgSmsUserAttrService.get(userId) == null) {
			return "true";
		}
		return "false";
	}

}