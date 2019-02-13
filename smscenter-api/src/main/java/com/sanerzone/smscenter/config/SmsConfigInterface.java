package com.sanerzone.smscenter.config;

/**
 * @param type 1:新增 2:删除
 * @param object
 * @author Administrator
 *
 */
public interface SmsConfigInterface {
	public boolean configGroup(int type, Object object,String groupId);
	public boolean configGatewayGroup(int type,Object object,String key,String gatewayId);
	public boolean configGateway(int type,String gatewayId,Object object);
	public boolean configKeyWords(int type,String value);
	public boolean configSign(int type,Object objcet);
	public boolean testGateway(String userId,String phone,String smsContent);
	public boolean configRule(int type, Object object);
	public boolean configRuleGroup(int type, String groupId, Object object);
	public boolean configGatewayQueue(int type,String gatewayQueueId,Object object);
	public boolean configUserTmpl(int type,String gatewayQueueId,Object object);
	public boolean configSmsGatewayTmpl(int type,String templateId,Object object);
}
