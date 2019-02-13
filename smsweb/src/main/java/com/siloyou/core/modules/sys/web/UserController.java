/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.core.modules.sys.web;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.modules.sys.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.core.common.beanvalidator.BeanValidators;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.utils.excel.ImportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.oa.entity.OaNotify;
import com.siloyou.core.modules.oa.service.OaNotifyService;
import com.siloyou.core.modules.sys.entity.Office;
import com.siloyou.core.modules.sys.entity.Role;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.service.SystemService;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;
import com.siloyou.jmsg.modules.sms.entity.SmsUserIndex;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsDayReportService;

/**
 * 用户Controller
 * @author ThinkGem
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemService systemService;
	
	@Autowired
	private JmsgSmsDayReportService jmsgSmsDayReportService;
	
	@Autowired
	private OaNotifyService oaNotifyService;

	@Autowired
	private RoleService roleService;
	
	@ModelAttribute
	public User get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getUser(id);
		}else{
			return new User();
		}
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"index"})
	public String index(User user, Model model) {
		return "modules/sys/userIndex";
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = {"list", ""})
	public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
        model.addAttribute("page", page);
		return "modules/sys/userList";
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "form")
	public String form(User user, Model model) {
		if (user.getCompany()==null || StringUtils.isBlank(user.getCompany().getId())){
			user.setCompany(UserUtils.getUser().getCompany());
		}
		if (user.getOffice()==null || user.getOffice().getId()==null){
			user.setOffice(UserUtils.getUser().getOffice());
		}
		model.addAttribute("user", user);

		model.addAttribute("allRoles", systemService.findAllRole());

		User currentUser = UserUtils.getUser();
		model.addAttribute("currentUser", currentUser);
		//如果是developer登录, 给出所有的角色
		//如果不是developer登录, 只给出一个角色:普通用户

		Role r = new Role();
		r.setName("普通用户");
		List<Role> commonUser = roleService.findListForCommonUser(r);
		model.addAttribute("commonUser", commonUser);

		return "modules/sys/userForm";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "save")
	public String save(User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		// 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
		user.setCompany(new Office(request.getParameter("company.id")));
		user.setOffice(new Office(request.getParameter("office.id")));
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
		}
		if (!beanValidator(model, user)){
			return form(user, model);
		}
		if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))){
			addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
			return form(user, model);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		List<String> roleIdList = user.getRoleIdList();
		for (Role r : systemService.findAllRole()){
			if (roleIdList.contains(r.getId())){
				roleList.add(r);
			}
		}
		user.setRoleList(roleList);
		// 保存用户信息
		user.setLoginFlag("1");
		
		systemService.saveUser(user);
		// 清除当前用户缓存
		if (user.getLoginName().equals(UserUtils.getUser().getLoginName())){
			UserUtils.clearCache();
			//UserUtils.getCacheMap().clear();
		}
		addMessage(redirectAttributes, "保存用户'" + user.getLoginName() + "'成功");
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}
	
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "delete")
	public String delete(User user, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		if (UserUtils.getUser().getId().equals(user.getId())){
			addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
		}else if (User.isAdmin(user.getId())){
			addMessage(redirectAttributes, "删除用户失败, 不允许删除超级管理员用户");
		}else{
			systemService.deleteUser(user);
			addMessage(redirectAttributes, "删除用户成功");
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}
	
	/**
	 * 导出用户数据
	 * @param user
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
    		new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
    }

	/**
	 * 导入用户数据
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<User> list = ei.getDataList(User.class);
			for (User user : list){
				try{
					if ("true".equals(checkLoginName("", user.getLoginName()))){
						user.setPassword(SystemService.entryptPassword("123456"));
						BeanValidators.validateWithException(validator, user);
						systemService.saveUser(user);
						successNum++;
					}else{
						failureMsg.append("<br/>登录名 "+user.getLoginName()+" 已存在; ");
						failureNum++;
					}
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
    }
	
	/**
	 * 下载导入用户数据模板
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据导入模板.xlsx";
    		List<User> list = Lists.newArrayList(); list.add(UserUtils.getUser());
    		new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
    }

	/**
	 * 验证登录名是否有效
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 用户信息显示及保存
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "info")
	public String info(User user, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			currentUser.setEmail(user.getEmail());
			currentUser.setPhone(user.getPhone());
			currentUser.setMobile(user.getMobile());
			currentUser.setRemarks(user.getRemarks());
			currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			model.addAttribute("message", "保存用户信息成功");
		}

		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());

		/* 首页统计报表
		if(PowerUtils.adminFlag()){//管理员权限

			
			JmsgSmsDayReport param = new JmsgSmsDayReport();
			param.setDayQ(DateUtils.getDay(0, 0, 0));
			param.setDayZ(DateUtils.getDay(23, 59, 59));
			JmsgSmsDayReport smsDayReport = jmsgSmsDayReportService.findSmsDayReportByDay(param);
			if(smsDayReport == null){
				smsDayReport = new JmsgSmsDayReport();
			}
			model.addAttribute("user", currentUser);
			model.addAttribute("smsDayReport",smsDayReport);


			return "modules/sys/adminIndex";//20190121 zhanghui 管理员首页报表统计
		}else if(PowerUtils.agencyFlag()){//分销商
			return "modules/sys/userInfo";
		}else{//普通用户
			return "modules/sys/userInfo";
		}
		*/
		
		return "modules/sys/userInfo";
	}

	/**
	 * 返回用户信息
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoData")
	public User infoData() {
		return UserUtils.getUser();
	}
	
	/**
	 * 修改个人用户密码
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userModifyPwd";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())){
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			}else{
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String companyId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = systemService.findUserByOfficeId(companyId);
		for (int i=0; i<list.size(); i++){
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_"+e.getId());
			map.put("pId", companyId);
			map.put("name", StringUtils.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}
	
	/**
	 * 用户信息显示及保存
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "userSysIndex")
	public String userSysIndex(User user, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		
		List<OaNotify> notifyList = oaNotifyService.findList(new OaNotify());
		
		//当天
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		param.setUser(currentUser);
		param.setQueryType("day");
		param.setDayQ(DateUtils.getDay(0, 0, 0));
		param.setDayZ(DateUtils.getDay(23, 59, 59));
		List<JmsgSmsDayReport> dayReportList = jmsgSmsDayReportService.findList4Index(param);
		JmsgSmsDayReport dayReport = new JmsgSmsDayReport();
		if (null != dayReportList && dayReportList.size() > 0)
		{
			dayReport = dayReportList.get(0);
		}
		
		//周（7天）
		param = new JmsgSmsDayReport();
		param.setUser(currentUser);
		param.setQueryType("day");
		param.setDayQ(DateUtils.getDay(DateUtils.getDay(-7), 0, 0, 0));
		param.setDayZ(DateUtils.getDay(23, 59, 59));
		List<JmsgSmsDayReport> weekReportList = jmsgSmsDayReportService.findList4Index(param);
		
		//月
		param = new JmsgSmsDayReport();
		param.setUser(currentUser);
		param.setQueryType("month");
		/*param.setDayQ(DateUtils.getFirstDayOfMonth());
		param.setDayZ(DateUtils.getEndDayOfMonth());*/
		param.setDayQ(DateUtils.getDay(DateUtils.getDay(-30), 0, 0, 0));
		param.setDayZ(DateUtils.getDay(23, 59, 59));
		dayReportList = jmsgSmsDayReportService.findList4Index(param);
		
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数
		
		//本月天数列表
		//List<Date> dayListOfTheMonth = DateUtils.getAllTheDateOftheMonth(new Date());
		//不取自然月，取30天数据
		List<Date> dayListOfTheMonth = DateUtils.getAllTheDate(new Date(), -30);
		//周天数列表（前6天 + 当天）
		List<Date> dayList = DateUtils.getAllTheDate(new Date(), -6);
		int successTate = 0;
		int successCount = 0;
		
		StringBuilder week2XValue = new StringBuilder();
		StringBuilder week2YValue = new StringBuilder();
		StringBuilder week1XValue = new StringBuilder();
		StringBuilder week1YValue = new StringBuilder();
		for (Date date : dayList)
		{
			successTate = 0;
			successCount = 0;
			
			for (JmsgSmsDayReport jmsgSmsDayReport : weekReportList)
			{
				if (DateUtils.isSameDay(date, jmsgSmsDayReport.getDay()))
				{
					if (jmsgSmsDayReport.getSuccessCount() > 0)
					{
						successTate = jmsgSmsDayReport.getReportSuccessCount() * 100 / jmsgSmsDayReport.getSuccessCount();
						successCount = jmsgSmsDayReport.getSuccessCount();
					}
					else
					{
						successTate = 0;
						successCount = 0;
					}
					break;
				}
			}
			
			week1XValue.append(",'").append(DateUtils.formatDate(date, "M/dd")).append("'");
			week1YValue.append(",").append(successTate);
			
			week2XValue.append(",'").append(DateUtils.formatDate(date, "M/dd")).append("'");
			week2YValue.append(",").append(successCount);
		}
		
		StringBuilder month2XValue = new StringBuilder();
		StringBuilder month2YValue = new StringBuilder();
		StringBuilder month1XValue = new StringBuilder();
		StringBuilder month1YValue = new StringBuilder();
		for (Date date : dayListOfTheMonth)
		{
			successTate = 0;
			successCount = 0;
			
			for (JmsgSmsDayReport jmsgSmsDayReport : dayReportList)
			{
				//if (DateUtils.getDayOfMonth(date) == DateUtils.getDayOfMonth(jmsgSmsDayReport.getDay()))
				if (DateUtils.isSameDay(date, jmsgSmsDayReport.getDay()))
				{
					if (jmsgSmsDayReport.getSuccessCount() > 0)
					{
						successTate = jmsgSmsDayReport.getReportSuccessCount() * 100 / jmsgSmsDayReport.getSuccessCount();
						successCount = jmsgSmsDayReport.getSuccessCount();
					}
					else
					{
						successTate = 0;
						successCount = 0;
					}
					break;
				}
			}
			
			month1XValue.append(",'").append(DateUtils.formatDate(date, "M/dd")).append("'");
			month1YValue.append(",").append(successTate);
			
			month2XValue.append(",'").append(DateUtils.formatDate(date, "M/dd")).append("'");
			month2YValue.append(",").append(successCount);
		}
		
		
		
		
		/*for (JmsgSmsDayReport jmsgSmsDayReport : weekReportList)
		{
			week1XValue.append(",'").append(DateUtils.formatDate(jmsgSmsDayReport.getDay(), "M/dd")).append("'");
			if (jmsgSmsDayReport.getSuccessCount() > 0)
			{
				week1YValue.append(",").append(df.format(jmsgSmsDayReport.getReportSuccessCount() * 100 / jmsgSmsDayReport.getSuccessCount()));
			}
			else
			{
				week1YValue.append(",").append(0);
			}
			
			week2XValue.append(",'").append(DateUtils.formatDate(jmsgSmsDayReport.getDay(), "M/dd")).append("'");
			week2YValue.append(",").append(jmsgSmsDayReport.getSuccessCount());
		}
		
		for (JmsgSmsDayReport jmsgSmsDayReport : dayReportList)
		{
			month1XValue.append(",'").append(DateUtils.formatDate(jmsgSmsDayReport.getDay(), "M/dd")).append("'");
			if (jmsgSmsDayReport.getSuccessCount() > 0)
			{
				month1YValue.append(",").append(df.format(jmsgSmsDayReport.getReportSuccessCount() * 100 / jmsgSmsDayReport.getSuccessCount()));
			}
			else
			{
				month1YValue.append(",").append(0);
			}
			
			month2XValue.append(",'").append(DateUtils.formatDate(jmsgSmsDayReport.getDay(), "M/dd")).append("'");
			month2YValue.append(",").append(jmsgSmsDayReport.getSuccessCount());
		}*/
		
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		model.addAttribute("notifyList", notifyList);
		
		model.addAttribute("jmsgSmsDayReport", dayReport);
		model.addAttribute("weekReportList", weekReportList);
		model.addAttribute("monthReportList", dayReportList);
		
		model.addAttribute("week2XValue", StringUtils.isBlank(week2XValue) ? week2XValue : week2XValue.substring(1, week2XValue.length()));
		model.addAttribute("week2YValue", StringUtils.isBlank(week2YValue) ? week2YValue : week2YValue.substring(1, week2YValue.length()));
		model.addAttribute("week1XValue", StringUtils.isBlank(week1XValue) ? week1XValue : week1XValue.substring(1, week1XValue.length()));
		model.addAttribute("week1YValue", StringUtils.isBlank(week1YValue) ? week1YValue : week1YValue.substring(1, week1YValue.length()));
		model.addAttribute("month2XValue", StringUtils.isBlank(month2XValue) ? month2XValue : month2XValue.substring(1, month2XValue.length()));
		model.addAttribute("month2YValue", StringUtils.isBlank(month2YValue) ? month2YValue : month2YValue.substring(1, month2YValue.length()));
		model.addAttribute("month1XValue", StringUtils.isBlank(month1XValue) ? month1XValue : month1XValue.substring(1, month1XValue.length()));
		model.addAttribute("month1YValue", StringUtils.isBlank(month1YValue) ? month1YValue : month1YValue.substring(1, month1YValue.length()));
		return "modules/sys/userSysIndex";
	}
	
	
	
	@RequestMapping(value = "adminIndex")
	public String adminIndex(HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		param.setDayQ(DateUtils.getDay(0, 0, 0));
		param.setDayZ(DateUtils.getDay(23, 59, 59));
		JmsgSmsDayReport smsDayReport = jmsgSmsDayReportService.findSmsDayReportByDay(param);
		if(smsDayReport == null){
			smsDayReport = new JmsgSmsDayReport();
		}
		model.addAttribute("user",user);
		model.addAttribute("smsDayReport",smsDayReport);
		return "modules/sys/adminIndex";
	}
	
	//用户近30天发送量数据趋势
	@RequestMapping(value = "queryCountByDay")
	public String queryCountByDay(HttpServletResponse response){
		Map<String,String> cmap = getDay(-29);//总量MAP
		Map<String,String> smap = Maps.newLinkedHashMap();//成功MAP
		smap.putAll(cmap);
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		param.setDayZ(new Date());
		param.setDayQ(DateUtils.getDay(-29));
		List<SmsUserIndex> list = jmsgSmsDayReportService.queryCountByDay(param);
		if(list != null && list.size() > 0){
			for (SmsUserIndex smsUserIndex : list) {
				if(smsUserIndex != null){
					String key = smsUserIndex.getDay();
					if(StringUtils.isNotBlank(key)){
						if(cmap.containsKey(key)){
							cmap.put(key, String.valueOf(smsUserIndex.getCount()));
						}
						if(smap.containsKey(key)){
							smap.put(key, String.valueOf(smsUserIndex.getSuccessCount()));
						}
					}
				}
			}
		}
		
		String day="";
		String count="";
		for(Entry<String,String> entry:cmap.entrySet()){
			count=count+","+entry.getValue();
			day=day+","+entry.getKey();
		}
		
		String success="";
		for(Entry<String,String> entry:smap.entrySet()){
			success=success+","+entry.getValue();
		}
		
		SmsUserIndex smsUserIndex = new SmsUserIndex();
		smsUserIndex.setDay(day.length() > 1 ? day.substring(1) : "");
		smsUserIndex.setCountArray(count.length() > 1 ? count.substring(1) : "");
		smsUserIndex.setSuccessArray(success.length() > 1 ? success.substring(1) : "");
		
		return renderString(response, smsUserIndex);
	}
	
	@RequestMapping(value = "queryCountByDayPhoneType")
	public String queryCountByDayPhoneType(String queryType, HttpServletResponse response){
		Map<String,String> ltmap = Maps.newLinkedHashMap();
		Map<String,String> dxmap = Maps.newLinkedHashMap();
		Map<String,String> ydmap = Maps.newLinkedHashMap();
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		param.setDayZ(new Date());
		if("1".equals(queryType)){//7天内
			ydmap.putAll(getDay(-6));//移动MAP
			ltmap.putAll(ydmap);//联通MAP
			dxmap.putAll(ydmap);//电信MAP
			param.setDayQ(DateUtils.getDay(-6));
			
		}else{
			ydmap.putAll(getDay(-29));//移动MAP
			ltmap.putAll(ydmap);//联通MAP
			dxmap.putAll(ydmap);//电信MAP
			param.setDayQ(DateUtils.getDay(-29));
		}
		List<SmsUserIndex> list = jmsgSmsDayReportService.findCountByDayPhoneType(param);
		if(list != null && list.size() >0){
			for (SmsUserIndex smsUserIndex : list) {
				if(smsUserIndex != null){
					String key = smsUserIndex.getDay();
					if(StringUtils.isNotBlank(key)){
						if("YD".equals(smsUserIndex.getPhoneType())){
							if(ydmap.containsKey(key)){
								ydmap.put(key, String.valueOf(smsUserIndex.getCount()));
							}
						}else if("LT".equals(smsUserIndex.getPhoneType())){
							if(ltmap.containsKey(key)){
								ltmap.put(key, String.valueOf(smsUserIndex.getCount()));
							}
						}else if("DX".equals(smsUserIndex.getPhoneType())){
							if(dxmap.containsKey(key)){
								dxmap.put(key, String.valueOf(smsUserIndex.getCount()));
							}
						}
					}
					
				}
			}
		}
		
		String day="";
		String ydCountArray="";
		String ltCountArray="";
		String dxCountArray="";
		for(Entry<String,String> entry:ydmap.entrySet()){
			ydCountArray=ydCountArray+","+entry.getValue();
			day=day+","+entry.getKey();
		}
		
		for(Entry<String,String> entry:ltmap.entrySet()){
			ltCountArray=ltCountArray+","+entry.getValue();
		}
		
		for(Entry<String,String> entry:dxmap.entrySet()){
			dxCountArray=dxCountArray+","+entry.getValue();
		}
		
		SmsUserIndex smsUserIndex = new SmsUserIndex();
		smsUserIndex.setDay(day.length() > 1 ? day.substring(1) : "");
		smsUserIndex.setYdCountArray(ydCountArray.length() > 1 ? ydCountArray.substring(1):"");
		smsUserIndex.setLtCountArray(ltCountArray.length() > 1 ? ltCountArray.substring(1):"");
		smsUserIndex.setDxCountArray(dxCountArray.length() > 1 ? dxCountArray.substring(1):"");
		
		return renderString(response, smsUserIndex);
	}
	
	//运营商 网关成功量占比、状态成功量占比、状态失败量占比、状态未知量占比
	@RequestMapping(value = "queryCountByPhoneType")
	public String queryCountByPhoneType(String queryType, HttpServletResponse response){
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		param.setDayZ(new Date());
		if("1".equals(queryType)){//7天前
			param.setDayQ(DateUtils.getDay(-6));
		}else{
			param.setDayQ(DateUtils.getDay(-29));
		}
		
		List<JmsgSmsDayReport> result = Lists.newArrayList();
		
		List<JmsgSmsDayReport> list = jmsgSmsDayReportService.findCountByPhoneType(param);
		if(list != null && list.size() >0){
			for (JmsgSmsDayReport jmsgSmsDayReport : list) {
				if(jmsgSmsDayReport != null){
					if("YD".equals(jmsgSmsDayReport.getPhoneType())||"LT".equals(jmsgSmsDayReport.getPhoneType())||"DX".equals(jmsgSmsDayReport.getPhoneType())){
						result.add(jmsgSmsDayReport);
					}
				}
			}
		}
		return renderString(response, result);
	}
	
	/**
	 * @param value 获取日期
	 * @return
	 */
	private Map<String,String> getDay(int value){
		Map<String,String> map = Maps.newLinkedHashMap();
		for(int i=value; i<=0; i++){
			map.put(DateUtils.formatDate(DateUtils.getDay(i), "M/d") , "0");
		}
		return map;
	}
    
//	@InitBinder
//	public void initBinder(WebDataBinder b) {
//		b.registerCustomEditor(List.class, "roleList", new PropertyEditorSupport(){
//			@Autowired
//			private SystemService systemService;
//			@Override
//			public void setAsText(String text) throws IllegalArgumentException {
//				String[] ids = StringUtils.split(text, ",");
//				List<Role> roles = new ArrayList<Role>();
//				for (String id : ids) {
//					Role role = systemService.getRole(Long.valueOf(id));
//					roles.add(role);
//				}
//				setValue(roles);
//			}
//			@Override
//			public String getAsText() {
//				return Collections3.extractToString((List) getValue(), "id", ",");
//			}
//		});
//	}
}
