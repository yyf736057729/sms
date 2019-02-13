package com.siloyou.jmsg.gateway.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JmsgGateWayInfo {
    private String  id;

    private String  gatewayName;

    private String  gatewayState;

    private String  type;

    private String  spNumber;

    private String  host;

    private Integer port;

    private String  localHost;

    private Integer localPort;

    private String  sourceAddr;

    private String  sharedSecret;

    private String  version;

    private Integer readTimeout;

    private Integer reconnectInterval;

    private Integer transactionTimeout;

    private Integer heartbeatInterval;

    private Integer heartbeatNoresponseout;

    private Integer debug;

    private String  corpId;

    private String  status;

    private Date    gmtCreated;

    private Date    gmtModified;

    private String  isOutProv;
    
    private String appCode;
    
    private int gatewaySign;				// 网关签名 0:否 1:是
	private int supportLongMsg;				// 是否支持长短信
	private int readLimit;					// 接收速率
	private int writeLimit;					// 请求速率
	private String appHost;                // 应用IP
	private int reportGetFlag;              // 状态获取方式 0：主动查询 1：异步通知
	
	private String extClass;
	
	private String extParam;	
	
	private boolean isWholeSpNumber;           //是否完整SP接入号
	
	private Map<String, String> param;
	
	private String serviceId;

    //模板参数
    private String feeUserType;				//计费用户类型
    private String feeCode;					//资费代码
    private String feeType;					//资费类别
    private String feeTerminalId;			//被计费用户的号码
    private String feeTerminalType;			//被计费用户的号码类型

    private String gwCorpId;				//企业代码
    private String gwServiceId;				//业务代码
    private String phoneType;				//运营商类型


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

    public boolean isWholeSpNumber()
    {
        return isWholeSpNumber;
    }

    public void setWholeSpNumber(boolean isWholeSpNumber)
    {
        this.isWholeSpNumber = isWholeSpNumber;
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

    public String getExtClass()
    {
        return extClass;
    }

    public void setExtClass(String extClass)
    {
        this.extClass = extClass;
    }

    public String getExtParam()
    {
        return extParam;
    }

    public void setExtParam(String extParam)
    {
        this.extParam = extParam;
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

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName == null ? null : gatewayName.trim();
    }

    public String getGatewayState() {
        return gatewayState;
    }

    public void setGatewayState(String gatewayState) {
        this.gatewayState = gatewayState == null ? null : gatewayState.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getSpNumber() {
        return spNumber;
    }

    public void setSpNumber(String spNumber) {
        this.spNumber = spNumber == null ? null : spNumber.trim();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host == null ? null : host.trim();
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost == null ? null : localHost.trim();
    }

    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr == null ? null : sourceAddr.trim();
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret == null ? null : sharedSecret.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(Integer reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public Integer getTransactionTimeout() {
        return transactionTimeout;
    }

    public void setTransactionTimeout(Integer transactionTimeout) {
        this.transactionTimeout = transactionTimeout;
    }

    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public Integer getHeartbeatNoresponseout() {
        return heartbeatNoresponseout;
    }

    public void setHeartbeatNoresponseout(Integer heartbeatNoresponseout) {
        this.heartbeatNoresponseout = heartbeatNoresponseout;
    }

    public Integer getDebug() {
        return debug;
    }

    public void setDebug(Integer debug) {
        this.debug = debug;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId == null ? null : corpId.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getIsOutProv() {
        return isOutProv;
    }

    public void setIsOutProv(String isOutProv) {
        this.isOutProv = isOutProv == null ? null : isOutProv.trim();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    //参数转型
	public Map<String, String> getParam() {
        param = new HashMap<>();
        if(feeUserType!=null&&!"".equals(feeUserType)){
            param.put("feeUserType",feeUserType);
        }
        if(feeCode!=null&&!"".equals(feeCode)){
            param.put("feeCode",feeCode);
        }
        if(feeType!=null&&!"".equals(feeType)){
            param.put("feeType",feeType);
        }
		return param;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}

	public String getServiceId() {
        if(serviceId!=null&&serviceId!=""){
            return serviceId;
        }
		return gwServiceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
    
    

}