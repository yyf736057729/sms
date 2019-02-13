/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import com.siloyou.jmsg.common.utils.RuleUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleInfo;
import com.siloyou.jmsg.modules.sms.service.JmsgRuleGroupService;
import com.siloyou.jmsg.modules.sms.service.JmsgRuleInfoService;

/**
 * 规则管理Controller
 * @author zj
 * @version 2017-03-26
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgRuleInfo")
public class JmsgRuleInfoController extends BaseController {

	@Autowired
	private JmsgRuleInfoService jmsgRuleInfoService;
	
	@Autowired
	private JmsgRuleGroupService jmsgRuleGroupService;
	
	@ModelAttribute
	public JmsgRuleInfo get(@RequestParam(required=false) String id) {
		JmsgRuleInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgRuleInfoService.get(id);
		}
		if (entity == null){
			entity = new JmsgRuleInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgRuleInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgRuleInfo jmsgRuleInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgRuleInfo> page = jmsgRuleInfoService.findPage(new Page<JmsgRuleInfo>(request, response), jmsgRuleInfo); 
		jmsgRuleInfo.setGroupList(jmsgRuleGroupService.findRuleGroup());
		model.addAttribute("jmsgRuleInfo", jmsgRuleInfo);
		model.addAttribute("page", page);
		return "modules/sms/jmsgRuleInfoList";
	}

	@RequiresPermissions("sms:jmsgRuleInfo:view")
	@RequestMapping(value = "form")
	public String form(JmsgRuleInfo jmsgRuleInfo, Model model) {
		model.addAttribute("jmsgRuleInfo", jmsgRuleInfo);
		return "modules/sms/jmsgRuleInfoForm";
	}

	@RequiresPermissions("sms:jmsgRuleInfo:edit")
	@RequestMapping(value = "save")
	public String save(JmsgRuleInfo jmsgRuleInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgRuleInfo)){
			return form(jmsgRuleInfo, model);
		}
		jmsgRuleInfoService.save(jmsgRuleInfo);
		addMessage(redirectAttributes, "保存规则管理成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgRuleInfo/?repage";
	}
	
	@RequiresPermissions("sms:jmsgRuleInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgRuleInfo jmsgRuleInfo, RedirectAttributes redirectAttributes) {
		jmsgRuleInfoService.delete(jmsgRuleInfo);
		addMessage(redirectAttributes, "删除规则管理成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgRuleInfo/?repage";
	}
	
	@RequiresPermissions("sms:jmsgRuleInfo:edit")
	@RequestMapping(value = "testRule")
	public String testRule(String ruleContent,String smsContent,String ruleType,HttpServletResponse response) {
		try {
			ruleContent = URLDecoder.decode(ruleContent, "UTF-8");
			smsContent = URLDecoder.decode(smsContent, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = RuleUtils.testRule(smsContent, ruleContent, ruleType);
		return renderString(response, result);
	}
	
	@RequiresPermissions("sms:jmsgRuleInfo:edit")
	@RequestMapping(value = "testGroup")
	public String testGroup(String groupId,String smsContent,HttpServletResponse response) {
		try {
			smsContent = URLDecoder.decode(smsContent, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = RuleUtils.testGroupRule(smsContent, groupId);
		return renderString(response, result);
	}
	
	//同步
	@RequiresPermissions("sms:jmsgRuleInfo:edit")
	@RequestMapping(value = "syncRule")
	public String syncRule(String ruleContent,String smsContent,String ruleType,HttpServletResponse response) {
		
		String code = "1";
		try {
			jmsgRuleInfoService.syncRule();
		} catch (Exception e) {
			code = "0";
			logger.debug("同步规则错误:{}",e);
			e.printStackTrace();
		}
		return renderString(response, code);
	}

}