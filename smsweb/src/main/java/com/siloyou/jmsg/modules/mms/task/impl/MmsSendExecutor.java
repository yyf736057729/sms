package com.siloyou.jmsg.modules.mms.task.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.WhitelistUtils;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTaskDetail;
import com.siloyou.jmsg.modules.mms.service.MmsSendService;
import com.siloyou.jmsg.modules.mms.task.MmsSendTask;

public class MmsSendExecutor implements Runnable {
	
	public static Logger logger = LoggerFactory.getLogger(MmsSendExecutor.class);
	
	private static JmsgMmsTaskDao taskDao = SpringContextHolder.getBean(JmsgMmsTaskDao.class);
	
	private static JmsgMmsTaskDetailDao taskDetailDao = SpringContextHolder.getBean(JmsgMmsTaskDetailDao.class);
	
	private final String taskId;
	
	private int sendMmsSize = 0;//发送彩信数量
	
	private static final int pageSize = 1000;
	
	private static long speed = 20;//速率 默认1秒50条 单位毫秒
	
	public MmsSendExecutor(String taskId){
		this.taskId = taskId;
	}
	
	//设置速率
	public static void setSpeed(long speed){
		MmsSendExecutor.speed = speed;
	}
	
	//获取速率
	public static long getSpeed(){
		return speed;
	}
	
	//任务进度
	public int taskProgress(){
		return sendMmsSize;
	}
	
