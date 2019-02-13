//package com.sanerzone.jmsg.impl;
//
//import java.util.Date;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.sanerzone.common.modules.TableNameUtil;
//import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
//import com.sanerzone.jmsg.dao.JmsgSmsSendDao;
//import com.sanerzone.jmsg.entity.JmsgSmsSend;
//import com.siloyou.jmsg.common.message.SmsMtMessage;
//
//@Service
//public class SmsSendInsert {
//	
//	@Autowired
//	private JmsgSmsSendDao jmsgSmsSendDao;
//
//	/**
//	 * SmsSend 入库
//	 * @param smsMtMessage
//	 * @param msgId
//	 * @param payCount 扣费条数
//	 * @return
//	 */
//	public int insertSmsSend(SmsMtMessage smsMtMessage, String msgId, int payCount) 
//	{
//		JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
//        jmsgSmsSend.setId(smsMtMessage.getId());
//        jmsgSmsSend.setDataId("99");
//        jmsgSmsSend.setTaskId(smsMtMessage.getTaskid());//任务我ID
//        jmsgSmsSend.setPhone(smsMtMessage.getPhone());//手机号码
//        jmsgSmsSend.setSmsContent(smsMtMessage.getMsgContent());//短信内容
//        jmsgSmsSend.setSmsType(payCount >1 ?"2":"1");//短信类型
//        jmsgSmsSend.setPayCount(payCount);//扣费条数
//        jmsgSmsSend.setUserId(smsMtMessage.getUserid());
//        jmsgSmsSend.setChannelCode(smsMtMessage.getGateWayID());//通道代码
//        jmsgSmsSend.setSpNumber(smsMtMessage.getSpNumber());//接入号
//        jmsgSmsSend.setPhoneType(smsMtMessage.getPhoneType());//运营商
//        jmsgSmsSend.setAreaCode(smsMtMessage.getCityCode());//省市代码
//        jmsgSmsSend.setPayType(smsMtMessage.getPayType());//扣费方式
//        jmsgSmsSend.setPayStatus("1");//扣费状态
//        jmsgSmsSend.setPushFlag(smsMtMessage.getUserReportNotify());//推送标识
//        jmsgSmsSend.setCompanyId("11");
//        jmsgSmsSend.setSendStatus(smsMtMessage.getSendStatus());//发送状态
//        jmsgSmsSend.setSubmitMode(smsMtMessage.getUserReportGateWayID());//提交方式 WEB,API
//        jmsgSmsSend.setTopic(JmsgCacheKeys.getSmsSingleTopic());//发送队列
//        jmsgSmsSend.setReportGatewayId(smsMtMessage.getUserReportGateWayID());
//        jmsgSmsSend.setMsgid(msgId);
//        jmsgSmsSend.setSendDatetime(new Date());
//        
//        jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(smsMtMessage.getId()));
//        
//        return jmsgSmsSendDao.insert(jmsgSmsSend);
//		
//	}
//}
