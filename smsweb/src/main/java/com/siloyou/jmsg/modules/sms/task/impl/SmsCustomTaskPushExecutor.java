package com.siloyou.jmsg.modules.sms.task.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSendService;
import com.siloyou.jmsg.modules.sms.task.IPublicCustomTask;

/**
 * 自定义任务-推送
 * @author zhangjie
 *
 */
public class SmsCustomTaskPushExecutor implements IPublicCustomTask
{
	private JmsgSmsSendService jmsgSmsSendService = SpringContextHolder.getBean(JmsgSmsSendService.class);
	
	private final static int pageSize=1000;
	
	@Override
	public String taskRun(String paramJson) {
		StringBuilder result = new StringBuilder();
		int count = 0;
		int successNo = 0;
		int failNo = 0;
		int index=0;
		int pageNo=0;
		
		Map<String, Object> param = new HashMap<String, Object>();
		param = JSON.parseObject(paramJson);
		/*JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
		jmsgSmsSend.setTableName((String) param.get("tableName"));
		jmsgSmsSend.setPhone((String) param.get("phone"));
		jmsgSmsSend.setTaskId((String) param.get("taskId"));
		jmsgSmsSend.setId((String) param.get("id"));
		jmsgSmsSend.setUser(new User((String) param.get("userId")));
		jmsgSmsSend.setPhoneType((String) param.get("phoneType"));
		jmsgSmsSend.setSendStatus((String) param.get("sendStatus"));
		jmsgSmsSend.setReportStatus((String) param.get("reportStatus"));
		jmsgSmsSend.setChannelCode((String) param.get("channelCode"));
		jmsgSmsSend.setCreateDatetimeQ((Date) param.get("createDatetimeQ"));
		jmsgSmsSend.setCreateDatetimeZ((Date) param.get("createDatetimeZ"));*/
		
		param.put("pageSize",pageSize);
		while(true){
			pageNo = index*pageSize;
			param.put("pageNo",pageNo);
			List<JmsgSmsSend> list = jmsgSmsSendService.findPushList(param);
			if(list != null && list.size() >0){
				for (JmsgSmsSend jmsgSmsSend : list) {
					
					if(StringUtils.startsWith(jmsgSmsSend.getSendStatus(),"P")) {
						continue;
					}
					if(StringUtils.startsWith(jmsgSmsSend.getSendStatus(),"T") && StringUtils.startsWith(jmsgSmsSend.getReportStatus(),"P")) {//状态null 不推送
						continue;
					}
					if (jmsgSmsSendService.push(jmsgSmsSend))
		        	{
		        		successNo ++;
		        	}
		        	else
		        	{
		        		failNo ++;
		        	}
					count ++;
				}
			}else{
				break;
			}
			
			if(list.size() < pageSize)break;
			index++;
		}
		
		result.append("推送 ：" + count + " 条，成功 ：" + successNo + " 条，失败：" + failNo + " 条。");
		
		return result.toString();
	}
}
