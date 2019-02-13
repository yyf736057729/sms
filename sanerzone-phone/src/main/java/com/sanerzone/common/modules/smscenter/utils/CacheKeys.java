package com.sanerzone.common.modules.smscenter.utils;

/**
 * 缓存主键工具类
 * 
 * @author zhukc
 *
 */
public class CacheKeys {
	
	public static final String GATEWAY_CACHE = "gatewayCache";
	
	//彩信一天内发送次数key
	public static String getCacheDaySendKey(String param){
		return "daySend_"+param;
	}
	
	//彩信一个月内发送次数key
	public static String getCacheMonthSendKey(String param){
		return "monthSend_"+param;
	}
	
	//重新发送次数key
	public static String getCacheAgainSendKey(String param){
		return "againSend_"+param;
	}
	
	//发送彩信速率
	public static String getCacheMmsSpeed = "globalMmsSpeed";
	
	//短信一天内发送次数key
	public static String getCacheDaySmsSendKey(String param){
		return "sd_"+param;
	}
	
	//短信一个月内发送次数key
	public static String getCacheMonthSmsSendKey(String param){
		return "sm_"+param;
	}
	
	//短信重新发送次数key
	public static String getCacheAgainSmsSendKey(String param){
		return "sa_"+param;
	}
	
	//网关key 分组ID_运营商_省份
	public static String getCacheGatewayGroupKey(String groupId,String phoneType,String provinceId){
		return groupId+"_"+phoneType+"_"+provinceId;
	}
	
	//通道信息key
	public static String getCacheGatewayInfoKey(String gatewayId){
		return "gatewayInfo_"+gatewayId;
	}
	
	
	//分组ID
	public static String getCacheGroupKey(String groupId){
		return "group_"+groupId;
	}
	
	//用户签名 key
	public static String getCacheUserSignKey(String userId){
		return "user_sign_"+userId;
	}
	
	//获取推送topic
	public static String getPushTopic(){
		return "SMSURV2";
	}
	
	//获取上行topic
	public static String getMOTopic(){
		return "SMSMOV2";
	}
	
	//获取状态topic
	public static String getReportTopic(){
		return "SMSRTV2";
	}
	
	//获取短信发送状态topic
	public static String getSmsSendStatus(){
		return "SMSSTATUSV2";
	}
	
	//获取短信提交网关topic
	public static String getSmsSubmit(){
		return "SMSSRV2";
	}
	
	//短信群发topic
	public static String getSmsBatchTopic(){
		return "SMS_BATCH_TOPIC";
	}
	
	//短信单发topic
	public static String getSmsSingleTopic(){
		return "SMSMTV2";
	}
	
	//UMT用户下行 topic
	public static String getSmsUMTTopic(){
		return "SMSUMTV2";
	}
	
	public static String getSmsREQTopic(){
		return "SMSREQ";
	}
		
	/**
     * 获取用户通道签名key 
     * 用户ID_通道ID_签名
     * @param userId
     * @param gatewayId
     * @param sign
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getGatewaySignKey(String userId, String gatewayId, String sign)
    {
        return userId + "_" + gatewayId + "_" + sign;
    }

}
