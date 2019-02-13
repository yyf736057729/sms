/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 网关状态Entity
 * @author zhukc
 * @version 2016-08-05
 */
public class JmsgSmsSubmit extends DataEntity<JmsgSmsSubmit> {
	
	private static final long serialVersionUID = 1L;
	private String msgid;      // 网关ID
    private String result;      // 网关状态0：成功 其他失败
    private String bizid;       // 业务ID（对应jmsg_sms_send中的id）
    private Date createtime;        // createtime
    private Date createtimeQ;        
    private Date createtimeZ;        
    private String taskid;      // 任务ID（对应jmsg_sms_send中的taskid）
    private String userid;
    private String gatewayid;
    private String reserve;
    private String phone;
    
    private String tableName;			//查询的数据表名称
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
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

    public String getReserve()
    {
        return reserve;
    }

    public void setReserve(String reserve)
    {
        this.reserve = reserve;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public JmsgSmsSubmit() {
		super();
	}

	public JmsgSmsSubmit(String id){
		super(id);
	}

	@Length(min=1, max=32, message="网关ID长度必须介于 1 和 32 之间")
	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	@Length(min=0, max=10, message="网关状态0：成功 其他失败长度必须介于 0 和 10 之间")
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@Length(min=1, max=32, message="业务ID（对应jmsg_sms_send中的id）长度必须介于 1 和 32 之间")
	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="createtime不能为空")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	@Length(min=1, max=32, message="任务ID（对应jmsg_sms_send中的taskid）长度必须介于 1 和 32 之间")
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
}