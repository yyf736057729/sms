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
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsKeywords;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsKeywordsService;

/**
 * 敏感词Controller
 * @author zhukc
 * @version 2016-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsKeywords")
public class JmsgSmsKeywordsController extends BaseController {

	@Autowired
	private JmsgSmsKeywordsService jmsgSmsKeywordsService;
	
	@ModelAttribute
	public JmsgSmsKeywords get(@RequestParam(required=false) String id) {
		JmsgSmsKeywords entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsKeywordsService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsKeywords();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsKeywords:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsKeywords jmsgSmsKeywords, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsKeywords> page = jmsgSmsKeywordsService.findPage(new Page<JmsgSmsKeywords>(request, response), jmsgSmsKeywords); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsKeywordsList";
	}

	@RequiresPermissions("sms:jmsgSmsKeywords:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsKeywords jmsgSmsKeywords, Model model) {
		model.addAttribute("jmsgSmsKeywords", jmsgSmsKeywords);
		return "modules/sms/jmsgSmsKeywordsForm";
	}

	@RequiresPermissions("sms:jmsgSmsKeywords:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsKeywords jmsgSmsKeywords, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsKeywords)){
			return form(jmsgSmsKeywords, model);
		}
		jmsgSmsKeywords.setScope("0");
		jmsgSmsKeywordsService.save(jmsgSmsKeywords);
		addMessage(redirectAttributes, "保存敏感词成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsKeywords/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsKeywords:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsKeywords jmsgSmsKeywords, RedirectAttributes redirectAttributes) {
		jmsgSmsKeywordsService.delete(jmsgSmsKeywords);
		KeywordsUtils.del(jmsgSmsKeywords.getKeywords().trim());
		addMessage(redirectAttributes, "删除敏感词成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsKeywords/?repage";
	}
	
	/**
	 * 验证敏感词是否有效
	 * @param oldPhone
	 * @param phone
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sms:jmsgSmsKeywords:edit")
	@RequestMapping(value = "checkKeywords")
	public String checkKeywords(String oldKeywords, String keywords) {
		keywords = keywords.trim();
		if (keywords !=null && keywords.equals(oldKeywords)) {
			return "true";
		} else if (keywords !=null && jmsgSmsKeywordsService.getByKeywords(keywords) == null) {
			return "true";
		}
		return "false";
	}
	
}