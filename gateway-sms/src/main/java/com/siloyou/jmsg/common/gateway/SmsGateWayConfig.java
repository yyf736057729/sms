package com.siloyou.jmsg.common.gateway;

import org.apache.commons.lang3.StringUtils;

import com.Application;

public class SmsGateWayConfig {

	public static String getAppTopic(String level) {
		if(StringUtils.equals(level, "LOW")) {
			return "SMSMT" + Application.appCode + "B";
		}
		return "SMSMT" + Application.appCode + "S";
	}
	
	public static String getSendConsumerGroup(String gatewayId, String level) {
//		return String.format("%s_%s_%sGrp", getAppTopic(level), gatewayId, level);
//		return String.format("%s_%sGrp", getAppTopic(level),  level);
        String format = String.format("%s_Grp", getAppTopic(level));
        return format;
	}
	
	public static String getSendConsumerIns(String gatewayId, String level) {
		return String.format("%s_%s_%sIns", getAppTopic(level), gatewayId, level);
	}
	
	public static String getSendConsumerTag(String gatewayId, String level) {
		return String.format("%s_%s", gatewayId, level);
	}
}
