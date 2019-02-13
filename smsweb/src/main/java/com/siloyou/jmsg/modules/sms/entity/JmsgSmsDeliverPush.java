/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 上行推送信息Entity
 * @author zhukc
 * @version 2016-08-14
 */
public class JmsgSmsDeliverPush extends DataEntity<JmsgSmsDeliverPush> {
	
	private static final long serialVersionUID = 1L;
	private String uuid;			// 随机ID
	private User user;				// 用户ID
	private String msgid;			// 消息流水号
	private String srcTermId;		// 接入号
	private String destTermId;		// 上行手机号
	private String msgContent;		// 短信内容
	private String gatewayId;		// 通道ID
	private String result;			// 结果
	private Date createtime;		// 创建时间
	private Date createtimeQ;
	private Date createtimeZ;
	private String pushFlag;		// 推送标识(1:推送 0:不用推送)
	private String userName;
	private int rPageSize;
	private int rPageNo;

	public int getrPageSize() {
		return rPageSize;
	}

	public void setrPageSize(int rPageSize) {
		this.rPageSize = rPageSize;
	}

	public int getrPageNo() {
		return rPageNo;
	}

	public void setrPageNo(int rPageNo) {
		this.rPageNo = rPageNo;
	}

	public String getUserName()
    {
	    userName = this.user.getName();
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Date getCreatetimeQ()
    {
        return createtimeQ;
    }

    public void setCreatetimeQ(Date createtimeQ)
    {
        this.createtimeQ = createtimeQ;
    }

    public Date getCreatetimeZ()
    {
        return createtimeZ;
    }

    public void setCreatetimeZ(Date createtimeZ)
    {
        this.createtimeZ = createtimeZ;
    }

    public JmsgSmsDeliverPush() {
		super();
	}

	public JmsgSmsDeliverPush(String id){
		super(id);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	@ExcelField(title="接入号", align=2, sort=20)
	public String getSrcTermId() {
		return srcTermId;
	}

	public void setSrcTermId(String srcTermId) {
		this.srcTermId = srcTermId;
	}
	
	@ExcelField(title="上行手机号", align=2, sort=25)
	public String getDestTermId() {
		return destTermId;
	}

	public void setDestTermId(String destTermId) {
		this.destTermId = destTermId;
	}
	
	@ExcelField(title="短信内容", align=2, sort=30)
	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="创建时间", align=2, sort=40)
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public String getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(String pushFlag) {
		this.pushFlag = pushFlag;
	}
	
}