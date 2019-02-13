/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 通道信息Entity
 * @author zhukc
 * @version 2016-07-29
 */
public class JmsgGatewayInfo extends DataEntity<JmsgGatewayInfo> {


	private static final long serialVersionUID = 1L;
	private String gatewayName;				// 网关名字
	private String gatewayState;			// 网关状态
	private String type;					// 网关类型
	private String spNumber;				// 接入号
	private String host;					// 服务器IP
	private int port;						// 服务器端口
	private String localHost;				// 本地主机
	private int localPort;					// 本地端口
	private String sourceAddr;				// 用户名
	private String sharedSecret;			// 密码
	private String version;					// 协议版本号
	private int readTimeout;				// 读取超时
	private int reconnectInterval;			// 重连间隔时间
	private int transactionTimeout;			// 发送消息的操作时间
	private int heartbeatInterval;			// 心跳间隔时间
	private int heartbeatNoresponseout;		// 心跳失败次数
	private int debug;						// 调试
	private String corpId;					// 消息来源,如msg_src
	private String status;					// status
	private Date createTime;				// create_time
	private Date createTimeQ;
	private Date createTimeZ;
	private Date modifieTime;				// modifie_time
	private String isOutProv;				// 是否支持外省
	private String extClass;				// 协议实现类,HTTP协议才会用到
	private String extParam;				// 扩展参数,HTTP协议才会用到
	private String appHost;                // 应用IP
	private String appCode;					// 应用代码
	private int reportGetFlag;              // 状态获取方式 0：主动查询 1：异步通知
	private int gatewaySign;				// 网关签名 0:否 1:是
	private int supportLongMsg;				// 是否支持长短信
	private int readLimit;					// 接收速率
	private int writeLimit;					// 请求速率
	private String customFlag;				//是否支持自定义签名
	private int batchSize;                 //单次处理条数
	private String remark;
	private Map<String, String> params ;
	private String phoneType;				// 运营商(联通、移动、电信)
	//模板参数
	private String feeUserType;				//计费用户类型
	private String feeCode;					//资费代码
	private String feeType;					//资费类别
	private String feeTerminalId;			//被计费用户的号码
	private String feeTerminalType;			//被计费用户的号码类型
	private String gwCorpId;				//企业代码
	private String gwServiceId;				//业务代码
	private String serviceId;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
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

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	public JmsgGatewayInfo() {
		super();
	}

	public JmsgGatewayInfo(String id){
		super(id);
	}

	public String getAppHost()
	{
		return appHost;
	}

	public void setAppHost(String appHost)
	{
		this.appHost = appHost;
	}

	public int getReportGetFlag()
	{
		return reportGetFlag;
	}

	public void setReportGetFlag(int reportGetFlag)
	{
		this.reportGetFlag = reportGetFlag;
	}

	public String getCustomFlag() {
		return customFlag;
	}

	public void setCustomFlag(String customFlag) {
		this.customFlag = customFlag;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getReconnectInterval() {
		return reconnectInterval;
	}

	public void setReconnectInterval(int reconnectInterval) {
		this.reconnectInterval = reconnectInterval;
	}

	public int getTransactionTimeout() {
		return transactionTimeout;
	}

	public void setTransactionTimeout(int transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	public int getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(int heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public int getHeartbeatNoresponseout() {
		return heartbeatNoresponseout;
	}

	public void setHeartbeatNoresponseout(int heartbeatNoresponseout) {
		this.heartbeatNoresponseout = heartbeatNoresponseout;
	}

	public int getDebug() {
		return debug;
	}

	public void setDebug(int debug) {
		this.debug = debug;
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

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
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


	public String getLocalHost() {
		return localHost;
	}

	public void setLocalHost(String localHost) {
		this.localHost = localHost;
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

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

}