	@Override
	public void run() {
		
		long startTime = System.currentTimeMillis();
		System.out.println("-----start:"+ startTime);

		String bizid = null;//任务明细ID
		while(true){
			JmsgMmsTask task = taskDao.get(taskId);
			String status = task.getStatus();//任务状态
			if("5".equals(status)){//任务暂停
				//修改任务明细发送状态
				Map<String,String> p2Map = Maps.newHashMap();
				p2Map.put("id", bizid);
				p2Map.put("taskId", taskId);
				taskDetailDao.updateSendStatusP2(p2Map);
				MmsSendTask.mmsSendExecMap.remove(taskId);// 剔除
				break;
			}
			
			String userId = task.getCreateUserId();
			String mmsTitle = task.getMmsTitle();
			String mmsUrl = task.getMmsUrl();
			int mmsSize = task.getMmsSize();
			
			//更新状态为运行中
			Map<String,String> sMap = Maps.newHashMap();
			sMap.put("taskId", taskId);
			sMap.put("status","2");//运行中
			sMap.put("updateBy","1");//修改人
				
			if("1".equals(status) || "8".equals(status)){//发送状态为待发送、继续发送
				taskDao.updateStatus(sMap);
			}
			List<JmsgMmsTaskDetail> list = queryTaskDetail(status,bizid);//获取任务明细
			
			if(list ==null || list.size() == 0){
				sMap.put("status","3");//发送状态为发送完成
				taskDao.updateStatus(sMap);
				MmsSendTask.mmsSendExecMap.remove(taskId);// 任务执行完成
				break;
			}
			
			if(list != null && list.size() >0){
				for (JmsgMmsTaskDetail bo : list) {
					sendMmsSize++;//任务执行进度
					bizid = bo.getId();
					String phone = bo.getPhone();
					long start = System.currentTimeMillis();
					sendHandler(taskId, phone, bizid, userId, mmsTitle, mmsUrl, mmsSize);
					long onceSpeed = System.currentTimeMillis() - start;//单次速率
					long sleepTime = speed - onceSpeed;//休眠时长
//					System.out.println("需要休眠时间:"+sleepTime+"单次执行耗时:"+onceSpeed+"速率:"+speed);
					if(sleepTime >0){
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("任务结束时间endTime:"+(endTime - startTime)/1000);
	}

	//获取任务明细
	private List<JmsgMmsTaskDetail> queryTaskDetail(String status,String bizid) {
		Map<String,String> pMap = Maps.newHashMap();
		pMap.put("taskId", taskId);
		String sendStatus = "P0";
		if("8".equals(status)){//继续发送
			sendStatus="P2";//继续发送
		}
		pMap.put("sendStatus",sendStatus);
		pMap.put("pageSize", String.valueOf(pageSize));
		pMap.put("id", StringUtils.isBlank(bizid)?"0":bizid);
		List<JmsgMmsTaskDetail> list = taskDetailDao.findPendingSendMmsByTaskId(pMap);//获取任务明细列表
		return list;
	}
	
	
	private void sendHandler(String taskid, String phone,String bizid,String userId,String mmsTitle,String mmsUrl,int mmsSize){
		if(BlacklistUtils.isExist(phone,0)){//判断是否黑名单
			sendMmsStatus(bizid, "F2", "");//黑名单
		}else{
			try{
				if(WhitelistUtils.isExist(phone)){//判断是否是白名单号码
					sendMms(taskid, bizid, userId, phone, mmsTitle, mmsUrl,mmsSize,false);
				}else{
					String dayKey = CacheKeys.getCacheDaySendKey(phone);
					String monthKey = CacheKeys.getCacheMonthSendKey(phone);
					long dayKeyValue = JedisClusterUtils.incr(dayKey,DateUtils.getEndDayTime());//一天内发送次数+1
					long monthKeyValue = JedisClusterUtils.incr(monthKey,DateUtils.getEndMonthTime());//一月内发送次数+1
					if(dayKeyValue > 2){//判断号码是否一天内发送2次以上  F3
						sendMmsStatus(bizid, "F3", "");
					}else{
						if(monthKeyValue > 10){//判断号码是否一个月内发送10次以上  F4
							sendMmsStatus(bizid, "F4", "");
						}else{
							sendMms(taskid, bizid, userId, phone, mmsTitle, mmsUrl,mmsSize,false);
						}
					}
				}
			}catch(Exception e){
				logger.error("{}", e);
			}
		}
	}
	
	/**
	 * 
	 * @param id 任务明细ID
	 * @param userId 用户ID
	 * @param phone 手机号码
	 * @param mmsTitle 彩信标题
	 * @param mmsUrl 彩信URL
	 * @param execFlag 执行标识 true:执行(重新发送) false:不执行
	 */
	public static void sendMms(String taskid, String id,String userId,String phone,String mmsTitle,String mmsUrl,int mmsSize,boolean execFlag){
		//发送号码
		String msgid = MmsSendService.sendMms(id, "{\"smstype\":\"mms\",\"userid\":\""+userId+"\",\"taskid\":\""+ taskid +"\""
				+ ", \"phone\":\""+phone+"\", \"from\":\"iMessage\", \"serviceid\":\"106905085241\", \"srcid\":\"106905085241\""
						+ ", \"message\":\""+mmsTitle+"\", \"url\":\""+mmsUrl+"\", \"size\":\""+mmsSize+"\", \"msgsrc\":\"911337\"}");
		String sendStatus = "T0";
		if(StringUtils.isBlank(msgid)){
			sendStatus = "P1";//重新发送
			if(execFlag){
				String againSendKey = CacheKeys.getCacheAgainSendKey(phone);
				long agaginCount = JedisClusterUtils.incr(againSendKey);
				if(agaginCount >3){//重新发送3次以上 F5
					sendStatus = "F5";
					String daySendKey = CacheKeys.getCacheDaySendKey(phone);
					int dayValue = JedisClusterUtils.getInt(daySendKey);
					
					if(dayValue >= 2){
						JedisClusterUtils.set(daySendKey, "1",0);
						
						System.out.println("1daySend:"+JedisClusterUtils.get(daySendKey));
					}else{
						JedisClusterUtils.set(daySendKey, String.valueOf((0)),0);
					}
					JedisClusterUtils.decr(CacheKeys.getCacheMonthSendKey(phone));
					
					//销毁key
					JedisClusterUtils.del(againSendKey);
				}
			}
		}
		sendMmsStatus(id, sendStatus, msgid);
	}
	
	
	//发送彩信状态
	public static void sendMmsStatus(String id,String sendStatus,String msgid){
		String result = MmsSendService.sendMmsStatus(id, "{\"id\":\""+id+"\", \"sendStatus\":\""+sendStatus+"\", \"msgid\":\""+msgid+"\"}");
		if(StringUtils.isBlank(result)){
			Map<String,String> map = Maps.newHashMap();
			map.put("id", id);
			map.put("sendStatus", sendStatus);
			map.put("msgid", msgid);
			taskDetailDao.updateSendStatus(map);
		}
	}

}
