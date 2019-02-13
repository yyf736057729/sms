/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.siloyou.jmsg.common.utils.MQUtils;
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
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayGroup;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayGroupService;

/**
 * 通道分组Controller
 * @author zhukc
 * @version 2016-07-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgGatewayGroup")
public class JmsgGatewayGroupController extends BaseController {

	@Autowired
	private JmsgGatewayGroupService jmsgGatewayGroupService;
	
	@ModelAttribute
	public JmsgGatewayGroup get(@RequestParam(required=false) String id) {
		JmsgGatewayGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgGatewayGroupService.get(id);
		}
		if (entity == null){
			entity = new JmsgGatewayGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgGatewayGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgGatewayGroup jmsgGatewayGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgGatewayGroup.getPage().setOrderBy("a.id DESC");
		Page<JmsgGatewayGroup> page = jmsgGatewayGroupService.findPage(new Page<JmsgGatewayGroup>(request, response), jmsgGatewayGroup); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgGatewayGroupList";
	}

	@RequiresPermissions("sms:jmsgGatewayGroup:view")
	@RequestMapping(value = "form")
	public String form(JmsgGatewayGroup jmsgGatewayGroup, Model model) {
		model.addAttribute("jmsgGatewayGroup", jmsgGatewayGroup);
		return "modules/sms/jmsgGatewayGroupForm";
	}

	@RequiresPermissions("sms:jmsgGatewayGroup:edit")
	@RequestMapping(value = "save")
	public String save(JmsgGatewayGroup jmsgGatewayGroup, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, jmsgGatewayGroup)){
//			return form(jmsgGatewayGroup, model);
//		}
////		JmsgGatewayGroup entity = jmsgGatewayGroupService.getByParam(jmsgGatewayGroup);
////
////		if (entity != null){
////			addMessage(model, "操作失败，通道分组关系已经存在");
////			return form(jmsgGatewayGroup, model);
////		}
//		jmsgGatewayGroupService.save(jmsgGatewayGroup);
//		addMessage(redirectAttributes, "保存通道分组成功");



		//重写
		String [] arr =jmsgGatewayGroup.getId().split(",");
		for(int i =0 ;i<arr.length;i++){
			if(!arr[i].equals("")){
				JmsgGatewayGroup jmsgGatewayGroup1 = new JmsgGatewayGroup();
				jmsgGatewayGroup1.setGroupId(jmsgGatewayGroup.getGroupId());
				jmsgGatewayGroup1.setPhoneType(jmsgGatewayGroup.getPhoneType());
				jmsgGatewayGroup1.setProvinceId(arr[i]);
				jmsgGatewayGroup1.setGatewayId(jmsgGatewayGroup.getGatewayId());
				jmsgGatewayGroup1.setLevel(jmsgGatewayGroup.getLevel());
				jmsgGatewayGroup1.setDelFlag(jmsgGatewayGroup.getDelFlag());
//				jmsgGatewayGroup1.setId(arr[i]);

				if (!beanValidator(model, jmsgGatewayGroup1)){
					break;
				}

				jmsgGatewayGroupService.save(jmsgGatewayGroup1);
			}
		}
		addMessage(redirectAttributes, "保存通道分组成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayGroup/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGatewayGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgGatewayGroup jmsgGatewayGroup, RedirectAttributes redirectAttributes) {
		jmsgGatewayGroupService.delete(jmsgGatewayGroup);
		addMessage(redirectAttributes, "删除通道分组成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayGroup/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGatewayGroup:edit")
	@RequestMapping(value = "batchDelete")
	public String batchDelete(JmsgGatewayGroup jmsgGatewayGroup, String ids,RedirectAttributes redirectAttributes) {
		String[] array = ids.split(";"); 
		for (String param : array) {
			String[] delParam = param.split("_");
			if(delParam.length !=5)continue;
			jmsgGatewayGroupService.delete(delParam[0],delParam[1],delParam[2],delParam[3],delParam[4]);
			
		}
		addMessage(redirectAttributes, "删除通道分组成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayGroup/?repage";
	}
	@Autowired
	private MQUtils mQUtils;
	@RequiresPermissions("sms:jmsgGatewayGroup:edit")
	@RequestMapping(value = "onCache")
	public String onCache(RedirectAttributes redirectAttributes) {
		String str ="onCache";
		String s = mQUtils.sendSmsMT("onCacheJmsgGatewayGroup", "onCache", "onCache", str.getBytes());
		if(s.equals("-1")){
			addMessage(redirectAttributes, "缓存失败");
		}else {
			addMessage(redirectAttributes, "缓存成功！");
		}
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayGroup";
	}
}