/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.util.List;

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
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleRelation;
import com.siloyou.jmsg.modules.sms.service.JmsgRuleGroupService;
import com.siloyou.jmsg.modules.sms.service.JmsgRuleInfoService;
import com.siloyou.jmsg.modules.sms.service.JmsgRuleRelationService;

/**
 * 规则关系Controller
 * @author zj
 * @version 2017-03-26
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgRuleRelation")
public class JmsgRuleRelationController extends BaseController {

	@Autowired
	private JmsgRuleRelationService jmsgRuleRelationService;
	
	@Autowired
	private JmsgRuleInfoService jmsgRuleInfoService;
	
	@Autowired
	private JmsgRuleGroupService jmsgRuleGroupService;
	
	@ModelAttribute
	public JmsgRuleRelation get(@RequestParam(required=false) String id) {
		JmsgRuleRelation entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgRuleRelationService.get(id);
		}
		if (entity == null){
			entity = new JmsgRuleRelation();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgRuleRelation:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgRuleRelation jmsgRuleRelation, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<JmsgRuleGroup> groupList = jmsgRuleGroupService.findList(new JmsgRuleGroup());
		JmsgRuleInfo jmsgRuleInfo = new JmsgRuleInfo();
		jmsgRuleInfo.setRuleType("4");
		List<JmsgRuleInfo> list = jmsgRuleInfoService.findList(jmsgRuleInfo);
		jmsgRuleRelation.setJmsgRuleInfoList(list);
		jmsgRuleRelation.setJmsgRuleGroupList(groupList);
		
		Page<JmsgRuleRelation> page = jmsgRuleRelationService.findPage(new Page<JmsgRuleRelation>(request, response), jmsgRuleRelation); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgRuleRelationList";
	}

	@RequiresPermissions("sms:jmsgRuleRelation:view")
	@RequestMapping(value = "form")
	public String form(JmsgRuleRelation jmsgRuleRelation, Model model) {
		List<JmsgRuleGroup> groupList = jmsgRuleGroupService.findList(new JmsgRuleGroup());
		JmsgRuleInfo jmsgRuleInfo = new JmsgRuleInfo();
		jmsgRuleInfo.setRuleType("4");
		List<JmsgRuleInfo> list = jmsgRuleInfoService.findList(jmsgRuleInfo);
		jmsgRuleRelation.setJmsgRuleInfoList(list);
		jmsgRuleRelation.setJmsgRuleGroupList(groupList);
		model.addAttribute("jmsgRuleRelation", jmsgRuleRelation);
		return "modules/sms/jmsgRuleRelationForm";
	}

	@RequiresPermissions("sms:jmsgRuleRelation:edit")
	@RequestMapping(value = "save")
	public String save(JmsgRuleRelation jmsgRuleRelation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgRuleRelation)){
			return form(jmsgRuleRelation, model);
		}
		
		if(!"4".equals(jmsgRuleRelation.getRuleType())){
			List<JmsgRuleRelation> list = jmsgRuleRelationService.findList(jmsgRuleRelation);
			if (null != list && list.size() > 0)
			{
				addMessage(redirectAttributes, "保存规则关系失败！当前分组的规则已存在！");
				return "redirect:"+Global.getAdminPath()+"/sms/jmsgRuleRelation/?repage";
			}
		}
		jmsgRuleRelationService.save(jmsgRuleRelation);
		addMessage(redirectAttributes, "保存规则关系成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgRuleRelation/?repage";
	}
	
	@RequiresPermissions("sms:jmsgRuleRelation:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgRuleRelation jmsgRuleRelation, RedirectAttributes redirectAttributes) {
		jmsgRuleRelationService.delete(jmsgRuleRelation);
		addMessage(redirectAttributes, "删除规则关系成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgRuleRelation/?repage";
	}
	
	@RequestMapping(value = "initRuleInfo")
	public String initRuleInfo(JmsgRuleInfo jmsgRuleInfo, HttpServletResponse response){
		List<JmsgRuleInfo> list = jmsgRuleInfoService.initRuleInfo(jmsgRuleInfo);
		return renderString(response, list);
	}

}