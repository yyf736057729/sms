//package com.sanerzone.jmsg.impl;
//
//import java.util.List;
//import java.util.Map;
//
//import com.google.common.collect.Maps;
//import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
//import com.sanerzone.common.support.utils.DateUtils;
//import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
//import com.sanerzone.common.support.utils.SpringContextHolder;
//import com.sanerzone.jmsg.dao.JmsgSmsSendDao;
//import com.sanerzone.jmsg.entity.JmsgSmsSend;
//import com.sanerzone.smscenter.utils.MQUtils;
//import com.siloyou.jmsg.common.message.SmsMtMessage;
//import com.siloyou.jmsg.common.message.SmsRtMessage;
//
//public class SmsPushExecutor {
//	
//	private final String taskId;//任务ID
//	
//	private JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);
//	private MQUtils mQUtils = SpringContextHolder.getBean(MQUtils.class);
//	
//	private final static int pageSize=1000;
//	
//	public SmsPushExecutor(String taskId){
//		this.taskId = taskId;
//	}
//	
//	public void run(){
//		
//		try{
//			//根据任务ID 获取需要推送的列表
//			int index=0;
//			int pageNo=0;
//			Map<String,Object> param = Maps.newHashMap();
//			param.put("taskId", taskId);
//			param.put("pageSize",pageSize);
//			while(true){
//				pageNo = index*pageSize;
//				param.put("pageNo",pageNo);
//				List<JmsgSmsSend> list = jmsgSmsSendDao.findPushListByTaskId(param);
//				if(list != null && list.size() >0){
//					SmsRtMessage rtMessage = null;
//					SmsMtMessage mtMsg = null;
//					for (JmsgSmsSend jmsgSmsSend : list) {
//						rtMessage = new SmsRtMessage();
//						mtMsg = new SmsMtMessage();
//						rtMessage.setDestTermID(jmsgSmsSend.getPhone());
//						rtMessage.setSrcTermID(jmsgSmsSend.getPhone());
//						rtMessage.setStat(jmsgSmsSend.getSendStatus());
//						rtMessage.setSmscSequence("0");
//						rtMessage.setDoneTime(DateUtils.formatDate(jmsgSmsSend.getSendDatetime(), "yyMMddHHmm"));
//						rtMessage.setSubmitTime(DateUtils.formatDate(jmsgSmsSend.getSendDatetime(), "yyMMddHHmm"));
//						String id = jmsgSmsSend.getId();
//						mtMsg.setId(id);
//						mtMsg.setUserid(jmsgSmsSend.getUserId());
//						mtMsg.setTaskid(taskId);
//						rtMessage.setSmsMt(mtMsg);
//						//mQUtils.pushSmsMQ(id, jmsgSmsSend.getReportGatewayId(),FstObjectSerializeUtil.write(rtMessage));
//						mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), mtMsg.getUserReportGateWayID(), mtMsg.getId(),FstObjectSerializeUtil.write(rtMessage));
//					}
//				}else{
//					break;
//				}
//				
//				if(list.size() < pageSize)break;
//				index++;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//}
