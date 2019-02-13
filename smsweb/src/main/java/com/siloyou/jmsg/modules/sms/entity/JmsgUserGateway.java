/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 用户通道Entity
 * @author zhukc
 * @version 2016-08-28
 */
public class JmsgUserGateway extends DataEntity<JmsgUserGateway> {
	
	private static final long serialVersionUID = 1L;
	private User user;					// 用户
	private String userid;				// 用户ID
	private String groupid;				// 分组
	private String username;			// 用户名称
	private String password;			// 密码
	private String spnumber;			// 接入号
	private String version;				// 版本
	private String maxChannels;			// 最大连接数
	private String retryWaitTime;		// 重试间隔(秒)
	private String maxRetryCnt;			// 最大重试次数
	private String resendFailmsg;		// 是否重发失败消息(0 否 1是)
	private String readLimit;			// 读取限制
	private String writeLimit;			// 写入限制
	private Date createtime;			// 创建时间
	private Date createtimeQ;			// 创建时间起
	private Date createtimeZ;			// 创建时间止
	private String status;              // 运行状态
	
	private String serviceId;           // 服务代码
	private String allowIP;             // 绑定IP
	
	private String appHost;            // 应用IP
	private String appCode;            // 应用代码
	
	private int allnumPush;			   // 是否全号推送  0全号， 1截取匹配长度， 2截取固定长度
	private int substringLength;	   // 截取长度
	
	private String fromType;		   // 接入号类型
	
    public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	public int getAllnumPush() {
		return allnumPush;
	}

	public void setAllnumPush(int allnumPush) {
		this.allnumPush = allnumPush;
	}

	public int getSubstringLength() {
		return substringLength;
	}

	public void setSubstringLength(int substringLength) {
		this.substringLength = substringLength;
	}

	public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getAppHost()
    {
        return appHost;
    }

    public void setAppHost(String appHost)
    {
        this.appHost = appHost;
    }

    public String getAppCode()
    {
        return appCode;
    }

    public void setAppCode(String appCode)
    {
        this.appCode = appCode;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getAllowIP()
    {
        return allowIP;
    }

    public void setAllowIP(String allowIP)
    {
        this.allowIP = allowIP;
    }

    public Date getCreatetimeQ() {
		return createtimeQ;
	}

	public void setCreatetimeQ(Date createtimeQ) {
		this.createtimeQ = createtimeQ;
	}

	public Date getCreatetimeZ() {
		return createtimeZ;
	}

	public void setCreatetimeZ(Date createtimeZ) {
		this.createtimeZ = createtimeZ;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JmsgUserGateway() {
		super();
	}

	public JmsgUserGateway(String id){
		super(id);
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSpnumber() {
		return spnumber;
	}

	public void setSpnumber(String spnumber) {
		this.spnumber = spnumber;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getMaxChannels() {
		return maxChannels;
	}

	public void setMaxChannels(String maxChannels) {
		this.maxChannels = maxChannels;
	}
	
	public String getRetryWaitTime() {
		return retryWaitTime;
	}

	public void setRetryWaitTime(String retryWaitTime) {
		this.retryWaitTime = retryWaitTime;
	}
	
	public String getMaxRetryCnt() {
		return maxRetryCnt;
	}

	public void setMaxRetryCnt(String maxRetryCnt) {
		this.maxRetryCnt = maxRetryCnt;
	}
	
	public String getResendFailmsg() {
		return resendFailmsg;
	}

	public void setResendFailmsg(String resendFailmsg) {
		this.resendFailmsg = resendFailmsg;
	}
	
	public String getReadLimit() {
		return readLimit;
	}

	public void setReadLimit(String readLimit) {
		this.readLimit = readLimit;
	}
	
	public String getWriteLimit() {
		return writeLimit;
	}

	public void setWriteLimit(String writeLimit) {
		this.writeLimit = writeLimit;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}