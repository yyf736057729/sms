package com.siloyou.jmsg.modules.sms.conf;

import java.util.Date;
import java.util.List;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboService;
import com.sanerzone.smscenter.config.SmsBatchSendInterface;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;

@DubboService(interfaceClass = SmsBatchSendInterface.class)
public class SmsBatchSendInterfaceImpl implements SmsBatchSendInterface{

	private JmsgSmsTaskService jmsgSmsTaskService = SpringContextHolder.getBean(JmsgSmsTaskService.class);
	
	@Override
	public String configSmsBatchSend(String userId,String taskId,String smsContent,int count,String status,Date sendDatetime,List<String> phoneList) {
		return jmsgSmsTaskService.httpSmsTask(userId, taskId, smsContent, count, status, sendDatetime, phoneList);
	}
}
