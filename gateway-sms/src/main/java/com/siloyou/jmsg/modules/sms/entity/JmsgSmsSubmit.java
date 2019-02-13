/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

/**
 * 网关状态Entity
 * @author zhukc
 * @version 2016-08-05
 */
public class JmsgSmsSubmit extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	private String msgid;		// 网关ID
	private String result;		// 网关状态0：成功 其他失败
	private String bizid;		// 业务ID（对应jmsg_sms_send中的id）
	private Date createtime;		// createtime
	private String taskid;		// 任务ID（对应jmsg_sms_send中的taskid）
	private String userid;
	private String gatewayid;
	private String reserve;
	private String phone;
	
	public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getReserve()
    {
        return reserve;
    }

    public void setReserve(String reserve)
    {
        this.reserve = reserve;
    }

    public JmsgSmsSubmit() {
		super();
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}
	
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getGatewayid()
    {
        return gatewayid;
    }

    public void setGatewayid(String gatewayid)
    {
        this.gatewayid = gatewayid;
    }
}