/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgAddresslistGroup;
import com.siloyou.jmsg.modules.sms.service.JmsgAddresslistGroupService;
import com.siloyou.jmsg.modules.sms.service.JmsgAddresslistInfoService;

/**
 * 群组管理Controller
 * @author zhukc
 * @version 2017-04-01
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgAddresslistGroup")
public class JmsgAddresslistGroupController extends BaseController {

	@Autowired
	private JmsgAddresslistGroupService jmsgAddresslistGroupService;
	
	@Autowired
	private JmsgAddresslistInfoService jmsgAddresslistInfoService;
	
	public JmsgAddresslistGroup get(String id) {
		JmsgAddresslistGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgAddresslistGroupService.get(id);
		}
		if (entity == null){
			entity = new JmsgAddresslistGroup();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgAddresslistGroup:view")
	@RequestMapping(value = "index")
	public String index(JmsgAddresslistGroup jmsgAddresslistGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("pid", "0");
		return "modules/sms/jmsgAddresslistGroupIndex";
	}
	
	@RequiresPermissions("sms:jmsgAddresslistGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgAddresslistGroup jmsgAddresslistGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		powerEntity(jmsgAddresslistGroup);//植入权限
		List<JmsgAddresslistGroup> list = jmsgAddresslistGroupService.findList(jmsgAddresslistGroup); 
		model.addAttribute("list", list);
		return "modules/sms/jmsgAddresslistGroupList";
	}

	private void powerEntity(JmsgAddresslistGroup jmsgAddresslistGroup) {
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			jmsgAddresslistGroup.setUserId(user.getId());
		}
	}

	@RequiresPermissions("sms:jmsgAddresslistGroup:view")
	@RequestMapping(value = "form")
	public String form(JmsgAddresslistGroup jmsgAddresslistGroup, Model model) {
		if(jmsgAddresslistGroup != null && StringUtils.isNotBlank(jmsgAddresslistGroup.getId())){
			jmsgAddresslistGroup = get(jmsgAddresslistGroup.getId());
		}
		
		if(jmsgAddresslistGroup.getParent() != null && StringUtils.isNotBlank(jmsgAddresslistGroup.getParent().getId())){
			jmsgAddresslistGroup.setParent(get(jmsgAddresslistGroup.getParent().getId()));
		}
		if(StringUtils.isBlank(jmsgAddresslistGroup.getId())){
			jmsgAddresslistGroup.setSort("30");
		}
		model.addAttribute("jmsgAddresslistGroup", jmsgAddresslistGroup);
		return "modules/sms/jmsgAddresslistGroupForm";
	}

	@RequiresPermissions("sms:jmsgAddresslistGroup:edit")
	@RequestMapping(value = "save")
	public String save(JmsgAddresslistGroup jmsgAddresslistGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgAddresslistGroup)){
			return form(jmsgAddresslistGroup, model);
		}
		String name = jmsgAddresslistGroup.getName();
		String remarks = jmsgAddresslistGroup.getRemarks();
		JmsgAddresslistGroup parent = jmsgAddresslistGroup.getParent();
		String sort = jmsgAddresslistGroup.getSort();
		if(jmsgAddresslistGroup != null && StringUtils.isNotBlank(jmsgAddresslistGroup.getId())){
			jmsgAddresslistGroup = get(jmsgAddresslistGroup.getId());
			jmsgAddresslistGroup.setName(name);
			jmsgAddresslistGroup.setRemarks(remarks);
			jmsgAddresslistGroup.setParent(parent);
			jmsgAddresslistGroup.setSort(sort);
		}
		jmsgAddresslistGroupService.save(jmsgAddresslistGroup);
		addMessage(redirectAttributes, "保存群组成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgAddresslistGroup/list?id="+jmsgAddresslistGroup.getId();
	}
	
	@RequiresPermissions("sms:jmsgAddresslistGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgAddresslistGroup jmsgAddresslistGroup, RedirectAttributes redirectAttributes) {
		jmsgAddresslistGroupService.delete(jmsgAddresslistGroup);
		addMessage(redirectAttributes, "删除群组成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgAddresslistGroup/?repage";
	}
	
	@RequiresPermissions("sms:jmsgAddresslistGroup:edit")
	@RequestMapping(value = "deleteAddressInfo")
	public String deleteAddressInfo(JmsgAddresslistGroup jmsgAddresslistGroup, RedirectAttributes redirectAttributes) {
		jmsgAddresslistInfoService.deleteByGroup(jmsgAddresslistGroup.getId());
		addMessage(redirectAttributes, "删除群组内通讯录信息成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgAddresslistGroup/?repage";
	}
	
	/**
	 * 获取群组JSON数据。
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String extId,HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		JmsgAddresslistGroup param = new JmsgAddresslistGroup();
		powerEntity(param);
		List<JmsgAddresslistGroup> list = jmsgAddresslistGroupService.findList(param);
		for (int i=0; i<list.size(); i++){
			JmsgAddresslistGroup e = list.get(i);
			if ((StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1))){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent().getId());
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}


}