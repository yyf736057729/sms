/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
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
import com.google.common.collect.Maps;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.mapper.JsonMapper;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.sanerzone.common.support.utils.ValidatorUtils;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.FileUtils;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExcelReaderUtil;
import com.siloyou.core.common.utils.excel.IRowReader;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.MsgIdUtits;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewaySign;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDot;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewaySignService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSendService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;
import com.siloyou.jmsg.modules.sms.service.excel.SmsDotReader;
import com.siloyou.jmsg.modules.sms.service.excel.SmsDotTmplReader;
/**
 * 短信任务发送Controller
 * @author zhukc
 * @version 2016-07-20
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsTask")
public class JmsgSmsTaskController extends BaseController {

	@Autowired
	private JmsgSmsTaskService jmsgSmsTaskService;
	
	@Autowired
	private JmsgSmsSendService jmsgSmsSendService;
	
	@Autowired
	private JmsgGatewaySignService jmsgGatewaySignService;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@ModelAttribute
	public JmsgSmsTask get(@RequestParam(required=false) String id) {
		JmsgSmsTask entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsTaskService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsTask();
		}
		return entity;
	}
	
	
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value ="reviewList")
	public String checkList(JmsgSmsTask jmsgSmsTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userIdText = request.getParameter("userIdText");
		String userId = request.getParameter("userId");

		String userName = request.getParameter("userName");
		try{
			if(null!=userName){
				userName = java.net.URLDecoder.decode(userName,"UTF-8");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		String smsContent = request.getParameter("smsContent");
		try{
			if(null!=smsContent){
				smsContent = java.net.URLDecoder.decode(smsContent,"UTF-8");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		String createDatetimeQ = request.getParameter("createDatetimeQ");
		String createDatetimeZ = request.getParameter("createDatetimeZ");
		jmsgSmsTask.setStatus("-1");//待审核状态
		if(jmsgSmsTask.getSign()!=null && !jmsgSmsTask.getSign().equals("")){//签名
			String sign = jmsgSmsTask.getSign();
			model.addAttribute("signs", sign);
			jmsgSmsTask.setSign("【"+sign+"】");
		}
		Page<JmsgSmsTask> page = jmsgSmsTaskService.findPage(new Page<JmsgSmsTask>(request, response), jmsgSmsTask);
		model.addAttribute("page", page);
		model.addAttribute("userIdText", userIdText);
		model.addAttribute("userId", userId);
		if(null!=userName){
			model.addAttribute("userName", userName);
		}else{
			model.addAttribute("userName", "");
		}
		if(null!=jmsgSmsTask.getUser()){
			model.addAttribute("userId", jmsgSmsTask.getUser().getId());
			model.addAttribute("userName", jmsgSmsTask.getUser().getName());
		}
		model.addAttribute("smsContent", smsContent);
		model.addAttribute("createDatetimeQ", createDatetimeQ);
		model.addAttribute("createDatetimeZ", createDatetimeZ);
		return "modules/sms/jmsgSmsDataReviewList";
	}

	/**
	 * @Description: 一键审核
	 * @param: jmsgSmsTask
	 * @param: request
	 * @param: response
	 * @param: model
	 * @return: String
	 * @author: zhanghui
	 * @Date: 2019-01-10
	 */
	@RequestMapping(value ="onekeyReview")
	public String onekeyReview(JmsgSmsTask jmsgSmsTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		String status = request.getParameter("status");
		String userIdText = request.getParameter("userIdText");
		String userId = request.getParameter("userId");
		String userName = request.getParameter("userName");

		String sign = request.getParameter("sign");
		if(sign.contains(",")){
			sign = sign.split(",")[0];
		}

		String smsContent = request.getParameter("smsContent");
		String createDatetimeQ = request.getParameter("createDatetimeQ");
		String createDatetimeZ = request.getParameter("createDatetimeZ");
		String onekeyReviewRemarks = request.getParameter("onekeyReviewRemarks");

		jmsgSmsTask.setStatus(status);

		jmsgSmsTask.setUserIdText(userIdText);

		if(!userId.equals("")){
			User u = new User();
			u.setId(userId);
			jmsgSmsTask.setUser(u);
		}

		jmsgSmsTask.setSign(sign);

		if(!smsContent.equals("")){
			jmsgSmsTask.setSmsContent(smsContent);
		}else{
			jmsgSmsTask.setSmsContent("");//SmsContent属性自动变成了一个逗号, 所以这里需要处理成空字符串
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			jmsgSmsTask.setCreateDatetimeQ(sdf.parse(createDatetimeQ));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			jmsgSmsTask.setCreateDatetimeZ(sdf.parse(createDatetimeZ));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if(!onekeyReviewRemarks.equals("")) {
			jmsgSmsTask.setReviewRemarks(onekeyReviewRemarks);
		}

		User user = UserUtils.getUser();
		jmsgSmsTask.setUpdateBy(user);
		jmsgSmsTask.setReviewUserId(user.getId());

		jmsgSmsTaskService.onekeyReview(jmsgSmsTask);//审核通过/审核不通过, 都是这个方法

		try {
			userName = java.net.URLEncoder.encode(userName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			sign = java.net.URLEncoder.encode(sign,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			smsContent = java.net.URLEncoder.encode(smsContent,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String param = "";
		param = param + "?userIdText=" + userIdText;
		param = param + "&userId=" + userId;
		param = param + "&userName=" + userName;
		param = param + "&sign=" + sign;
		param = param + "&smsContent=" + smsContent;
		param = param + "&createDatetimeQ=" + createDatetimeQ;
		param = param + "&createDatetimeZ=" + createDatetimeZ;
		return "redirect:" + adminPath + "/sms/jmsgSmsTask/reviewList" + param;
	}


	//待审核短信条数
	@RequestMapping(value ="checkCount")
	public String checkCount(JmsgSmsTask jmsgSmsTask,HttpServletResponse response){
		Map<String,String> map = Maps.newHashMap();
		String count = "0";
		jmsgSmsTask.setStatus("-1");//待审核状态
		List<JmsgSmsTask> list = jmsgSmsTaskService.findList(jmsgSmsTask);
		if(list != null && list.size() > 0){
			count = String.valueOf(list.size());
		}
		map.put("count", count);
		return renderString(response, map);
	}
	
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value ="reviewSms")
	public String reviewSms(String ids,String status,String reviewRemarks,HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes) {
		String[] array = ids.split(";");
		if(array != null && array.length > 0){
			JmsgSmsTask param;
			User user = UserUtils.getUser();
			
			try {
				if(StringUtils.isNotBlank(reviewRemarks)){
					reviewRemarks = URLDecoder.decode(reviewRemarks, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			for (String id : array) {
				param = new JmsgSmsTask();
				param.setId(id);
				param.setStatus(status);
				param.setReviewRemarks(reviewRemarks);
				param.setUser(user);
				jmsgSmsTaskService.reviewSmsContent(param);
			}
		}
		addMessage(redirectAttributes, "审核成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/reviewList";
	}
	
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value ="updateSmsContent")
	public String updateSmsContent(JmsgSmsTask param,String content, HttpServletRequest request, HttpServletResponse response,RedirectAttributes redirectAttributes) {
		param.setStatus("1");//待发送
		param.setUser(UserUtils.getUser());
		try {
			if(StringUtils.isNotBlank(content)){
				content = URLDecoder.decode(content, "UTF-8");
				param.setSmsContent(content);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(param.getSendDatetime() == null){
			param.setSendDatetime(new Date());
		}
		jmsgSmsTaskService.updateSmsContent(param);
		addMessage(redirectAttributes, "审核成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/reviewList";
	}
	
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value = "smsGroupInit")
	public String smsGroupInit(JmsgSmsTask jmsgSmsTask, Model model){
		
		//判断是否禁用
		String usedFlag = jmsgAccountService.findUsedFlag(UserUtils.getUser().getId());
		if(!"1".equals(usedFlag)){
			addMessage(model, "账户禁用");
		}
		List<Dict> sign = new ArrayList<Dict>();
		Dict dict = new Dict();
	    // 获取用户的签名
		JmsgGatewaySign jmsgGatewaySign = new JmsgGatewaySign();
		jmsgGatewaySign.setUser(UserUtils.getUser());
		List<JmsgGatewaySign> list = jmsgGatewaySignService.findList(jmsgGatewaySign);
		
		Map<String, String> signMap = new HashMap<String, String>();
	    for (JmsgGatewaySign param : list)
	    {
	    	if (!signMap.containsKey(param.getSign()))
	    	{
	    		dict = new Dict();	
		    	dict.setLabel("【" + param.getSign() + "】");
			    dict.setValue("【" + param.getSign() + "】");
			    sign.add(dict);
			    
			    signMap.put(param.getSign(), param.getSign());
	    	}
	    }
        jmsgSmsTask.setUserSign(sign);
		model.addAttribute("jmsgSmsTask", jmsgSmsTask);
		model.addAttribute("usedFlag", usedFlag);
		return "modules/sms/jmsgSmsGroupInit";
	}
	
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsTask jmsgSmsTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.adminFlag()){//管理员

		}else if(PowerUtils.agencyFlag()){
			jmsgSmsTask.setCompanyId(UserUtils.getUser().getCompany().getId());//公司ID
		}else{
			jmsgSmsTask.setCreateUserId(UserUtils.getUser().getId());
		}
		
		Page<JmsgSmsTask> page = jmsgSmsTaskService.findPage(new Page<JmsgSmsTask>(request, response), jmsgSmsTask); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsTaskList";
	}

	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsTask jmsgSmsTask, Model model) {
		model.addAttribute("jmsgSmsTask", jmsgSmsTask);
		return "modules/sms/jmsgSmsTaskForm";
	}

	@RequiresPermissions("sms:jmsgSmsTask:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsTask jmsgSmsTask, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsTask)){
			return form(jmsgSmsTask, model);
		}

		User user = UserUtils.getUser();
		boolean runFlag = true;
		String msg = "";
		String usedFlag = jmsgAccountService.findUsedFlag(user.getId());//判断账号是否禁用
		if(!"1".equals(usedFlag)){//账号禁用
			addMessage(redirectAttributes, "账户禁用");
			return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsGroupInit";
		}
		
		if(jmsgSmsTask.getSendDatetime() == null){//判断时间
			jmsgSmsTask.setSendDatetime(new Date());
		}else{
			double timeD = DateUtils.getDistanceOfTwoDate(new Date(), jmsgSmsTask.getSendDatetime());
			if(timeD > 15){
				addMessage(redirectAttributes, "发送时间有误,短信发送只支持15天内");
				return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsGroupInit";
			}else if(timeD < 0){
				jmsgSmsTask.setSendDatetime(new Date());
			}
		}
		String content = StringEscapeUtils.unescapeHtml4(jmsgSmsTask.getContent().trim());//发送内容
		content = SignUtils.formatContent(content);
		jmsgSmsTask.setContent(content);
		
		List<String> phoneList = Lists.newArrayList();
        
		int errorCount = 0;
        if (StringUtils.isNotBlank(jmsgSmsTask.getPhones()))
        {
            String[] phones = jmsgSmsTask.getPhones().split("\r\n");
            
            for (String string : phones) 
            {
            	if(ValidatorUtils.isMobile(string)){//验证是不是手机号
            		phoneList.add(string);
            	}else{
            		errorCount++;
            	}
            }
        }
		
		int payCount = jmsgSmsTaskService.findPayCount(content);
		
		int count = phoneList.size()*payCount;//支付总条数
		if(count >0){
			String key = AccountCacheUtils.getAmountKey("sms", user.getId());
			if(JedisClusterUtils.decrBy(key, count) < 0){
				JedisClusterUtils.incrBy(key, count);
				runFlag = false;
				msg = "账户余额不足";
			}
		}else{
			runFlag = false;
			msg = "号码为空不符合要求";
		}



		if(runFlag){//执行
			
			String noCheck = user.getNoCheck();
			if("2".equals(noCheck)){//自动审核
				if(user.getReviewCount() >= count){//审核条数大于发送条数
					noCheck = "1";//免审
				}else{
					noCheck="0";//必审
				}
			}
			String taskId = MsgIdUtits.msgId("T");//生成任务ID
    		jmsgSmsTask.setId(taskId);
    		jmsgSmsTask.setSmsContent(content);
    		jmsgSmsTask.setSendCount(phoneList.size());
    		jmsgSmsTask.setStatus("0".equals(noCheck) ? "-1" : "1");

			//无论是否免审, 发送短信 触发关键字 全局 进入审核
			String keywords = KeywordsUtils.keywords(content.trim());
			if (StringUtils.isNotBlank(keywords)){
				jmsgSmsTask.setStatus("-1");
			}

			//无论是否免审, 发送短信 触发关键字 用户关键词 进入审核
			if("1".equals(user.getFilterWordFlag())){//过滤敏感词
				if (!KeywordsUtils.exits(user.getKeyword(), content)) {

				}else{
					jmsgSmsTask.setStatus("-1");
				}
			}

    		jmsgSmsTask.setTaskType("0");//普通
    		jmsgSmsTask.setCreateBy(user);
    		
			msg = jmsgSmsTaskService.createSmsTask(jmsgSmsTask, phoneList,errorCount,payCount);
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsGroupInit";
	}
	
	
	@RequiresPermissions("sms:jmsgSmsTask:edit")
	@RequestMapping(value = "dotSave")
	public String dotSave(JmsgSmsTask jmsgSmsTask, MultipartFile phoneFile, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsTask)){
			return form(jmsgSmsTask, model);
		}

		User user = UserUtils.getUser();
		String usedFlag = jmsgAccountService.findUsedFlag(user.getId());
		if(!"1".equals(usedFlag)){
			addMessage(redirectAttributes, "账户禁用");
			return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsDotInit";
		}
		
		String msg = "";
		if(jmsgSmsTask.getSendDatetime() == null){//判断时间
			jmsgSmsTask.setSendDatetime(new Date());
		}else{
			double timeD = DateUtils.getDistanceOfTwoDate(new Date(), jmsgSmsTask.getSendDatetime());
			if(timeD > 15){
				addMessage(redirectAttributes, "发送时间有误,短信发送只支持15天内");
				return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsDotInit";
			}else if(timeD < 0){
				jmsgSmsTask.setSendDatetime(new Date());
			}
		}
		
		
		String noCheck = user.getNoCheck();
		if("2".equals(noCheck)){//自动审核
			if(user.getReviewCount() >= jmsgSmsTask.getSendCount()){//审核条数大于发送条数
				noCheck = "1";//免审
			}else{
				noCheck="0";//必审
			}
		}
		String taskId = MsgIdUtits.msgId("T");//生成任务ID
		jmsgSmsTask.setId(taskId);
		jmsgSmsTask.setSmsContent("定制短信,普通点对点");
		jmsgSmsTask.setStatus("0".equals(noCheck) ? "-1" : "1");
		jmsgSmsTask.setTaskType("1");//点对点
		jmsgSmsTask.setCreateBy(user);
		
		try {
			msg = jmsgSmsTaskService.createSmsTaskDot(jmsgSmsTask);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "短信接收号码导入失败";
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsDotInit";
	}
	
	
	@RequiresPermissions("sms:jmsgSmsTask:edit")
	@RequestMapping(value = "dotTmplSave")
	public String dotTmplSave(JmsgSmsTask jmsgSmsTask,Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsTask)){
			return form(jmsgSmsTask, model);
		}

		User user = UserUtils.getUser();
		String usedFlag = jmsgAccountService.findUsedFlag(user.getId());
		if(!"1".equals(usedFlag)){
			addMessage(redirectAttributes, "账户禁用");
			return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsDotTmplInit";
		}
		String msg = "";
		if(jmsgSmsTask.getSendDatetime() == null){//判断时间
			jmsgSmsTask.setSendDatetime(new Date());
		}else{
			double timeD = DateUtils.getDistanceOfTwoDate(new Date(), jmsgSmsTask.getSendDatetime());
			if(timeD > 15){
				addMessage(redirectAttributes, "发送时间有误,短信发送只支持15天内");
				return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsDotTmplInit";
			}else if(timeD < 0){
				jmsgSmsTask.setSendDatetime(new Date());
			}
		}
		
		
		String noCheck = user.getNoCheck();
		if("2".equals(noCheck)){//自动审核
			if(user.getReviewCount() >= jmsgSmsTask.getSendCount()){//审核条数大于发送条数
				noCheck = "1";//免审
			}else{
				noCheck="0";//必审
			}
		}
		String taskId = MsgIdUtits.msgId("T");//生成任务ID
		jmsgSmsTask.setId(taskId);
		jmsgSmsTask.setSmsContent("定制短信,excel模板");
		jmsgSmsTask.setStatus("0".equals(noCheck) ? "-1" : "1");
		jmsgSmsTask.setTaskType("2");//模板点对点
		jmsgSmsTask.setCreateBy(user);
		
		try {
			msg = jmsgSmsTaskService.createSmsTaskDot(jmsgSmsTask);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "短信接收号码导入失败";
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/smsDotTmplInit";
	}
	

	/**
	 * 保存短信素材
	 * 
	 * @param jmsgSmsTask
	 * @param user
	 * @param content
	 * @param phoneList
	 * @param payCount
	 * @param count
	 * @return
	 */
	private String saveSmsData(JmsgSmsTask jmsgSmsTask, User user, String content, Set<String> phoneList, int payCount,
			int count,boolean timeFlag) {
		String msg;
		String userId = user.getId();
		String sign = SignUtils.get(content);
		String noCheck = user.getNoCheck();
		if("2".equals(noCheck)){//自动审核
			if(user.getReviewCount() >= count){//审核条数大于发送条数
				noCheck = "1";//免审
			}else{
				noCheck="0";//收到审核
			}
		}
		
		Map<String,String> map = jmsgSmsTaskService.queryDataId(userId, content, noCheck, "sms");//获取短信素材ID
		String dataId = map.get("dataId");
		if(StringUtils.isBlank(dataId)){
			msg = "短信素材正在审核中，请稍后操作";
		}else{
			msg = jmsgSmsTaskService.save(phoneList, userId, dataId, map.get("reviewStatus"), content, jmsgSmsTask.getSendDatetime(),sign,payCount,timeFlag);
		}
		
		return msg;
	}
	
	@RequiresPermissions("sms:jmsgSmsTask:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsTask jmsgSmsTask, RedirectAttributes redirectAttributes) {
		jmsgSmsTaskService.delete(jmsgSmsTask);
		addMessage(redirectAttributes, "删除短信任务发送成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsTask:edit")
	@RequestMapping(value = "updateStatus")
	public String updateStatus(JmsgSmsTask jmsgSmsTask, Model model, RedirectAttributes redirectAttributes){
		String msg = "";
		boolean flag = true;
		String status = jmsgSmsTask.getStatus();
		JmsgSmsTask entity = jmsgSmsTaskService.get(jmsgSmsTask.getId());
		
		if("3".equals(entity.getStatus())){
			flag = false;
			msg = "短信已经发送完成!";
		}else if("9".equals(entity.getStatus())){
			flag = false;
			msg = "已经停止发送短信";
		}else{
			if("9".equals(status)){
				msg = "停止发送短信成功!";
			}else if("5".equals(status)){
				msg = "暂停发送短信成功!";
			}else if("1".equals(status)){
				msg = "继续发送短信成功!";
			}
		}
			
		if(flag)jmsgSmsTaskService.updateStatus(jmsgSmsTask);
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsTask/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value = "phoneView")
	public String phoneView(String taskId, Model model) {
		JmsgSmsTask entity = jmsgSmsTaskService.get(taskId);
		StringBuffer result = new StringBuffer();
		if (null != entity)
		{
			JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
			jmsgSmsSend.setTableName(getTableName(entity.getSendDatetime()));
			jmsgSmsSend.setTaskId(taskId);
			//List<String> list = jmsgSmsSendService.findPhone(taskId);
			List<String> list = jmsgSmsSendService.findPhoneV2(jmsgSmsSend);
			for (String phone : list) {
				result = result.append(phone);
			}
		}
		
		model.addAttribute("phone", result.toString());
		return "modules/mms/jmsgMmsTaskPhoneView";
	}

	/**
	 * 校验短信内容是否包含关键字
	 * @param content
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequestMapping(value = "checkKeywords")
	public String checkKeywords(String content, HttpServletRequest request, HttpServletResponse response, Model model) {
	    if (StringUtils.isNotBlank(content)) {
            try {
                content = URLDecoder.decode(content, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
	    
	    String result = KeywordsUtils.keywords(content.trim());

		User user = UserUtils.getUser();

		if (!KeywordsUtils.exits(user.getKeyword(), content.trim())) {

		}else{
			result = user.getKeyword();
		}
		
	    return renderString(response, result);
	}
	
	/**
	 * 上传
	 * 读取文件中的号码
	 * @param phoneFile
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
    @RequestMapping(value = "upload")
    public String upload(MultipartFile phoneFile, HttpServletRequest request, HttpServletResponse response, Model model) {
	    List<String> phoneList = new ArrayList<String>();
        if (StringUtils.isNotBlank(phoneFile.getName())) {
            try {
                InputStreamReader reader = new InputStreamReader(phoneFile.getInputStream());
                BufferedReader br = new BufferedReader(reader);
                for(String line = br.readLine();line !=null;line=br.readLine()){
                    if(StringUtils.isNotBlank(line)){
                        phoneList.add(StringUtils.trim(line));
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
	    
	    return renderString(response, phoneList);
	}
	
	/**
	 * 过滤错号
	 * @param
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
    @RequestMapping(value = "filterPhonesError")
	public String filterPhonesError(JmsgSmsTask jmsgSmsTask, HttpServletRequest request, HttpServletResponse response, Model model)
	{
	    List<String> phoneList = new ArrayList<String>();
	    
	    if (StringUtils.isNotBlank(jmsgSmsTask.getPhones()))
	    {
	        String[] phones = jmsgSmsTask.getPhones().split("\r\n");
	        
	        for (String string : phones) 
	        {
	            if (ValidatorUtils.isMobile(string))
	            {
	                phoneList.add(string);
	            }
	        }
	    }
	    
	    return renderString(response, phoneList);
	}
	
	/**
	 * 过滤重号
	 * @param
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
    @RequestMapping(value = "filterPhonesSame")
    public String filterPhonesSame(JmsgSmsTask jmsgSmsTask, HttpServletRequest request, HttpServletResponse response, Model model)
    {
	    Set<String> phoneList = new HashSet<String>();
        
        if (StringUtils.isNotBlank(jmsgSmsTask.getPhones()))
        {
            String[] phones = jmsgSmsTask.getPhones().split("\r\n");
            
            for (String string : phones) 
            {
                phoneList.add(string);
            }
        }
        
        return renderString(response, phoneList);
    }
    
    /**
	 * 根据查询时间获取表名
	 * @param
	 * @return
	 */
	private String getTableName(Date datetime)
	{
		String tableName = null;
		if (null == datetime) {
			datetime = new Date();
		}
		Calendar current = Calendar.getInstance();

		Calendar today = Calendar.getInstance(); // 今天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		Calendar yesterday = Calendar.getInstance();    //3天前  
        
        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));  
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));  
        yesterday.set(Calendar.DAY_OF_MONTH, yesterday.get(Calendar.DAY_OF_MONTH)-3);  
        yesterday.set( Calendar.HOUR_OF_DAY, 0);  
        yesterday.set( Calendar.MINUTE, 0);  
        yesterday.set(Calendar.SECOND, 0);  
        yesterday.set(Calendar.MILLISECOND, 0);

		current.setTime(datetime);
		
		String monthStr = "";
		int month = current.get(Calendar.MONTH) + 1;
		if (month < 10)
		{
			monthStr = "0" + month;
		}
		else
		{
			monthStr = "" + month;
		}

		if (current.after(today)) {
			tableName = "jmsg_sms_send_" + current.get(Calendar.DAY_OF_MONTH);
		} else if (current.before(today) && (current.after(yesterday) || current.compareTo(yesterday) == 0)) {

			tableName = "jmsg_sms_send_" + current.get(Calendar.DAY_OF_MONTH);
			//tableName = "jmsg_sms_send_" + monthStr + current.get(Calendar.DAY_OF_MONTH);
		} else {
			tableName = "jmsg_sms_send_history_" + current.get(Calendar.YEAR) + monthStr;
		}

		return tableName;
	}
	
	/**
	 * 普通点对点
	 * 
	 * @param jmsgSmsTask
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value = "smsDotInit")
	public String smsDotInit(JmsgSmsTask jmsgSmsTask, Model model){
		//判断是否禁用
		String usedFlag = jmsgAccountService.findUsedFlag(UserUtils.getUser().getId());
		if(!"1".equals(usedFlag)){
			addMessage(model, "账户禁用");
		}
		model.addAttribute("usedFlag", usedFlag);
		return "modules/sms/jmsgSmsDotInit";
	}
	
	/**
	 * 模板点对点
	 * 
	 * @param jmsgSmsTask
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sms:jmsgSmsTask:view")
	@RequestMapping(value = "smsDotTmplInit")
	public String smsDotTmplInit(JmsgSmsTask jmsgSmsTask, Model model){
		//判断是否禁用
		String usedFlag = jmsgAccountService.findUsedFlag(UserUtils.getUser().getId());
		if(!"1".equals(usedFlag)){
			addMessage(model, "账户禁用");
		}
		model.addAttribute("usedFlag", usedFlag);
		return "modules/sms/jmsgSmsDotTmplInit";
	}
    
	/**
	 * 导入短信普通点对点
	 * @param
	 * @param
	 * @return
	 */
    @RequestMapping(value = "importDot", method=RequestMethod.POST)
    public String importDot(MultipartFile phoneFile, HttpServletResponse response) {
    	Map<String,String> map = Maps.newHashMap();
    	String code = "1";
    	String content = "";
    	List<JmsgSmsDot> list = Lists.newArrayList();
    	int count = 0;
		try {
			
			if(phoneFile.getSize() > 5*1024*1000){
				map.put("code", "-9");//只支持导入5M内的文件
				return renderString(response, map);
			}
			
			
			String sourceName = phoneFile.getOriginalFilename();
			
	        String fileType = "";
	        if(sourceName.toLowerCase().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)){
	        	fileType = ExcelReaderUtil.EXCEL03_EXTENSION;
	        }
	        if(sourceName.toLowerCase().endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)){
	        	fileType = ExcelReaderUtil.EXCEL07_EXTENSION;
	        }
	        
	        if(StringUtils.isNotBlank(fileType)){
	        	String taskId = MsgIdUtits.msgId();//生成任务ID
	        	String fileDir = Global.getConfig("smsTask.phoneList.dir");
	        	String fileName = fileDir + File.separator + taskId + fileType;
		        FileUtils.createFile(fileName);
		        File file = new File(fileName);
				jmsgSmsTaskService.inputstreamToFile(phoneFile.getInputStream(), file);
				IRowReader reader = new SmsDotReader(list);
				ExcelReaderUtil.readExcel(reader, fileName);
				code = reader.getResult();
	        }else{
	        	code = "-5";//只支持导入xls,xlsx文件
	        }
		} catch (Exception e) {
			e.printStackTrace();
			code = "0";
		}
		if("1".equals(code)){
			content = JsonMapper.toJsonString(list);
			count = list.size();
		}
		map.put("code", code);
		map.put("content", content);
		map.put("count", String.valueOf(count));
		return renderString(response, map);
    }
    
    /**
	 * 导入短信普通点对点
	 * @param
	 * @param
	 * @return
	 */
    @RequestMapping(value = "importDotTmpl", method=RequestMethod.POST)
    public String importDotTmpl(MultipartFile phoneFile, String phoneRow, String smsContent,HttpServletResponse response) {
    	Map<String,String> map = Maps.newHashMap();
    	String code = "1";
    	String content = "";
    	int count = 0;
    	List<JmsgSmsDot> list = Lists.newArrayList();
		try {
			

			if(phoneFile.getSize() > 5*1024*1000){
				map.put("code", "-9");//只支持导入5M内的文件
				return renderString(response, map);
			}
			
			String sourceName = phoneFile.getOriginalFilename();
			
	        String fileType = "";
	        if(sourceName.toLowerCase().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)){
	        	fileType = ExcelReaderUtil.EXCEL03_EXTENSION;
	        }
	        if(sourceName.toLowerCase().endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)){
	        	fileType = ExcelReaderUtil.EXCEL07_EXTENSION;
	        }
	        
	        if(StringUtils.isNotBlank(fileType)){
	        	String taskId = MsgIdUtits.msgId();//生成任务ID
	        	String fileDir = Global.getConfig("smsTask.phoneList.dir");
	        	String fileName = fileDir + File.separator + taskId + fileType;
		        FileUtils.createFile(fileName);
		        File file = new File(fileName);
				jmsgSmsTaskService.inputstreamToFile(phoneFile.getInputStream(), file);
				IRowReader reader = new SmsDotTmplReader(list, phoneRow, smsContent);
				ExcelReaderUtil.readExcel(reader, fileName);
				code = reader.getResult();
	        }else{
	        	code = "-5";//只支持导入xls,xlsx文件
	        }
		} catch (Exception e) {
			e.printStackTrace();
			code = "0";
		}
		if("1".equals(code)){
			content = JsonMapper.toJsonString(list);
			count = list.size();
		}
		map.put("code", code);
		map.put("content", content);
		map.put("count", String.valueOf(count));
		return renderString(response, map);
    }
    
	@RequestMapping(value = "download")
	public void download(String taskId,HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException{
		try{
			String fileName = taskId + ".txt";
			String fileDir = Global.getConfig("smsTask.phoneList.dir");
        	String path = fileDir+File.separator+fileName;
			
			File file = new File(path);
			InputStream fis = new BufferedInputStream(new FileInputStream(path));  
	        byte[] buffer = new byte[fis.available()];  
	        fis.read(buffer);  
	        fis.close();
	        response.reset();
	        
	        response.addHeader("Content-Type", "text/html; charset=utf-8");  
	        String downLoadName = new String(fileName.getBytes("gbk"), "iso8859-1");  
	        response.addHeader("Content-Disposition", "attachment;filename="+downLoadName); 
	        response.addHeader("Content-Length", "" + file.length());
	        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());//得到向客户端输出二进制数据的对象  
	        response.setContentType("octets/stream");  
	        toClient.write(buffer); //输出数据  
	        toClient.flush();  
	        toClient.close();   
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }
	}

}
