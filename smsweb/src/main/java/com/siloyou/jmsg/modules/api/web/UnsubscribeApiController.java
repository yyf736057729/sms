package com.siloyou.jmsg.modules.api.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneDynamic;
import com.siloyou.jmsg.modules.sms.service.JmsgPhoneDynamicService;

@Controller
@RequestMapping(value = "${apiPath}")
public class UnsubscribeApiController extends BaseController{
	
	@Autowired
	private JmsgPhoneDynamicService jmsgPhoneDynamicService;
	
	@RequestMapping(value = "mms/unsubscribeInit")
	public String init(String flag,HttpServletRequest request, HttpServletResponse response,Model model) {
		model.addAttribute("flag", flag);
		return "modules/mms/jmsgUnsubscribe";
	}
	
	@RequestMapping(value = "mms/unsubscribe")
	public String unsubscribe(HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes) {
		String phone = request.getParameter("phone");
		if(!BlacklistUtils.isExist(phone,1)){
			JmsgPhoneDynamic jmsgPhoneDynamic = new JmsgPhoneDynamic();
			jmsgPhoneDynamic.setPhone("86"+phone);
			jmsgPhoneDynamic.setType("1");//手动退订
			jmsgPhoneDynamicService.save(jmsgPhoneDynamic);
			BlacklistUtils.put(phone,1,1);
		}
		
		addMessage(redirectAttributes, "退订成功");
		return "redirect:"+Global.getApiPath()+"/mms/unsubscribeInit?flag=1";
	}
}
