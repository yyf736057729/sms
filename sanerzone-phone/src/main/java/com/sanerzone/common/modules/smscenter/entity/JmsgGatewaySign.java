/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.entity;

import java.util.Date;

import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 通道签名Entity
 * @author zhukc
 * @version 2016-07-29
 */
public class JmsgGatewaySign extends DataEntity<JmsgGatewaySign> {
	
	private static final long serialVersionUID = 1L;
	private String userId;          // 用户ID
	private String gatewayId;		// 通道ID
	private String gatewayName;		// 通道
	private String sign;			// 签名
	private String spNumber;		// 接入号
	private String extNumber;       // 扩展号
	private Date createTime;
	private Date createTimeQ;
	private Date createTimeZ;
	private String note;
	
	public Date getCreateTimeQ()
    {
        return createTimeQ;
    }

    public void setCreateTimeQ(Date createTimeQ)
    {
        this.createTimeQ = createTimeQ;
    }

    public Date getCreateTimeZ()
    {
        return createTimeZ;
    }

    public void setCreateTimeZ(Date createTimeZ)
    {
        this.createTimeZ = createTimeZ;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public String getExtNumber()
    {
        return extNumber;
    }

    public void setExtNumber(String extNumber)
    {
        this.extNumber = extNumber;
    }

    public JmsgGatewaySign() {
		super();
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public JmsgGatewaySign(String id){
		super(id);
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}
	
}