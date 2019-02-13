/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.siloyou.core.modules.sys.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.Office;
import com.siloyou.core.modules.sys.entity.Role;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.service.SystemService;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.account.entity.JmsgAccount;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;

/**
 * 资金账户信息Controller
 * @author zhukc
 * @version 2016-05-17
 */
@Controller
@RequestMapping(value = "${adminPath}/account/jmsgAccount")
public class JmsgAccountController extends BaseController {

	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private SystemService systemService;

	@Autowired
	private RoleService roleService;
	
	@ModelAttribute
	public JmsgAccount get(@RequestParam(required=false) String id) {
		JmsgAccount entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgAccountService.get(id);
		}
		if (entity == null){
			entity = new JmsgAccount();
		}
		return entity;
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "index")
	public String index() {
		return "modules/account/jmsgAccountIndex";
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgAccount jmsgAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		try
        {
			if (null != jmsgAccount.getCompany() && StringUtils.isNotBlank(jmsgAccount.getCompany().getName()))
			{
				jmsgAccount.getCompany().setName(URLDecoder.decode(jmsgAccount.getCompany().getName(), "UTF-8"));
			}
        }
        catch (UnsupportedEncodingException e)
        {
            addMessage(model, e.toString());
        }
		
		Page<JmsgAccount> page = jmsgAccountService.findPage(new Page<JmsgAccount>(request, response), jmsgAccount); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountList";
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "agencyList")
	public String agencyList(JmsgAccount jmsgAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!PowerUtils.agencyFlag()){//不是代理权限
			jmsgAccount.setUser(UserUtils.getUser());
		}else {
			jmsgAccount.setCompanyId(UserUtils.getUser().getCompany().getId());
			//jmsgAccount.setUserId(UserUtils.getUser().getId());
		}
		jmsgAccount.setAppType("mms");
		Page<JmsgAccount> page = jmsgAccountService.findPage(new Page<JmsgAccount>(request, response), jmsgAccount); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountAgencyList";
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "agencySmsList")
	public String agencySmsList(JmsgAccount jmsgAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!PowerUtils.agencyFlag()){//不是代理权限
			jmsgAccount.setUser(UserUtils.getUser());
		}else {
			jmsgAccount.setCompanyId(UserUtils.getUser().getCompany().getId());
//			jmsgAccount.setUserId(UserUtils.getUser().getId());
		}
		jmsgAccount.setAppType("sms");
		Page<JmsgAccount> page = jmsgAccountService.findPage(new Page<JmsgAccount>(request, response), jmsgAccount); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountAgencySmsList";
	}

	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "form")
	public String form(JmsgAccount jmsgAccount, Model model) {
		String appType = jmsgAccount.getAppType();
		Office company = null;
		if(jmsgAccount != null && jmsgAccount.getUser() != null){
			company = jmsgAccount.getUser().getCompany();
		}
		
		jmsgAccount = jmsgAccountService.getByAgency(jmsgAccount);
		if(jmsgAccount == null){
			jmsgAccount = new JmsgAccount();
			if(company == null || StringUtils.isBlank(company.getId())){
				company = UserUtils.getUser().getCompany();
			}
			User user = new User();
			user.setCompany(company);
			jmsgAccount.setUser(user);
		}else{
			jmsgAccount.setUser(UserUtils.get(jmsgAccount.getUser().getId()));
		}
		
		String action = "modules/account/jmsgAccountForm";
		if("sms".equals(appType)){
			action = "modules/account/jmsgAccountSmsForm";
		}
		model.addAttribute("jmsgAccount", jmsgAccount);
		model.addAttribute("allRoles", systemService.findAllRole());

		User currentUser = UserUtils.getUser();
		model.addAttribute("currentUser", currentUser);
		//如果是developer登录, 给出所有的角色
		//如果不是developer登录, 只给出一个角色:普通用户

		Role r = new Role();
		r.setName("普通用户");
		List<Role> commonUser = roleService.findListForCommonUser(r);
		model.addAttribute("commonUser", commonUser);

		return action;
	}
	
	/**
	 * 用户通道分组配置
	 * @param jmsgAccount
	 * @param model
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequiresPermissions("account:jmsgAccount:view")
    @RequestMapping(value = "toGroupConfig")
    public String toGroupConfig(JmsgAccount jmsgAccount, Model model) {
        Office company = null;
        if(jmsgAccount != null && jmsgAccount.getUser() != null){
            company = jmsgAccount.getUser().getCompany();
        }
        
        jmsgAccount = jmsgAccountService.getByAgency(jmsgAccount);
        if(jmsgAccount == null){
            jmsgAccount = new JmsgAccount();
            if(company == null || StringUtils.isBlank(company.getId())){
                company = UserUtils.getUser().getCompany();
            }
            User user = new User();
            user.setCompany(company);
            jmsgAccount.setUser(user);
        }else{
            jmsgAccount.setUser(UserUtils.get(jmsgAccount.getUser().getId()));
        }
        
        model.addAttribute("jmsgAccount", jmsgAccount);
        model.addAttribute("allRoles", systemService.findAllRole());
        return "modules/account/jmsgAccountSmsGroupCfg";
    }
	
	/**
	 * 更新用户通道组信息
	 * @param jmsgAccount
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequiresPermissions("account:jmsgAccount:edit")
    @RequestMapping(value = "groupConfig")
    public String groupConfig(JmsgAccount jmsgAccount, Model model, RedirectAttributes redirectAttributes) 
    {
        // 普通用户只更新自己的通道分组，代理用户才需要同步更新当前机构下所有用户的通道分组
//        if ("3".equals(jmsgAccount.getUser().getUserType()))
//        {
//            systemService.batchUpdateUserGroup(jmsgAccount);
//        }
//        else if ("4".equals(jmsgAccount.getUser().getUserType()))
//        {
            systemService.updateUserGroupById(jmsgAccount);
//        }
            
        // 清除当前用户缓存
        if (jmsgAccount.getUser().getLoginName().equals(UserUtils.getUser().getLoginName()))
        {
            UserUtils.clearCache();
        }
        addMessage(redirectAttributes, "更新用户通道组信息成功");
        return "redirect:" + Global.getAdminPath() + "/account/jmsgAccount/?repage";
    }
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "agencyForm")
	public String agencyForm(JmsgAccount jmsgAccount, Model model) {
		jmsgAccount = jmsgAccountService.getByAgency(jmsgAccount);
		if(jmsgAccount == null)jmsgAccount = new JmsgAccount();
		model.addAttribute("jmsgAccount", jmsgAccount);
		return "modules/account/jmsgAccountAgencyForm";
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "agencySmsForm")
	public String agencySmsForm(JmsgAccount jmsgAccount, Model model) {
		jmsgAccount = jmsgAccountService.getByAgency(jmsgAccount);
		if(jmsgAccount == null)jmsgAccount = new JmsgAccount();
		model.addAttribute("jmsgAccount", jmsgAccount);
		return "modules/account/jmsgAccountAgencySmsForm";
	}	
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "listByRecharge")
	public String listByRecharge(JmsgAccount jmsgAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		String name = request.getParameter("name");
		try{
			if(null!=name){
				name = java.net.URLDecoder.decode(name,"UTF-8");
				jmsgAccount.getUser().setName(name);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		if(!PowerUtils.adminFlag()){//不是管理员权限
			jmsgAccount.setUser(UserUtils.getUser());
		}
		Page<JmsgAccount> page = jmsgAccountService.findPage(new Page<JmsgAccount>(request, response), jmsgAccount); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountRechargeList";
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "listByRechargeAgency")
	public String listByRechargeAgency(JmsgAccount jmsgAccount, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.agencyFlag()){//代理权限
			jmsgAccount.setCompanyId(UserUtils.getUser().getCompany().getId());
		}else{
			jmsgAccount.setUser(UserUtils.getUser());
		}
		Page<JmsgAccount> page = jmsgAccountService.findPage(new Page<JmsgAccount>(request, response), jmsgAccount); 
		model.addAttribute("page", page);
		return "modules/account/jmsgAccountRechargeAgencyList";
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "rechargeInit")
	public String rechargeInit(JmsgAccount jmsgAccount, Model model) {
		model.addAttribute("jmsgAccount", jmsgAccount);
		return "modules/account/jmsgAccountRecharge";
	}
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "rechargeSmsInit")
	public String rechargeSmsInit(JmsgAccount jmsgAccount, Model model) {
		model.addAttribute("jmsgAccount", jmsgAccount);
		return "modules/account/jmsgAccountSmsRecharge";
	}	
	
	@RequiresPermissions("account:jmsgAccount:view")
	@RequestMapping(value = "rechargeAgencyInit")
	public String rechargeAgencyInit(JmsgAccount jmsgAccount, Model model) {
		model.addAttribute("jmsgAccount", jmsgAccount);
		return "modules/account/jmsgAccountRechargeAgency";
	}
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "recharge")
	public String recharge(JmsgAccount jmsgAccount, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgAccount)){
			return form(jmsgAccount, model);
		}
		User user =  UserUtils.get(jmsgAccount.getUser().getId());
		String createUserId = UserUtils.getUser().getId();
		String remark = jmsgAccount.getRemark();
		long money =0;
		if(jmsgAccount.getMoney() == null){
			money = new Double(jmsgAccount.getMoneyD()*100).longValue();
		}else{
			money = jmsgAccount.getMoney();
		}
		String payment = jmsgAccount.getPayment();
		String appType = jmsgAccount.getAppType();
		String changeCode = payment.substring(2, payment.length());
		String key = AccountCacheUtils.getAmountKey("sms", user.getId());
		if("XF01".equals(payment)){//手动扣款
			JedisClusterUtils.decrBy(key, money);
			jmsgAccountService.consumeMoney(user.getId(), changeCode, money, appType, "手动扣款操作 "+remark, createUserId, "");
		}else if("CZ00".equals(payment)){
			JedisClusterUtils.incrBy(key, money);
			jmsgAccountService.rechargeMoney(user.getId(), changeCode, money, appType, "充值操作(帐转入) "+remark, createUserId, "");
		}else if("CZ01".equals(payment)){
			JedisClusterUtils.incrBy(key, money);
			jmsgAccountService.rechargeMoney(user.getId(), changeCode, money, appType, "手动返充操作 "+remark, createUserId, "");
		}
		addMessage(redirectAttributes, "资金账户充值成功");

		String name = user.getName();
		try{
			name = java.net.URLEncoder.encode(name,"UTF-8");
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/listByRecharge?user.id="+user.getId()+"&appType="+appType+"&name=" + name;
	}
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "rechargeAgency")
	public String rechargeAgency(JmsgAccount jmsgAccount, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgAccount)){
			return form(jmsgAccount, model);
		}
		String appType = jmsgAccount.getAppType();
		String msg = "";
		User user = UserUtils.get(jmsgAccount.getUser().getId());
		String createUserId = UserUtils.getUser().getId();
		String remark = jmsgAccount.getRemark();
		long money = jmsgAccount.getMoney();//充值金额
		String payment = jmsgAccount.getPayment();//充值方式
		String changeCode = payment.substring(2, payment.length());
		
		if("XF01".equals(payment)){//手动扣款
			msg = rechargeMoney(appType, user.getId(), createUserId, createUserId, money, changeCode, "手动扣款操作 "+remark, "手动扣款操作 "+remark);
		}else if("CZ00".equals(payment)){//充值
			msg = rechargeMoney(appType, createUserId, user.getId(),createUserId,  money, changeCode,"充值操作(帐转出) "+remark,"充值操作(帐转入) "+remark);
		}else if("CZ01".equals(payment)){//手动返充
			msg = rechargeMoney(appType, createUserId, user.getId(),createUserId,  money, changeCode,"手动返充操作 "+remark,"手动返充操作 "+remark);
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/listByRechargeAgency?user.id="+user.getId()+"&appType="+appType+"&user.name="+user.getName();
	}
	
	/**
	 * 
	 * @param appType
	 * @param kfUserId 扣费用户
	 * @param czUserId 充值用户
	 * @param createUserId 创建用户
	 * @param money 金额
	 * @param changeCode
	 * @return
	 */
	private String rechargeMoney(String appType, String kfUserId, String czUserId,String createUserId, long money,String changeCode,String kfRemark, String czRemark) {
		String msg = DictUtils.getDictLabel(appType, "app_type", "")+"账户充值成功";

		String kfUserKey = AccountCacheUtils.getAmountKey(appType, kfUserId);
		String czUserKey = AccountCacheUtils.getAmountKey(appType, czUserId);
		long amount = JedisClusterUtils.decrBy(kfUserKey, money);
		if(amount > 0){
			jmsgAccountService.consumeMoney(kfUserId, changeCode, money, appType, kfRemark, createUserId, "");//扣费
			jmsgAccountService.rechargeMoney(czUserId, changeCode, money, appType, czRemark, createUserId, "");//充值
			JedisClusterUtils.incrBy(czUserKey, money);
		}else{
			JedisClusterUtils.incrBy(kfUserKey, money);
			msg = "操作失败，余额不足";
		}
		
//		long balance = balance(kfUserId,appType);//用户余额
//		if(balance >= money){
//			jmsgAccountService.consumeMoney(kfUserId, changeCode, money, appType, kfRemark, createUserId, "");//扣费
//			jmsgAccountService.rechargeMoney(czUserId, changeCode, money, appType, czRemark, createUserId, "");//充值
//		}else{
//			msg = "操作失败，余额不足";
//		}
		return msg;
	}
	
	private long balance(String userId,String appType){
//		return jmsgAccountService.findUserMoeny(userId, appType);//余额
		String key = AccountCacheUtils.getAmountKey(appType, userId);
		return JedisClusterUtils.getInt(key);
	}
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "save")
	public String save(JmsgAccount jmsgAccount, String oldLoginName,String newPassword,Model model, RedirectAttributes redirectAttributes) {
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(newPassword)) {
			jmsgAccount.getUser().setPassword(SystemService.entryptPassword(newPassword));
		}
		if (!"true".equals(checkLoginName(oldLoginName, jmsgAccount.getUser().getLoginName()))){
			addMessage(model, "保存用户'" + jmsgAccount.getUser().getLoginName() + "'失败，登录名已存在");
			return form(jmsgAccount, model);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		List<String> roleIdList = jmsgAccount.getUser().getRoleIdList();
		for (Role r : systemService.findAllRole()){
			if (roleIdList.contains(r.getId())){
				roleList.add(r);
			}
		}
		
		jmsgAccount.getUser().setRoleList(roleList);
		// 保存用户信息
		jmsgAccount.getUser().setLoginFlag("1");
//		jmsgAccount.getUser().setCompany(UserUtils.getUser().getCompany());
		
		if (StringUtils.isBlank(jmsgAccount.getUser().getGroupId()))
		{
		    jmsgAccount.getUser().setGroupId("-1");
		}
		// 普通用户继承代理商通道，修改代理商的通道， 代理商下所有用户的通道同步修改
		if ("4".equals(jmsgAccount.getUser().getUserType()))
		{
		    // 新建用户时才需要
		    if (StringUtils.isBlank(jmsgAccount.getUser().getId()))
		    {
		        User dlUser = systemService.getUserByCompanyId(jmsgAccount.getCompanyId());
		        if(null==jmsgAccount.getCompanyId()){
					jmsgAccount.getUser().setGroupId("");
				}else{
					jmsgAccount.getUser().setGroupId(dlUser.getGroupId());
				}
		    }
		}
		
		systemService.saveUser(jmsgAccount.getUser(),jmsgAccount.getPayMode(),jmsgAccount.getAppType());
		// 清除当前用户缓存
		if (jmsgAccount.getUser().getLoginName().equals(UserUtils.getUser().getLoginName())){
			UserUtils.clearCache();
		}
		jmsgAccount.setCompany(jmsgAccount.getUser().getCompany());
		redirectAttributes.addFlashAttribute("jmsgAccount", jmsgAccount);
		addMessage(redirectAttributes, "保存资金账户信息成功");
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/?repage";
	}
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "agencySave")
	public String agencySave(JmsgAccount jmsgAccount,String oldLoginName,String newPassword, Model model, RedirectAttributes redirectAttributes) {
		
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(newPassword)) {
			jmsgAccount.getUser().setPassword(SystemService.entryptPassword(newPassword));
		}
		if (!"true".equals(checkLoginName(oldLoginName, jmsgAccount.getUser().getLoginName()))){
			addMessage(model, "保存用户'" + jmsgAccount.getUser().getLoginName() + "'失败，登录名已存在");
			return agencyForm(jmsgAccount, model);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		Role role = new Role();
		role.setId(Global.getConfig("mms.commom.role.id"));//普通用户角色ID
		roleList.add(role);
		
		jmsgAccount.getUser().setRoleList(roleList);
		// 保存用户信息
		jmsgAccount.getUser().setLoginFlag("1");
		jmsgAccount.getUser().setCompany(UserUtils.getUser().getCompany());
		
		systemService.saveUserByAgency(jmsgAccount.getUser(),"mms","2","4");
		// 清除当前用户缓存
		if (jmsgAccount.getUser().getLoginName().equals(UserUtils.getUser().getLoginName())){
			UserUtils.clearCache();
		}
		addMessage(redirectAttributes, "保存彩信账户信息成功");
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/agencyList";
	}
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "agencySmsSave")
	public String agencySmsSave(JmsgAccount jmsgAccount,String oldLoginName,String newPassword, Model model, RedirectAttributes redirectAttributes) {
		
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(newPassword)) {
			jmsgAccount.getUser().setPassword(SystemService.entryptPassword(newPassword));
		}
		if (!"true".equals(checkLoginName(oldLoginName, jmsgAccount.getUser().getLoginName()))){
			addMessage(model, "保存用户'" + jmsgAccount.getUser().getLoginName() + "'失败，登录名已存在");
			return agencyForm(jmsgAccount, model);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		Role role = new Role();
		role.setId(Global.getConfig("sms.commom.role.id"));//普通用户角色ID
		roleList.add(role);
		
		jmsgAccount.getUser().setRoleList(roleList);
		// 保存用户信息
		jmsgAccount.getUser().setLoginFlag("1");
		User loginUser = UserUtils.getUser();
		jmsgAccount.getUser().setGroupId(loginUser.getGroupId());//分组ID
		jmsgAccount.getUser().setCompany(loginUser.getCompany());//公司ID
		
		systemService.saveUserByAgency(jmsgAccount.getUser(),"sms","2","4");//默认按照状态报告扣费
		// 清除当前用户缓存
		if (jmsgAccount.getUser().getLoginName().equals(loginUser.getLoginName())){
			UserUtils.clearCache();
		}
		addMessage(redirectAttributes, "保存短信账户信息成功");
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/agencySmsList";
	}	
	
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgAccount jmsgAccount, RedirectAttributes redirectAttributes) {
		jmsgAccountService.delete(jmsgAccount);
		addMessage(redirectAttributes, "删除资金账户信息成功");
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/?repage";
	}
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "agencyDelete")
	public String agencyDelete(JmsgAccount jmsgAccount,String redirect, RedirectAttributes redirectAttributes) {
		String usedFlag = jmsgAccount.getUsedFlag();
		String msg = "";
		if("1".equals(usedFlag)){
			msg = "禁用账户成功";
			jmsgAccount.setUsedFlag("0");
		}else{
			msg = "启用账户成功";
			jmsgAccount.setUsedFlag("1");
		}
		
//		jmsgAccount.getUser().setDelFlag(delFlag);
//		jmsgAccount.getUser().setCreateBy(new User(UserUtils.getUser().getId()));
//		
//		systemService.deleteByAgency(jmsgAccount.getUser());
		
		jmsgAccountService.agencyDelete(jmsgAccount);
		systemService.dubboCache(jmsgAccount.getUser().getId());
		addMessage(redirectAttributes, msg);
		if("1".equals(redirect))return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/list";
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/agencyList";
	}
	
	@RequiresPermissions("account:jmsgAccount:edit")
	@RequestMapping(value = "agencySmsDelete")
	public String agencySmsDelete(JmsgAccount jmsgAccount,String redirect, RedirectAttributes redirectAttributes) {
		String oldUsedFlag = jmsgAccount.getUsedFlag();
		String usedFlag = "0";
//		String delFlag = "1";
		String msg = "禁用账户成功";
		if("0".equals(oldUsedFlag)){
			msg = "启用账户成功";
			usedFlag = "1";
//			delFlag="0";
		}
		
//		jmsgAccount.getUser().setDelFlag(delFlag);
//		jmsgAccount.getUser().setCreateBy(new User(UserUtils.getUser().getId()));
//		
//		systemService.deleteByAgency(jmsgAccount.getUser());
		
		jmsgAccount.setUsedFlag(usedFlag);
		jmsgAccountService.agencyDelete(jmsgAccount);
		systemService.dubboCache(jmsgAccount.getUser().getId());
		addMessage(redirectAttributes, msg);
		if("1".equals(redirect))return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/list";
		return "redirect:"+Global.getAdminPath()+"/account/jmsgAccount/agencySmsList";
	}	
	
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}
	
	@RequestMapping(value = "balance")
	public String showAccountMoney(String userType, HttpServletRequest request, HttpServletResponse response){
		return renderString(response, String.valueOf(balance(UserUtils.getUser().getId(), userType)), "application/json");
	}
	
	@RequestMapping(value = "showAccount")
	public String showAccount(String userId,Model model) {
		String key = AccountCacheUtils.getAmountKey("sms", userId);
		String amount = JedisClusterUtils.get(key);
		model.addAttribute("amount", amount);
		return "modules/account/jmsgAccountAmount";
	}
	
}