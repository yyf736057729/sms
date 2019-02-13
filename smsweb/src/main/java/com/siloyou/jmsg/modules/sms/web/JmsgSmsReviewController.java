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
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReview;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsReviewService;

/**
 * 短信审核Controller
 * @author zhukc
 * @version 2016-07-16
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsReview")
public class JmsgSmsReviewController extends BaseController {

	@Autowired
	private JmsgSmsReviewService jmsgSmsReviewService;
	
	@ModelAttribute
	public JmsgSmsReview get(@RequestParam(required=false) String id) {
		JmsgSmsReview entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsReviewService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsReview();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsReview:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsReview jmsgSmsReview, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsReview> page = jmsgSmsReviewService.findPage(new Page<JmsgSmsReview>(request, response), jmsgSmsReview); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsReviewList";
	}

	@RequiresPermissions("sms:jmsgSmsReview:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsReview jmsgSmsReview, Model model) {
		model.addAttribute("jmsgSmsReview", jmsgSmsReview);
		return "modules/sms/jmsgSmsReviewForm";
	}

	@RequiresPermissions("sms:jmsgSmsReview:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsReview jmsgSmsReview, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsReview)){
			return form(jmsgSmsReview, model);
		}
		jmsgSmsReviewService.save(jmsgSmsReview);
		addMessage(redirectAttributes, "保存短信审核成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsReview/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsReview:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsReview jmsgSmsReview, RedirectAttributes redirectAttributes) {
		jmsgSmsReviewService.delete(jmsgSmsReview);
		addMessage(redirectAttributes, "删除短信审核成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsReview/?repage";
	}

}