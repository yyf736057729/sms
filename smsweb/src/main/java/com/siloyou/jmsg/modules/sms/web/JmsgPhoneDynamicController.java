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
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneDynamic;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneDynamicService;

/**
 * 动态黑名单Controller
 * @author zhukc
 * @version 2016-06-21
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhoneDynamic")
public class JmsgPhoneDynamicController extends BaseController {

	@Autowired
	private JmsgPhoneDynamicService jmsgPhoneDynamicService;
	
	@ModelAttribute
	public JmsgPhoneDynamic get(@RequestParam(required=false) String id) {
		JmsgPhoneDynamic entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgPhoneDynamicService.get(id);
		}
		if (entity == null){
			entity = new JmsgPhoneDynamic();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgPhoneDynamic:view")
	@RequestMapping(value = "init")
	public String init(Model model) {
		String time = syncTime();
		model.addAttribute("time", StringUtils.isBlank(time)?"未知":time);
		return "modules/sms/jmsgPhoneDynamicList";
	}
	
	@RequiresPermissions("sms:jmsgPhoneDynamic:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgPhoneDynamic jmsgPhoneDynamic, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgPhoneDynamic> page = jmsgPhoneDynamicService.findPage(new Page<JmsgPhoneDynamic>(request, response), jmsgPhoneDynamic); 
		model.addAttribute("page", page);
		model.addAttribute("time", syncTime());
		return "modules/sms/jmsgPhoneDynamicList";
	}

	@RequiresPermissions("sms:jmsgPhoneDynamic:view")
	@RequestMapping(value = "form")
	public String form(JmsgPhoneDynamic jmsgPhoneDynamic, Model model) {
		model.addAttribute("jmsgPhoneDynamic", jmsgPhoneDynamic);
		return "modules/sms/jmsgPhoneDynamicForm";
	}

	@RequiresPermissions("sms:jmsgPhoneDynamic:edit")
	@RequestMapping(value = "save")
	public String save(JmsgPhoneDynamic jmsgPhoneDynamic, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgPhoneDynamic)){
			return form(jmsgPhoneDynamic, model);
		}
		String phone = jmsgPhoneDynamic.getPhone();
		if(!BlacklistUtils.isExist(phone,1)){
			jmsgPhoneDynamic.setType("0");
			jmsgPhoneDynamicService.save(jmsgPhoneDynamic);
			BlacklistUtils.put(phone,1,1);
		}
		addMessage(redirectAttributes, "保存动态黑名单成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneDynamic/?repage";
	}
	
	@RequiresPermissions("sms:jmsgPhoneDynamic:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgPhoneDynamic jmsgPhoneDynamic, RedirectAttributes redirectAttributes) {
		jmsgPhoneDynamicService.delete(jmsgPhoneDynamic);
		BlacklistUtils.del(jmsgPhoneDynamic.getPhone(),1);
		addMessage(redirectAttributes, "删除动态黑名单成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneDynamic/?repage";
	}
	
	@RequiresPermissions("sms:jmsgPhoneDynamic:edit")
	@RequestMapping(value = "sync")
	public String sync(RedirectAttributes redirectAttributes) {
		String createtime = syncTime();
		if(StringUtils.isBlank(createtime)){
			createtime = DateUtils.getDateTime();
		}
		JmsgPhoneDynamic jmsgPhoneDynamic = new JmsgPhoneDynamic();
		jmsgPhoneDynamic.setCreatetime(DateUtils.parseDate(createtime));
		List<String> list = jmsgPhoneDynamicService.findPhoneList(jmsgPhoneDynamic);
        BlacklistUtils.put(list, 1, 1);
        JedisClusterUtils.set("phone_dynamic_sync_time", DateUtils.getDateTime(), 0);//设置同步时间
		addMessage(redirectAttributes, "同步动态黑名单成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgPhoneDynamic/?repage";
	}
	
	private String syncTime(){
		return JedisClusterUtils.get("phone_dynamic_sync_time");//获取同步时间
	}

}