package com.siloyou.jmsg.modules.sms.task.impl;

import java.io.Serializable;
//import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.support.utils.DateUtils;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
//import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.MQUtils;
//import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.WhitelistUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.service.SmsSendService;
import com.siloyou.jmsg.modules.sms.task.SmsSendTask;

public class SmsTaskSendExecutor implements Runnable {

	public static Logger logger = LoggerFactory.getLogger(SmsTaskSendExecutor.class);
	private static JmsgSmsTaskDao taskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
	private static JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);
	private MQUtils mQUtils = SpringContextHolder.getBean(MQUtils.class);
	SmsSendService smsSendService = SpringContextHolder.getBean(SmsSendService.class);
	
	private final String taskId;

	private int sendSmsSize = 0;// 发送彩信数量

	private static final int pageSize = 1000;

	private static long speed = 10;// 速率 默认1秒50条 单位毫秒

	public SmsTaskSendExecutor(String taskId) {
		this.taskId = taskId;
	}

	// 设置速率
	public static void setSpeed(long speed) {
		SmsTaskSendExecutor.speed = speed;
	}

	// 获取速率
	public static long getSpeed() {
		return speed;
	}

	// 任务进度
	public int taskProgress() {
		return sendSmsSize;
	}

	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
		logger.info("任务开始执行,TASKID{},时间{}" , taskId, DateUtils.getDateTime());

		String maxId = "";// 发送ID
		String tableIndex = TableNameUtil.getTableIndex(new Date());
		String tableName="jmsg_sms_send_"+tableIndex;
		try {
			while (true) {
				JmsgSmsTask task = taskDao.get(taskId);// 获取任务信息
				String status = task.getStatus();// 任务状态
				if ("5".equals(status)) {// 任务暂停
					SmsSendTask.smsTaskSendExecMap.remove(taskId);// 剔除线程
					break;
				}

				// 更新状态为运行中
				Map<String, String> sMap = Maps.newHashMap();
				sMap.put("taskId", taskId);
				sMap.put("status", "2");// 运行中

				if ("1".equals(status) || "8".equals(status)) {// 发送状态为待发送、继续发送
					taskDao.updateStatus(sMap);
				}

				Map<String, Object> param = Maps.newHashMap();
				param.put("taskId", taskId);
				param.put("id", maxId);
				param.put("pageSize", pageSize);
				param.put("tableName", tableName);
					
				List<JmsgSmsSend> list = jmsgSmsSendDao.findListByTaskId(param);// 获取待发送任务

				if (list == null || list.size() == 0) {
					sMap.put("status", "3");// 发送状态为发送完成
					taskDao.updateStatus(sMap);
					SmsSendTask.smsTaskSendExecMap.remove(taskId);// 任务执行完成
					break;
				}

				if (list != null && list.size() > 0) {
					for (JmsgSmsSend jmsgSmsSend : list) {
						sendSmsSize++;// 任务执行进度
						long start = System.currentTimeMillis();
						Map<String, String> map = null;
						String sendStatus = "T000";
						if(StringUtils.isNotBlank(jmsgSmsSend.getChannelCode())){
							map = sendHandler(jmsgSmsSend);// 发送短信处理
							sendStatus = map.get("sendStatus");
						}else{
							sendStatus = "F0073";
						}
						
						if(StringUtils.startsWith(sendStatus, "F")){//失败发送SR消息
							sendSmsSR(jmsgSmsSend.getId(),sendStatus);
						}
						logger.info("TASKID:{},提交队列情况:{}",  map);
						
						long onceSpeed = System.currentTimeMillis() - start;// 单次速率
						long sleepTime = speed - onceSpeed;// 休眠时长
						if (sleepTime > 0) {
							try {
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}

				}

				if (list.size() == pageSize) {
					maxId = list.get(pageSize - 1).getId();
				} else {
					sMap.put("status", "3");// 发送状态为发送完成
					taskDao.updateStatus(sMap);
					SmsSendTask.smsTaskSendExecMap.remove(taskId);// 任务执行完成
					break;
				}
			}
			
			//推送 
//			SmsPushExecutor smsPushExecutor = new SmsPushExecutor(taskId,tableIndex);
//			smsPushExecutor.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		logger.info("任务执行完成,TASKID{},结束时间{},耗时：{}}",taskId, DateUtils.getDateTime(), (endTime - startTime)/1000);
	}

	//发送SR，修改发送状态
	private void sendSmsSR(String bizid, String sendStatus) throws Exception {
		SmsSrMessage sr = new SmsSrMessage();
		SmsMtMessage message = new SmsMtMessage();
		message.setId(bizid);
		sr.setResult(sendStatus);
		sr.setMessage(message);
		mQUtils.sendSmsSR(bizid, FstObjectSerializeUtil.write((Serializable)sr));
	}

	private Map<String, String> sendHandler(JmsgSmsSend entity) {

		String id = entity.getId();
		String phone = entity.getPhone();

		Map<String, String> map = Maps.newHashMap();
		String sendStatus = "F000";
		String msgid="";
		map.put("id", id);
		
		User user = UserUtils.get(entity.getUser().getId());
		
		//  1：校验 0： 不校验
		if (user.getSysBlacklistFlag() == 1 && BlacklistUtils.isExistSysBlackList(phone))
		{
		    // 判断是否系统黑名单
	        sendStatus = "F002";
		}
		else if (user.getUserBlacklistFlag() == 1 && BlacklistUtils.isExistUserBlackList(phone))
        {
	        // 判断是否营销黑名单
            sendStatus = "F008";
        }
		else {
			try {
				if (WhitelistUtils.isExist(phone)) {// 判断是否是白名单号码
					Map<String,String> sendMap=smsSendService.sendSms(entity);
					sendStatus = sendMap.get("sendStatus");
					msgid = sendMap.get("msgid");
				} else {
					String dayKey = CacheKeys.getCacheDaySmsSendKey(phone);
					String monthKey = CacheKeys.getCacheMonthSmsSendKey(phone);
					long dayKeyValue = JedisClusterUtils.incr(dayKey, DateUtils.getEndDayTime());// 一天内发送次数+1
					long monthKeyValue = JedisClusterUtils.incr(monthKey, DateUtils.getEndMonthTime());// 一月内发送次数+1
					if (dayKeyValue > 50) {// 判断号码是否一天内发送5次以上 F3
						sendStatus = "F003";
					} else {
						if (monthKeyValue > 300) {// 判断号码是否一个月内发送30次以上 F4
							sendStatus = "F004";
						} else {
							Map<String,String> sendMap=smsSendService.sendSms(entity);
							sendStatus = sendMap.get("sendStatus");
							msgid = sendMap.get("msgid");
						}
					}
				}
			} catch (Exception e) {
				logger.error("{}", e);
			}
		}
		map.put("sendStatus", sendStatus);
		map.put("msgid", msgid);
		return map;
	}

}
