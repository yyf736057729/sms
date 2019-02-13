/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.core.modules.sys.entity;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.supcan.annotation.treelist.cols.SupCol;
import com.siloyou.core.common.utils.Collections3;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;
import com.siloyou.core.common.utils.excel.fieldtype.RoleListType;

/**
 * 用户Entity
 * @author ThinkGem
 * @version 2013-12-05
 */
public class User extends DataEntity<User> {

	private static final long serialVersionUID = 1L;
	private Office company;	// 归属公司
	private Office office;	// 归属部门
	private String loginName;// 登录名
	private String password;// 密码
	private String no;		// 工号
	private String name;	// 姓名
	private String email;	// 邮箱
	private String phone;	// 电话
	private String mobile;	// 手机
	private String userType;// 用户类型
	private String loginIp;	// 最后登陆IP
	private Date loginDate;	// 最后登陆日期
	private String loginFlag;	// 是否允许登陆
	private String photo;	// 头像
	private String roleId; 	//角色id

	private String oldLoginName;// 原登录名
	private String newPassword;	// 新密码
	
	private String oldLoginIp;	// 上次登陆IP
	private Date oldLoginDate;	// 上次登陆日期
	
	private Role role;	// 根据角色查询用户条件
	private String payMode;	//扣款方式
	private String mmsfrom;	//接入号
	private String callbackUrl;//回调地址
	private int rspContentType;    //响应内容类型 0：xml 1：json
	private String noCheck;//免审
	private String apikey;//
	private String whiteIP;//IP白名单
	private String upUrl;//上行推送地址
	private String groupId;//分组ID
	private int reviewCount;//审核条数
	private Integer signFlag;//是否校验签名
	private String keyword;//关键字
	private String qq;         //QQ
	private String companyName;//公司名称
	private String contactName;//联系人
	private String price;      //短信价格（元/条）
	private Integer sysBlacklistFlag;  //是否校验系统黑名单
	private Integer userBlacklistFlag; //是否校验群发黑名单
	private String contentRule; //内容规则配置
	private String balanceCaution; //余额提醒（条数）
	private String cautionMobile;  //余额提醒号码
	private int interfaceFlag;     //是否开通接口
	private String filterWordFlag;//是否过滤敏感词
	private int cmppUserType;//cmpp用户类型 	1:三网合一 0:单一
	private String forceSign;//强制签名
	private String extnumYd;//移动
	private String extnumLt;
	private String extnumDx;
	private int userMonthLimit;//限额 1:不限额 0:限额
	private int sendLimit;//限制发送频次 1:限制 0:不限制
	private String ruleGroupId;	//短信内容校验规则分组ID
	private int payType;	//用户付费类型 0：预付费 1：后付费
	private int yzmGatewayFlag;//验证码通道 1:是 0:否
	private String allnumPush;//是否全号推送
	private int substringLength;//截取长度
	private int yzmSendCount;//验证码发送次数
	private int httpSpeed;//http速率
	private String marketingControl;//营销频控
	private int usedSign;
	private String sendTag;
	private String firstSign;//签名导流优先
	private String marketBlacklistFlag;//校验营销黑名单
	private String yzmBlacklistFlag;//校验行业黑名单
	private String contentMgIdOne;//内容控制策略1
	private String contentMgIdTwo;//内容控制策略2
	private String userTmplFlag;//内容导流优先（0无，1模板优先，2关键字优先）

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserTmplFlag() {
		return userTmplFlag;
	}

	public void setUserTmplFlag(String userTmplFlag) {
		this.userTmplFlag = userTmplFlag;
	}

	public String getContentMgIdOne() {
		return contentMgIdOne;
	}

	public void setContentMgIdOne(String contentMgIdOne) {
		this.contentMgIdOne = contentMgIdOne;
	}

	public String getContentMgIdTwo() {
		return contentMgIdTwo;
	}

	public void setContentMgIdTwo(String contentMgIdTwo) {
		this.contentMgIdTwo = contentMgIdTwo;
	}

	public String getMarketBlacklistFlag() {
		return marketBlacklistFlag;
	}

	public void setMarketBlacklistFlag(String marketBlacklistFlag) {
		this.marketBlacklistFlag = marketBlacklistFlag;
	}

	public String getYzmBlacklistFlag() {
		return yzmBlacklistFlag;
	}

	public void setYzmBlacklistFlag(String yzmBlacklistFlag) {
		this.yzmBlacklistFlag = yzmBlacklistFlag;
	}

	public String getFirstSign() {
		return firstSign;
	}

