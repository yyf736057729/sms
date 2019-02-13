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
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.common.utils.PhoneTypeUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneType;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneTypeService;

/**
 * 号码运营商Controller
 * @author zhukc
 * @version 2016-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhoneType")
public class JmsgPhoneTypeController extends BaseController {

	@Autowired
	private JmsgPhoneTypeService jmsgPhoneTypeService;
	
	@ModelAttribute
	public JmsgPhoneType get(@RequestParam(required=false) String id) {
		JmsgPhoneType entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgPhoneTypeService.get(id);
		}
		if (entity == null){
			entity = new JmsgPhoneType();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgPhoneType:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgPhoneType jmsgPhoneType, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgPhoneType> page = jmsgPhoneTypeService.findPage(new Page<JmsgPhoneType>(request, response), jmsgPhoneType); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgPhoneTypeList";
	}

	@RequiresPermissions("sms:jmsgPhoneType:view")
	@RequestMapping(value = "form")
	public String form(JmsgPhoneType jmsgPhoneType, Model model) {
		model.addAttribute("jmsgPhoneType", jmsgPhoneType);
		return "modules/sms/jmsgPhoneTypeForm";
	}

	@RequiresPermissions("sms:jmsgPhoneType:edit")
	@RequestMapping(value = "save")
	public String save(JmsgPhoneType jmsgPhoneType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgPhoneType)){
			return form(jmsgPhoneType, model);
		}
		jmsgPhoneTypeService.save(jmsgPhoneType);
		addMessage(redirectAttributes, "保存号码运营商成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneType/?repage";
	}
	
	@RequiresPermissions("sms:jmsgPhoneType:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgPhoneType jmsgPhoneType, RedirectAttributes redirectAttributes) {
		PhoneTypeUtils.remove(jmsgPhoneType.getNum());
		jmsgPhoneTypeService.delete(jmsgPhoneType);
		addMessage(redirectAttributes, "删除号码运营商成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneType/?repage";
	}
	
	
	/**
	 * 验证号段是否有效
	 * @param oldPhone
	 * @param phone
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sms:jmsgPhoneType:edit")
	@RequestMapping(value = "checkNum")
	public String checkNum(String oldNum, String num) {
		if (num !=null && num.equals(oldNum)) {
			return "true";
		} else if (num !=null && jmsgPhoneTypeService.getByNum(num) == null) {
			return "true";
		}
		return "false";
	}

}