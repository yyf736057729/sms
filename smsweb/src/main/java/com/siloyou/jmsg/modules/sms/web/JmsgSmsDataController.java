/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.security.FormAuthenticationFilter;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsData;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsDataService;

/**
 * 短信素材Controller
 * @author zhukc
 * @version 2016-07-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsData")
public class JmsgSmsDataController extends BaseController {

	@Autowired
	private JmsgSmsDataService jmsgSmsDataService;
	
	@ModelAttribute
	public JmsgSmsData get(@RequestParam(required=false) String id) {
		JmsgSmsData entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsDataService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsData();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsData:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsData jmsgSmsData, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		User user = UserUtils.getUser();
		
		if(PowerUtils.adminFlag()){//管理员权限
			
		}else if(PowerUtils.agencyFlag()){//分销商
			jmsgSmsData.setCompanyId(user.getCompany().getId());
		}else{//普通用户
			jmsgSmsData.setUser(user);
		}
		
		Page<JmsgSmsData> page = jmsgSmsDataService.findPage(new Page<JmsgSmsData>(request, response), jmsgSmsData); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDataList";
	}
	
	@RequiresPermissions("sms:jmsgSmsData:review")
	@RequestMapping(value ="reviewList")
	public String checkList(JmsgSmsData jmsgSmsData, HttpServletRequest request, HttpServletResponse response, Model model) {
		jmsgSmsData.setReviewStatus("9");//待审核状态
		Page<JmsgSmsData> page = jmsgSmsDataService.findPage(new Page<JmsgSmsData>(request, response), jmsgSmsData); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDataReviewList";
	}	
	
	@RequiresPermissions("sms:jmsgSmsData:review")
	@RequestMapping(value ="reviewSms")
	public String reviewSms(String ids, String status,String reviewContent, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		logger.info("ids:{}, status:{}, content:{}", ids, status, reviewContent);
		String[] idList = ids.split(";");
		if(StringUtils.equals(status, "true")) {
			status = "1";
		} else if(StringUtils.equals(status, "false")) {
			status = "0";
		}
		jmsgSmsDataService.updateReviewStatus(idList, status,reviewContent);
		boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		if(mobile) {
			return renderString(response, "审核已生效");
		}
		addMessage(redirectAttributes, "短信审核成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsData/reviewList";
	}
	
	@RequiresPermissions("sms:jmsgSmsData:review")
	@RequestMapping(value ="reviewSmsForm")
	public String checkMmsForm(String ids,Model model) {
		model.addAttribute("ids", ids);
		return "modules/sms/jmsgSmsDataReviewForm";
	}	

	@RequiresPermissions("sms:jmsgSmsData:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsData jmsgSmsData, Model model) {
		model.addAttribute("jmsgSmsData", jmsgSmsData);
		return "modules/sms/jmsgSmsDataForm";
	}

	@RequiresPermissions("sms:jmsgSmsData:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsData jmsgSmsData, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsData)){
			return form(jmsgSmsData, model);
		}
		String msg = "保存短信素材成功";
		String content = jmsgSmsData.getContent().trim();//短信素材
		
		String keywords = KeywordsUtils.keywords(content);
		if(StringUtils.isBlank(keywords)){
		
			String contentKey = HttpRequest.md5(content);
			User user = UserUtils.getUser();
			String userId = user.getId();
			
			JmsgSmsData result = jmsgSmsDataService.findJmsgSmsDataByContentKey(userId, contentKey);
			if(result != null){
				msg = "短信素材已经存在";
			}else{
				String reviewStatus="9";//待审
				Date reviewDatetime = null;
				if("1".equals(user.getNoCheck())){
					reviewStatus="8";//免审
					reviewDatetime = new Date();
				}
				
				jmsgSmsDataService.save(content, "sms", userId, contentKey, reviewStatus, reviewDatetime, "1", "1");
			}
		}else{
			msg = "发送内容包含敏感词["+keywords+"]";
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsData/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsData:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsData jmsgSmsData, RedirectAttributes redirectAttributes) {
		jmsgSmsDataService.delete(jmsgSmsData);
		addMessage(redirectAttributes, "删除短信素材成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsData/?repage";
	}
	
	@RequiresPermissions("mms:jmsgMmsData:view")
	@RequestMapping(value ="reviewView")
	public String checkView(String id, HttpServletRequest request, HttpServletResponse response, Model model) {
		JmsgSmsData entity = jmsgSmsDataService.get(id);
		model.addAttribute("jmsgSmsData", entity);
		return "modules/sms/jmsgSmsDataReviewView";
	}

}