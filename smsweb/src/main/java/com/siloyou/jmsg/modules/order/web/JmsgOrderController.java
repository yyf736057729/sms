/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.order.web;

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
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.modules.order.entity.JmsgOrder;
import com.siloyou.jmsg.modules.order.service.JmsgOrderService;

/**
 * 订单信息Controller
 * @author zhukc
 * @version 2016-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/order/jmsgOrder")
public class JmsgOrderController extends BaseController {

	@Autowired
	private JmsgOrderService jmsgOrderService;
	
	@ModelAttribute
	public JmsgOrder get(@RequestParam(required=false) String id) {
		JmsgOrder entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgOrderService.get(id);
		}
		if (entity == null){
			entity = new JmsgOrder();
		}
		return entity;
	}
	
	@RequiresPermissions("order:jmsgOrder:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgOrder jmsgOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!PowerUtils.adminFlag()){//不是管理员权限
			jmsgOrder.setUser(UserUtils.getUser());
		}
		Page<JmsgOrder> page = jmsgOrderService.findPage(new Page<JmsgOrder>(request, response), jmsgOrder); 
		model.addAttribute("page", page);
		return "modules/order/jmsgOrderList";
	}

	@RequiresPermissions("order:jmsgOrder:view")
	@RequestMapping(value = "form")
	public String form(JmsgOrder jmsgOrder, Model model) {
		model.addAttribute("jmsgOrder", jmsgOrder);
		return "modules/order/jmsgOrderForm";
	}

	@RequiresPermissions("order:jmsgOrder:edit")
	@RequestMapping(value = "save")
	public String save(JmsgOrder jmsgOrder, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgOrder)){
			return form(jmsgOrder, model);
		}
		jmsgOrderService.save(jmsgOrder);
		addMessage(redirectAttributes, "保存订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/order/jmsgOrder/?repage";
	}
	
	@RequiresPermissions("order:jmsgOrder:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgOrder jmsgOrder, RedirectAttributes redirectAttributes) {
		jmsgOrderService.delete(jmsgOrder);
		addMessage(redirectAttributes, "删除订单信息成功");
		return "redirect:"+Global.getAdminPath()+"/order/jmsgOrder/?repage";
	}

}