	public void setFirstSign(String firstSign) {
		this.firstSign = firstSign;
	}

	public String getSendTag() {
		return sendTag;
	}

	public void setSendTag(String sendTag) {
		this.sendTag = sendTag;
	}

	public int getUsedSign() {
		return usedSign;
	}

	public void setUsedSign(int usedSign) {
		this.usedSign = usedSign;
	}

	public String getMarketingControl() {
		return marketingControl;
	}

	public void setMarketingControl(String marketingControl) {
		this.marketingControl = marketingControl;
	}

	public int getHttpSpeed() {
		return httpSpeed;
	}

	public void setHttpSpeed(int httpSpeed) {
		this.httpSpeed = httpSpeed;
	}

	public int getYzmSendCount() {
		return yzmSendCount;
	}

	public void setYzmSendCount(int yzmSendCount) {
		this.yzmSendCount = yzmSendCount;
	}

	public String getAllnumPush() {
		return allnumPush;
	}

	public void setAllnumPush(String allnumPush) {
		this.allnumPush = allnumPush;
	}

	public int getSubstringLength() {
		return substringLength;
	}

	public void setSubstringLength(int substringLength) {
		this.substringLength = substringLength;
	}

	public int getYzmGatewayFlag() {
		return yzmGatewayFlag;
	}

