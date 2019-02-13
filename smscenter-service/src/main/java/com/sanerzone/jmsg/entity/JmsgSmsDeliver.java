/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.entity;

import com.sanerzone.common.support.persistence.DataEntity;
import com.siloyou.jmsg.common.message.SmsMoMessage;

/**
 * 短信上行Entity
 * @author zhukc
 * @version 2016-08-13
 */
public class JmsgSmsDeliver extends DataEntity<JmsgSmsDeliver> {
	
	private static final long serialVersionUID = 1L;
	
	private String userId;//用户ID
	private String upUrl;//上行地址
	private String result;//推送结果
	private String pushFlag;//推送标识 1：已推 0:不用推送
	private int rspContentType;    //响应内容类型 0：xml 1：json
	private SmsMoMessage moMsg;
	
	public int getRspContentType()
    {
        return rspContentType;
    }

    public void setRspContentType(int rspContentType)
    {
        this.rspContentType = rspContentType;
    }

    public String getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(String pushFlag) {
		this.pushFlag = pushFlag;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public JmsgSmsDeliver() {
		super();
	}

	public JmsgSmsDeliver(String id){
		super(id);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUpUrl() {
		return upUrl;
	}

	public void setUpUrl(String upUrl) {
		this.upUrl = upUrl;
	}

	public SmsMoMessage getMoMsg() {
		return moMsg;
	}

	public void setMoMsg(SmsMoMessage moMsg) {
		this.moMsg = moMsg;
	}
	
}