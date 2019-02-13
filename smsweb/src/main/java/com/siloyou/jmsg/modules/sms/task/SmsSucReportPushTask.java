package com.siloyou.jmsg.modules.sms.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.support.utils.DateUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.modules.sms.entity.JmsgCustomTask;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserGateway;
import com.siloyou.jmsg.modules.sms.service.JmsgCustomTaskService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSendService;
import com.siloyou.jmsg.modules.sms.service.JmsgUserGatewayService;

/**
 * 状态成功(report_status=T100)
 * 推送成功(push_flag=1),
 * 推送表(jmsg_sms_push)中记录少于计费条数(jmsg_sms_send:pay_count)
 * 目前只推送CMPP用户
 */
@Service
@Lazy(false)
public class SmsSucReportPushTask {
	
	@Autowired
	private JmsgUserGatewayService jmsgUserGatewayService;
	
	@Autowired
	private JmsgSmsSendService jmsgSmsSendService;
	
	@Autowired
	private JmsgCustomTaskService jmsgCustomTaskService;
	
	private static int pageSize = 1000;
	
	RateLimiter rate = RateLimiter.create(200);
	
	private static Logger logger = LoggerFactory.getLogger(SmsSucReportPushTask.class);
	
	//每天 2:15:30执行任务执行推送任务
	@Scheduled(cron = "30 15 2 * * ?")
	public void run(){
		
		//获取用户
		List<JmsgUserGateway> cmppUser = jmsgUserGatewayService.findList(new JmsgUserGateway());
		if(cmppUser != null && cmppUser.size() > 0){
			Date day = DateUtils.getDay(-2);
			
			String tIndex = TableNameUtil.getTableIndex(day);
			
			String tableName = "jmsg_sms_send_"+tIndex;
			String pushTable = "jmsg_sms_push_"+tIndex;
			Map<String,Object> map = Maps.newHashMap();
			map.put("tableName", tableName);
			map.put("pushTable", pushTable);
			
			
			for (JmsgUserGateway jmsgUserGateway : cmppUser) {
				String userId = jmsgUserGateway.getUserid();//用户ID
				String userName = jmsgUserGateway.getUser() != null ? jmsgUserGateway.getUser().getName() : "";
				pushReport(map, userId, day, userName);
			}
		}
	}
	
	private void pushReport(Map<String,Object> map, String userId, Date day, String userName){
		
		String dayString = DateUtils.formatDate(day, "yyyy-MM-dd");
		
		logger.info("【闲时补推】userid:{}, day:{},任务开始", userId, dayString);
		
		map.put("userId", userId);
		map.put("pageSize", pageSize);
		JmsgCustomTask jmsgCustomTask = insertCustomTask(map, dayString, userName, userId);
		
		int sucCount = 0;
		int failCount = 0;
		
		int pageNo = 0;
		int index = 0;
		while (true) {
			pageNo = index*pageSize;
			map.put("pageNo", pageNo);
			List<JmsgSmsSend> list = jmsgSmsSendService.findSucReportPushList(map);
			index++;
			if(list != null && list.size() > 0){
				for (JmsgSmsSend jmsgSmsSend : list) {
					if(jmsgSmsSend.getPayCount() > jmsgSmsSend.getPushCount()){
						rate.acquire();
						if(jmsgSmsSendService.push(jmsgSmsSend)){//推送
							sucCount++;
						}else{
							failCount++;
						}
					}
				}
			}else{
				break;
			}
			
			if(list.size() < pageSize){
				break;
			}
			
		}
		
		jmsgCustomTask.setStatus("3");//执行完成
		jmsgCustomTask.setExecuteResult("推送："+(sucCount+failCount)+"，成功："+sucCount+"，失败："+failCount);
		jmsgCustomTaskService.updateParam(jmsgCustomTask);
		
		logger.info("【闲时补推】userid:{}, day:{},任务结束", userId, dayString);

	}
	
	//创建任务
	private JmsgCustomTask insertCustomTask(Map<String,Object> map, String day, String userName, String userId){
		JmsgCustomTask customTask = new JmsgCustomTask();
		customTask.setCreateBy(new User("1"));
		customTask.setTaskName(day+"_"+userName+"【"+userId+"】_闲时补推");
		customTask.setType("3");
		customTask.setParamJson(JSON.toJSONString(map));
		customTask.setExecuteClass("com.siloyou.jmsg.modules.sms.task.SmsSucReportPushTask");
		customTask.setStatus("2");//执行中
		customTask.setVersion("1");
		jmsgCustomTaskService.save(customTask);
		return customTask;
	}
	
}
