package com.sanerzone.smscenter.config;

import java.util.Date;
import java.util.List;

/**
 * 批量发送
 * @author Administrator
 */
public interface SmsBatchSendInterface {
	public String configSmsBatchSend(String userId,String taskId,String smsContent,int count,String status,Date sendDatetime,List<String> phonList);
}
