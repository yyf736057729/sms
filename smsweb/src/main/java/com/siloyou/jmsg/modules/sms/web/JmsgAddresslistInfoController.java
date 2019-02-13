/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.utils.excel.ImportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgAddresslistInfo;
import com.siloyou.jmsg.modules.sms.service.JmsgAddresslistInfoService;

/**
 * 联系人列表Controller
 * @author zhukc
 * @version 2017-04-01
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgAddresslistInfo")
public class JmsgAddresslistInfoController extends BaseController {

	@Autowired
	private JmsgAddresslistInfoService jmsgAddresslistInfoService;
	
	@ModelAttribute
	public JmsgAddresslistInfo get(@RequestParam(required=false) String id) {
		JmsgAddresslistInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgAddresslistInfoService.get(id);
		}
		if (entity == null){
			entity = new JmsgAddresslistInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgAddresslistInfo:view")
	@RequestMapping(value = "index")
	public String index(JmsgAddresslistInfo jmsgAddresslistInfo, Model model) {
		return "modules/sms/jmsgAddresslistInfoIndex";
	}
	
	@RequiresPermissions("sms:jmsgAddresslistInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgAddresslistInfo jmsgAddresslistInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		powerEntity(jmsgAddresslistInfo);
		try {
			if (null != jmsgAddresslistInfo.getGroup() && StringUtils.isNotBlank(jmsgAddresslistInfo.getGroup().getName()))
			{
				jmsgAddresslistInfo.getGroup().setName(URLDecoder.decode(jmsgAddresslistInfo.getGroup().getName(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			addMessage(model, e.toString());
		}
		Page<JmsgAddresslistInfo> page = jmsgAddresslistInfoService.findPage(new Page<JmsgAddresslistInfo>(request, response), jmsgAddresslistInfo); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgAddresslistInfoList";
	}
	
	private void powerEntity(JmsgAddresslistInfo jmsgAddresslistInfo){
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			jmsgAddresslistInfo.setUserId(user.getId());
		}	
	}

	@RequiresPermissions("sms:jmsgAddresslistInfo:view")
	@RequestMapping(value = "form")
	public String form(JmsgAddresslistInfo jmsgAddresslistInfo, Model model) {
		model.addAttribute("jmsgAddresslistInfo", jmsgAddresslistInfo);
		return "modules/sms/jmsgAddresslistInfoForm";
	}
	
	@RequiresPermissions("sms:jmsgAddresslistInfo:edit")
	@RequestMapping(value = "batchForm")
	public String batchForm(JmsgAddresslistInfo jmsgAddresslistInfo, Model model) {
		model.addAttribute("jmsgAddresslistInfo", jmsgAddresslistInfo);
		return "modules/sms/jmsgAddresslistInfoBatch";
	}
	
	/**
	 * 下载导入用户数据模板
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sms:jmsgAddresslistInfo:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "通讯录数据导入模板.xlsx";
    		List<JmsgAddresslistInfo> list = Lists.newArrayList();
    		JmsgAddresslistInfo param = new JmsgAddresslistInfo();
    		param.setContacts("小明");
    		param.setPhone("13800000000");
    		param.setEmail("13800000000@139.com");
    		param.setBirthday("1990-03-05");
    		param.setRemarks("泛圣科技");
    		list.add(param);
    		new ExportExcel("通讯录数据", JmsgAddresslistInfo.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sms/jmsgAddresslistInfo/batchForm?repage";
    }
	
	/**
	 * 导入用户数据
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sms:jmsgAddresslistInfo:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, String groupId,RedirectAttributes redirectAttributes) {
		try {
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<JmsgAddresslistInfo> list = ei.getDataList(JmsgAddresslistInfo.class);
			jmsgAddresslistInfoService.batchSave(list, groupId);
			addMessage(redirectAttributes, "已成功导入 "+list.size()+" 条用户");
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sms/jmsgAddresslistInfo/batchForm?repage";
    }



	@RequiresPermissions("sms:jmsgAddresslistInfo:edit")
	@RequestMapping(value = "save")
	public String save(JmsgAddresslistInfo jmsgAddresslistInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgAddresslistInfo)){
			return form(jmsgAddresslistInfo, model);
		}
		jmsgAddresslistInfoService.save(jmsgAddresslistInfo);
		addMessage(redirectAttributes, "保存联系人成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgAddresslistInfo/?repage";
	}
	
	@RequiresPermissions("sms:jmsgAddresslistInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgAddresslistInfo jmsgAddresslistInfo, RedirectAttributes redirectAttributes) {
		jmsgAddresslistInfoService.delete(jmsgAddresslistInfo);
		addMessage(redirectAttributes, "删除联系人成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgAddresslistInfo/?repage";
	}
	
	@RequiresPermissions("sms:jmsgAddresslistInfo:edit")
	@RequestMapping(value = "batchDelete")
	public String batchDelete(String ids, RedirectAttributes redirectAttributes) {
		String[] array = ids.split(";");
		JmsgAddresslistInfo param;
		if(array.length > 0){
			for (String id : array) {
				param = new JmsgAddresslistInfo();
				param.setId(id);
				jmsgAddresslistInfoService.delete(param);
			}
		}
		addMessage(redirectAttributes, "删除联系人成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgAddresslistInfo/?repage";
	}

	@RequiresPermissions("sms:jmsgAddresslistInfo:view")
	@RequestMapping(value = "findList")
	public String findList(JmsgAddresslistInfo jmsgAddresslistInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		powerEntity(jmsgAddresslistInfo);
		List<JmsgAddresslistInfo> list = jmsgAddresslistInfoService.findList(jmsgAddresslistInfo);
		return renderString(response, list);
	}
}