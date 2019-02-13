/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.htmlparser.util.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.IdGen;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.security.FormAuthenticationFilter;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsData;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsDataService;

/**
 * 彩信素材Controller
 * @author zhukc
 * @version 2016-05-20
 */
@Controller
@RequestMapping(value = "${adminPath}/mms/jmsgMmsData")
public class JmsgMmsDataController extends BaseController {

	@Autowired
	private JmsgMmsDataService jmsgMmsDataService;
	
	@Autowired
	private MQUtils mQUtils;
	
	@ModelAttribute
	public JmsgMmsData get(@RequestParam(required=false) String id) {
		JmsgMmsData entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgMmsDataService.get(id);
		}
		if (entity == null){
			entity = new JmsgMmsData();
		}
		return entity;
	}
	
	@RequestMapping(value = "getContent")
	public String getContent(String id,HttpServletResponse response,HttpServletRequest request,Model model){
		String result ="";
		JmsgMmsData entity = jmsgMmsDataService.get(id);
		if(entity != null){
			boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);//手机登录
			if(mobile){
				model.addAttribute("entity", entity);
				return renderString(response, model);
			}else{
				result = entity.getContent();
				return renderString(response, result);
			}
		}else{
			return renderString(response, result);
		}
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgMmsData jmsgMmsData, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.adminFlag()){//管理员权限
			
		}else if(PowerUtils.agencyFlag()){//代理商权限
			jmsgMmsData.setCompanyId(UserUtils.getUser().getCompany().getId());
		}else {
			jmsgMmsData.setUser(UserUtils.getUser());
		}
		
		Page<JmsgMmsData> page = jmsgMmsDataService.findPage(new Page<JmsgMmsData>(request, response), jmsgMmsData); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsDataList";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:check")
	@RequestMapping(value ="checkList")
	public String checkList(JmsgMmsData jmsgMmsData, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgMmsData.setCheckStatus("9");//待审核状态
		Page<JmsgMmsData> page = jmsgMmsDataService.findPage(new Page<JmsgMmsData>(request, response), jmsgMmsData); 
		model.addAttribute("page", page);
		return "modules/mms/jmsgMmsDataCheckList";
	}
	
	@RequestMapping(value ="checkCount")
	public String checkCount(HttpServletResponse response){
		long count = jmsgMmsDataService.checkCount();
		return renderString(response, count);
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value ="checkView")
	public String checkView(String id, HttpServletRequest request, HttpServletResponse response, Model model) {
		JmsgMmsData entity = jmsgMmsDataService.findCheckInfo(id);
		model.addAttribute("jmsgMmsData", entity);
		return "modules/mms/jmsgMmsDataCheckView";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:check")
	@RequestMapping(value ="checkMms")
	public String checkMms(String ids, String status,String checkContent, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		logger.info("ids:{}, status:{}, content:{}", ids, status, checkContent);
		String[] idList = ids.split(";");
		if(StringUtils.equals(status, "true")) {
			status = "1";
		} else if(StringUtils.equals(status, "false")) {
			status = "0";
		}
		jmsgMmsDataService.updateCheckStatus(idList, status,checkContent);
		mQUtils.sendCheckMmsMQ("mmsId", "{\"mmsid\":\""+ids+"\"}");//回调彩信审核结果
		boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		if(mobile) {
			return renderString(response, "审核已生效");
		}
		addMessage(redirectAttributes, "彩信审核成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsData/checkList";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:check")
	@RequestMapping(value ="checkMmsForm")
	public String checkMmsForm(String ids,Model model) {
		model.addAttribute("ids", ids);
		return "modules/mms/jmsgMmsDataCheckForm";
	}	
	

	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value = "form")
	public String form(JmsgMmsData jmsgMmsData, Model model) {
		model.addAttribute("jmsgMmsData", jmsgMmsData);
		return "modules/mms/jmsgMmsDataForm";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value = "view")
	public String view(JmsgMmsData jmsgMmsData, Model model) {
		model.addAttribute("jmsgMmsData", jmsgMmsData);
		return "modules/mms/jmsgMmsDataView";
	}


	@RequiresPermissions("mms:jmsgMmsData:edit")
	@RequestMapping(value = "save")
	public String save(JmsgMmsData jmsgMmsData, Model model, RedirectAttributes redirectAttributes) throws ParserException, IOException {
		if (!beanValidator(model, jmsgMmsData)){
			return form(jmsgMmsData, model);
		}
		if(StringUtils.isBlank(jmsgMmsData.getMmsCode())){
			jmsgMmsData.setMmsCode(IdGen.randomBase62(6));
		}
		
		if("1".equals(UserUtils.getUser().getNoCheck())){//免审
			jmsgMmsData.setCheckStatus("8");
		}else{
			jmsgMmsData.setCheckStatus("9");//待审
		}
		
		jmsgMmsDataService.save(jmsgMmsData);
		
		//审核
		List<Dict> emails = DictUtils.getDictList("review_mms_emails");
		if(emails != null && !emails.isEmpty()) {
			StringBuffer sbfEmails = new StringBuffer();
			for(Dict dict : emails) {
				sbfEmails.append(dict.getValue()).append(",");
			}
			mQUtils.sendCreateMmsMQ(jmsgMmsData.getId(), "{\"toaddress\":\""+sbfEmails.toString()+"\",\"subject\":\""+jmsgMmsData.getMmsTitle()+"\"}");
		}
		
		addMessage(redirectAttributes, "保存彩信素材成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsData/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgMmsData jmsgMmsData, RedirectAttributes redirectAttributes) {
		jmsgMmsDataService.delete(jmsgMmsData);
		addMessage(redirectAttributes, "删除彩信素材成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsData/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value = "makeinit")
	public String make(JmsgMmsData jmsgMmsData, Model model) {
		model.addAttribute("jmsgMmsData", jmsgMmsData);
		return "modules/mms/jmsgMmsMake";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:edit")
	@RequestMapping(value = "makesave")
	public String create(JmsgMmsData jmsgMmsData,  String sorts[], 
			String txtcontent[], HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		
		String photoname[] = request.getParameterValues("photoname");
		StringBuffer mmsBody = new StringBuffer();
		for(int i = 0;i < sorts.length;i ++) {
			
			if(StringUtils.isNotBlank(photoname[i])) {
				mmsBody.append("<img src=\"").append(photoname[i]).append("\" />");
			}
			
			if(StringUtils.isNotBlank(txtcontent[i])) {
				mmsBody.append("<div>").append(txtcontent[i].replaceAll("\r\n", "<BR>")).append("</div>");
			}
		}
		
		if(StringUtils.isBlank(jmsgMmsData.getMmsCode())){
			jmsgMmsData.setMmsCode(IdGen.randomBase62(6));
		}
		
		jmsgMmsData.setContent(mmsBody.toString());
		
		if("1".equals(UserUtils.getUser().getNoCheck())){//免审
			jmsgMmsData.setCheckStatus("8");
		}else{
			jmsgMmsData.setCheckStatus("9");//待审
		}
		
		jmsgMmsDataService.save(jmsgMmsData);
		
		//审核
		List<Dict> emails = DictUtils.getDictList("review_mms_emails");
		if(emails != null && !emails.isEmpty()) {
			StringBuffer sbfEmails = new StringBuffer();
			for(Dict dict : emails) {
				sbfEmails.append(dict.getValue()).append(",");
			}
			mQUtils.sendCreateMmsMQ(jmsgMmsData.getId(), "{\"toaddress\":\""+sbfEmails.toString()+"\",\"subject\":\""+jmsgMmsData.getMmsTitle()+"\"}");
		}
		addMessage(redirectAttributes, "彩信素材保存成功");
		return "redirect:"+Global.getAdminPath()+"/mms/jmsgMmsData/makeinit?repage";
	}
	
}