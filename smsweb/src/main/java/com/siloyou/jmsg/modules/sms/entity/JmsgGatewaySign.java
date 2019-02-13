/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;
import java.util.List;

import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 通道签名Entity
 * @author zhukc
 * @version 2016-07-29
 */
public class JmsgGatewaySign extends DataEntity<JmsgGatewaySign> {
	
	private static final long serialVersionUID = 1L;
	private User user;
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
	
	private List<JmsgGatewaySign> gatewaySignList;
	
	public List<JmsgGatewaySign> getGatewaySignList() {
		return gatewaySignList;
	}

	public void setGatewaySignList(List<JmsgGatewaySign> gatewaySignList) {
		this.gatewaySignList = gatewaySignList;
	}

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

    @ExcelField(title="用户ID", align=2, sort=1)
    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    @ExcelField(title="备注", align=2, sort=45)
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

	@ExcelField(title="通道代码", align=2, sort=20)
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	@ExcelField(title="签名", align=2, sort=40)
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	@ExcelField(title="扩展号", align=2, sort=25)
	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}
	
}