	public void setYzmGatewayFlag(int yzmGatewayFlag) {
		this.yzmGatewayFlag = yzmGatewayFlag;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getRuleGroupId() {
		return ruleGroupId;
	}

	public void setRuleGroupId(String ruleGroupId) {
		this.ruleGroupId = ruleGroupId;
	}

	public int getSendLimit() {
		return sendLimit;
	}

	public void setSendLimit(int sendLimit) {
		this.sendLimit = sendLimit;
	}

	public int getCmppUserType() {
		return cmppUserType;
	}

	public void setCmppUserType(int cmppUserType) {
		this.cmppUserType = cmppUserType;
	}

	public String getForceSign() {
		return forceSign;
	}

	public void setForceSign(String forceSign) {
		this.forceSign = forceSign;
	}

	public String getExtnumYd() {
		return extnumYd;
	}

	public void setExtnumYd(String extnumYd) {
		this.extnumYd = extnumYd;
	}

	public String getExtnumLt() {
		return extnumLt;
	}

	public void setExtnumLt(String extnumLt) {
		this.extnumLt = extnumLt;
	}

	public String getExtnumDx() {
		return extnumDx;
	}

	public void setExtnumDx(String extnumDx) {
		this.extnumDx = extnumDx;
	}

	public int getUserMonthLimit() {
		return userMonthLimit;
	}

	public void setUserMonthLimit(int userMonthLimit) {
		this.userMonthLimit = userMonthLimit;
	}

	public String getFilterWordFlag() {
		return filterWordFlag;
	}

	public void setFilterWordFlag(String filterWordFlag) {
		this.filterWordFlag = filterWordFlag;
	}

	public int getRspContentType()
    {
        return rspContentType;
    }

    public void setRspContentType(int rspContentType)
    {
        this.rspContentType = rspContentType;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getCautionMobile()
    {
        return cautionMobile;
    }

    public void setCautionMobile(String cautionMobile)
    {
        this.cautionMobile = cautionMobile;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getContentRule()
    {
        return contentRule;
    }

    public void setContentRule(String contentRule)
    {
        this.contentRule = contentRule;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public Integer getSignFlag() {
		return signFlag;
	}

	public void setSignFlag(Integer signFlag) {
		this.signFlag = signFlag;
	}

	public Integer getSysBlacklistFlag() {
		return sysBlacklistFlag;
	}

	public void setSysBlacklistFlag(Integer sysBlacklistFlag) {
		this.sysBlacklistFlag = sysBlacklistFlag;
	}

	public Integer getUserBlacklistFlag() {
		return userBlacklistFlag;
	}

	public void setUserBlacklistFlag(Integer userBlacklistFlag) {
		this.userBlacklistFlag = userBlacklistFlag;
	}

	public String getBalanceCaution()
    {
        return balanceCaution;
    }

    public void setBalanceCaution(String balanceCaution)
    {
        this.balanceCaution = balanceCaution;
    }

    public int getInterfaceFlag()
    {
        return interfaceFlag;
    }

    public void setInterfaceFlag(int interfaceFlag)
    {
        this.interfaceFlag = interfaceFlag;
    }

    public String getQq()
    {
        return qq;
    }

    public void setQq(String qq)
    {
        this.qq = qq;
    }

    public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

    public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public String getUpUrl() {
		return upUrl;
	}

	public void setUpUrl(String upUrl) {
		this.upUrl = upUrl;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getNoCheck() {
		return noCheck;
	}

	public void setNoCheck(String noCheck) {
		this.noCheck = noCheck;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public String getWhiteIP() {
		return whiteIP;
	}

	public void setWhiteIP(String whiteIP) {
		this.whiteIP = whiteIP;
	}

	public String getMmsfrom() {
		return mmsfrom;
	}

	public void setMmsfrom(String mmsfrom) {
		this.mmsfrom = mmsfrom;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

	public User() {
		super();
		this.loginFlag = Global.YES;
	}
	
	public User(String id){
		super(id);
	}

	public User(String id, String loginName){
		super(id);
		this.loginName = loginName;
	}

	public User(Role role){
		super();
		this.role = role;
	}
	
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

	@SupCol(isUnique="true", isHide="true")
	@ExcelField(title="ID", type=1, align=2, sort=1)
	public String getId() {
		return id;
	}

	@JsonIgnore
	@NotNull(message="归属公司不能为空")
	@ExcelField(title="归属公司", align=2, sort=20)
	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}
	
	@JsonIgnore
	@ExcelField(title="归属部门", align=2, sort=25)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Length(min=1, max=100, message="登录名长度必须介于 1 和 100 之间")
	@ExcelField(title="登录名", align=2, sort=30)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@JsonIgnore
	@Length(min=1, max=100, message="密码长度必须介于 1 和 100 之间")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Length(min=1, max=100, message="姓名长度必须介于 1 和 100 之间")
	@ExcelField(title="姓名", align=2, sort=40)
	public String getName() {
		return name;
	}
	
	@Length(min=1, max=100, message="工号长度必须介于 1 和 100 之间")
	@ExcelField(title="工号", align=2, sort=45)
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Email(message="邮箱格式不正确")
	@Length(min=0, max=200, message="邮箱长度必须介于 1 和 200 之间")
	@ExcelField(title="邮箱", align=1, sort=50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Length(min=0, max=200, message="电话长度必须介于 1 和 200 之间")
	@ExcelField(title="电话", align=2, sort=60)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min=0, max=200, message="手机长度必须介于 1 和 200 之间")
	@ExcelField(title="手机", align=2, sort=70)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@ExcelField(title="备注", align=1, sort=900)
	public String getRemarks() {
		return remarks;
	}
	
	@Length(min=0, max=100, message="用户类型长度必须介于 1 和 100 之间")
	@ExcelField(title="用户类型", align=2, sort=80, dictType="sys_user_type")
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@ExcelField(title="创建时间", type=0, align=1, sort=90)
	public Date getCreateDate() {
		return createDate;
	}

	@ExcelField(title="最后登录IP", type=1, align=1, sort=100)
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="最后登录日期", type=1, align=1, sort=110)
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getOldLoginName() {
		return oldLoginName;
	}

	public void setOldLoginName(String oldLoginName) {
		this.oldLoginName = oldLoginName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldLoginIp() {
		if (oldLoginIp == null){
			return loginIp;
		}
		return oldLoginIp;
	}

	public void setOldLoginIp(String oldLoginIp) {
		this.oldLoginIp = oldLoginIp;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOldLoginDate() {
		if (oldLoginDate == null){
			return loginDate;
		}
		return oldLoginDate;
	}

	public void setOldLoginDate(Date oldLoginDate) {
		this.oldLoginDate = oldLoginDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@JsonIgnore
	@ExcelField(title="拥有角色", align=1, sort=800, fieldType=RoleListType.class)
	public List<Role> getRoleList() {
		return roleList;
	}
	
	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	@JsonIgnore
	public List<String> getRoleIdList() {
		List<String> roleIdList = Lists.newArrayList();
		for (Role role : roleList) {
			roleIdList.add(role.getId());
		}
		return roleIdList;
	}

	public void setRoleIdList(List<String> roleIdList) {
		roleList = Lists.newArrayList();
		for (String roleId : roleIdList) {
			Role role = new Role();
			role.setId(roleId);
			roleList.add(role);
		}
	}
	
	/**
	 * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
	 */
	public String getRoleNames() {
		return Collections3.extractToString(roleList, "name", ",");
	}
	
	public boolean isAdmin(){
		return isAdmin(this.id);
	}
	
	public static boolean isAdmin(String id){
		return id != null && "1".equals(id);
	}
	
	@Override
	public String toString() {
		return id;
	}
}