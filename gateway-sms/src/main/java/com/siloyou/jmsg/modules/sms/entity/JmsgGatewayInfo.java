/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 通道信息Entity
 * 
 * @author zhukc
 * @version 2016-07-29
 */
public class JmsgGatewayInfo extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	private String gatewayName; // 网关名字
	private String gatewayState; // 网关状态
	private String type; // 网关类型
	private String spNumber; // 接入号
	private String host; // 服务器IP
	private String port; // 服务器端口
	private String localHost; // 本地主机
	private String localPort; // 本地端口
	private String sourceAddr; // 用户名
	private String sharedSecret; // 密码
	private String version; // 协议版本号
	private String readTimeout; // 读取超时
	private String reconnectInterval; // 重连间隔时间
	private String transactionTimeout; // 发送消息的操作时间
	private String heartbeatInterval; // 心跳间隔时间
	private String heartbeatNoresponseout; // 心跳失败次数
	private String debug; // 调试
	private String corpId; // 消息来源,如msg_src
	private String status; // status
	private Date createTime; // create_time
	private Date createTimeQ;
	private Date createTimeZ;
	private Date modifieTime; // modifie_time
	private String isOutProv; // 是否支持外省
	private String extClass; // 协议实现类,HTTP协议才会用到
	private String extParam; // 扩展参数,HTTP协议才会用到
	private String appHost;                // 应用IP
	private String appCode;
	private int reportGetFlag;              // 状态获取方式 0：主动查询 1：异步通知
	private int gatewaySign;				// 网关签名 0:否 1:是
	private int supportLongMsg;				// 是否支持长短信
	private int readLimit;					// 接收速率
	private int writeLimit;					// 请求速率
	private String customFlag;				//是否支持自定义签名
	private int batchSize;                 //单次处理条数
	private String remark;
	private Map<String, String> params ;
	//模板参数
	private String feeUserType;				//计费用户类型
	private String feeCode;					//资费代码
	private String feeType;					//资费类别
	private String feeTerminalId;			//被计费用户的号码
	private String feeTerminalType;			//被计费用户的号码类型
	private String gwCorpId;				//企业代码
	private String gwServiceId;				//业务代码
	private String phoneType;				//运营商类型
	private String serviceId;

	private String remarks;
	private String	updateBy;
	private Date updateDate;
	private String	delFlag;

	public String getAppHost() {
		return appHost;
	}

	public void setAppHost(String appHost) {
		this.appHost = appHost;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getFeeUserType() {
		return feeUserType;
	}

	public void setFeeUserType(String feeUserType) {
		this.feeUserType = feeUserType;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeTerminalId() {
		return feeTerminalId;
	}

	public void setFeeTerminalId(String feeTerminalId) {
		this.feeTerminalId = feeTerminalId;
	}

	public String getFeeTerminalType() {
		return feeTerminalType;
	}

	public void setFeeTerminalType(String feeTerminalType) {
		this.feeTerminalType = feeTerminalType;
	}

	public String getGwCorpId() {
		return gwCorpId;
	}

	public void setGwCorpId(String gwCorpId) {
		this.gwCorpId = gwCorpId;
	}

	public String getGwServiceId() {
		return gwServiceId;
	}

	public void setGwServiceId(String gwServiceId) {
		this.gwServiceId = gwServiceId;
	}

	public Date getCreateTimeQ() {
		return createTimeQ;
	}

	public void setCreateTimeQ(Date createTimeQ) {
		this.createTimeQ = createTimeQ;
	}

	public Date getCreateTimeZ() {
		return createTimeZ;
	}

	public void setCreateTimeZ(Date createTimeZ) {
		this.createTimeZ = createTimeZ;
	}

	public int getReportGetFlag() {
		return reportGetFlag;
	}

	public void setReportGetFlag(int reportGetFlag) {
		this.reportGetFlag = reportGetFlag;
	}

	public int getGatewaySign() {
		return gatewaySign;
	}

	public void setGatewaySign(int gatewaySign) {
		this.gatewaySign = gatewaySign;
	}

	public int getSupportLongMsg() {
		return supportLongMsg;
	}

	public void setSupportLongMsg(int supportLongMsg) {
		this.supportLongMsg = supportLongMsg;
	}

	public int getReadLimit() {
		return readLimit;
	}

	public void setReadLimit(int readLimit) {
		this.readLimit = readLimit;
	}

	public int getWriteLimit() {
		return writeLimit;
	}

	public void setWriteLimit(int writeLimit) {
		this.writeLimit = writeLimit;
	}

	public String getCustomFlag() {
		return customFlag;
	}

	public void setCustomFlag(String customFlag) {
		this.customFlag = customFlag;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public JmsgGatewayInfo() {
		super();
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getGatewayState() {
		return gatewayState;
	}

	public void setGatewayState(String gatewayState) {
		this.gatewayState = gatewayState;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLocalHost() {
		return localHost;
	}

	public void setLocalHost(String localHost) {
		this.localHost = localHost;
	}

	public String getLocalPort() {
		return localPort;
	}

	public void setLocalPort(String localPort) {
		this.localPort = localPort;
	}

	public String getSourceAddr() {
		return sourceAddr;
	}

	public void setSourceAddr(String sourceAddr) {
		this.sourceAddr = sourceAddr;
	}

	public String getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(String readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getReconnectInterval() {
		return reconnectInterval;
	}

	public void setReconnectInterval(String reconnectInterval) {
		this.reconnectInterval = reconnectInterval;
	}

	public String getTransactionTimeout() {
		return transactionTimeout;
	}

	public void setTransactionTimeout(String transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	public String getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(String heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public String getHeartbeatNoresponseout() {
		return heartbeatNoresponseout;
	}

	public void setHeartbeatNoresponseout(String heartbeatNoresponseout) {
		this.heartbeatNoresponseout = heartbeatNoresponseout;
	}

	public String getDebug() {
		return debug;
	}

	public void setDebug(String debug) {
		this.debug = debug;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getModifieTime() {
		return modifieTime;
	}

	public void setModifieTime(Date modifieTime) {
		this.modifieTime = modifieTime;
	}

	public String getIsOutProv() {
		return isOutProv;
	}

	public void setIsOutProv(String isOutProv) {
		this.isOutProv = isOutProv;
	}

	public String getExtClass() {
		return extClass;
	}

	public void setExtClass(String extClass) {
		this.extClass = extClass;
	}

	public String getExtParam() {
		return extParam;
	}

	public void setExtParam(String extParam) {
		this.extParam = extParam;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
	

}