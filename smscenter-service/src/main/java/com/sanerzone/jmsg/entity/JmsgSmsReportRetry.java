/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 状态报告重试Entity
 * @author zhukc
 * @version 2016-08-09
 */
public class JmsgSmsReportRetry extends DataEntity<JmsgSmsReportRetry> {
	
	private static final long serialVersionUID = 1L;
	private String gatewayId;			// 通道ID(网关ID)
	private String msgid;				// 网关ID
	private String stat;				// 接收状态
	private Long submitTime;			// 提交时间
	private Long doneTime;				// 接收时间
	private String srcTermId;			// 发送号码
	private String destTermId;			// 接收号码
	private String smscSequence;		// 网关侧序号
	private Date createtime;			// 创建时间
	
	private int sourceFlag;             //来源标示 0：MT为空 1：send未入库
	private String bizid;
	private String tableName;			//查询的数据表名称
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getSourceFlag()
    {
        return sourceFlag;
    }

    public void setSourceFlag(int sourceFlag)
    {
        this.sourceFlag = sourceFlag;
    }

    public String getBizid()
    {
        return bizid;
    }

    public void setBizid(String bizid)
    {
        this.bizid = bizid;
    }

    public JmsgSmsReportRetry() {
		super();
	}

	public JmsgSmsReportRetry(String id){
		super(id);
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
	
	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitTime) {
		this.submitTime = submitTime;
	}
	
	public Long getDoneTime() {
		return doneTime;
	}

	public void setDoneTime(Long doneTime) {
		this.doneTime = doneTime;
	}
	
	public String getSrcTermId() {
		return srcTermId;
	}

	public void setSrcTermId(String srcTermId) {
		this.srcTermId = srcTermId;
	}
	
	public String getDestTermId() {
		return destTermId;
	}

	public void setDestTermId(String destTermId) {
		this.destTermId = destTermId;
	}
	
	public String getSmscSequence() {
		return smscSequence;
	}

	public void setSmscSequence(String smscSequence) {
		this.smscSequence = smscSequence;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}