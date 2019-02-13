//package com.sanerzone.jmsg.impl;
//
//import java.util.Date;
//import java.util.List;
//
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.marre.sms.SmsAlphabet;
//import org.marre.sms.SmsMsgClass;
//import org.marre.sms.SmsTextMessage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.alibaba.rocketmq.client.consumer.MQPullConsumer;
//import com.alibaba.rocketmq.client.consumer.PullResult;
//import com.alibaba.rocketmq.client.consumer.PullTaskCallback;
//import com.alibaba.rocketmq.client.consumer.PullTaskContext;
//import com.alibaba.rocketmq.common.message.MessageExt;
//import com.alibaba.rocketmq.common.message.MessageQueue;
//import com.sanerzone.common.modules.TableNameUtil;
//import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
//import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
//import com.sanerzone.common.support.utils.StringUtils;
//import com.sanerzone.jmsg.entity.JmsgSmsSend;
//import com.siloyou.jmsg.common.message.SmsMtMessage;
//
//@Service
//public class SmsMtPullTaskCallback implements PullTaskCallback {
//
//	public static Logger logger = LoggerFactory.getLogger(SmsMtPullTaskCallback.class);
//	
//	@Autowired
//	private SqlSessionFactory sqlSessionFactory;
//	
//	@Override
//	public void doPullTask(MessageQueue mq, PullTaskContext context) {
//		MQPullConsumer consumer = context.getPullConsumer();
//		try {
//
//			long offset = consumer.fetchConsumeOffset(mq, false);
//			if (offset < 0)
//				offset = 0;
//
//			PullResult pullResult = consumer.pull(mq, "normal||high", offset, 32);
//			switch (pullResult.getPullStatus()) {
//			case FOUND:
//				consumeMessage (pullResult.getMsgFoundList());
//				break;
//			case NO_MATCHED_MSG:
//				break;
//			case NO_NEW_MSG:
//			case OFFSET_ILLEGAL:
//				break;
//			default:
//				break;
//			}
//			consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
//
//			context.setPullNextDelayTimeMillis(100);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//    public void consumeMessage(List<MessageExt> msgs)
//    {
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        SmsMtMessage smsMtMessage = null;
//        int index =0;
//        JmsgSmsSend jmsgSmsSend;
//        int payCount;
//        try
//        {
//            for (MessageExt msg : msgs)
//            {
//                logger.info("mt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}",
//                    msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
//                
//                smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(msg.getBody());
//                
//                if (null != smsMtMessage)
//                {
//                	if(StringUtils.equals(smsMtMessage.getUserReportGateWayID(), "HTTP")) {
//                		continue;
//                	}
//                    payCount = findPayCount(smsMtMessage.getMsgContent());//扣费条数
//                    jmsgSmsSend = new JmsgSmsSend();
//                    jmsgSmsSend.setId(smsMtMessage.getId());
//                    jmsgSmsSend.setDataId("99");
//                    jmsgSmsSend.setTaskId(smsMtMessage.getTaskid());//任务我ID
//                    jmsgSmsSend.setPhone(smsMtMessage.getPhone());//手机号码
//                    jmsgSmsSend.setSmsContent(smsMtMessage.getMsgContent());//短信内容
//                    jmsgSmsSend.setSmsType(payCount >1 ?"2":"1");//短信类型
//                    jmsgSmsSend.setPayCount(payCount);//扣费条数
//                    jmsgSmsSend.setUserId(smsMtMessage.getUserid());
//                    jmsgSmsSend.setChannelCode(smsMtMessage.getGateWayID());//通道代码
//                    jmsgSmsSend.setSpNumber(smsMtMessage.getSpNumber());//接入号
//                    jmsgSmsSend.setPhoneType(smsMtMessage.getPhoneType());//运营商
//                    jmsgSmsSend.setAreaCode(smsMtMessage.getCityCode());//省市代码
//                    jmsgSmsSend.setPayType(smsMtMessage.getPayType());//扣费方式
//                    jmsgSmsSend.setPayStatus("1");//扣费状态
//                    jmsgSmsSend.setPushFlag(smsMtMessage.getUserReportNotify());//推送标识
//                    jmsgSmsSend.setCompanyId("11");
//                    jmsgSmsSend.setSendStatus(smsMtMessage.getSendStatus());//发送状态
//                    jmsgSmsSend.setSubmitMode(smsMtMessage.getUserReportGateWayID());//提交方式 WEB,API
//                    jmsgSmsSend.setTopic(JmsgCacheKeys.getSmsSingleTopic());//发送队列
//                    jmsgSmsSend.setReportGatewayId(smsMtMessage.getUserReportGateWayID());
//                    jmsgSmsSend.setMsgid(msg.getMsgId());
//                    jmsgSmsSend.setSendDatetime(new Date());
//                    
//                    jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(smsMtMessage.getId()));
//                    
//                    index++;
//                    sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
//                    if(index % 500 == 0) {
//                        sqlSession.commit();
//                    }
//                }
//                else
//                {
//                    logger.info("mt listener 解析异常: topic:{}, tags:{}, msgid:{}, key:{}",
//                        msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
//                }
//            }
//            sqlSession.commit();
//        }
//        catch (Exception e)
//        {
//            logger.error("{}", e);
//        }
//        finally
//        {
//            if (sqlSession != null)
//            {
//                sqlSession.close();
//            }
//        }
//    }
//    
//    /**
//     * 获取扣费条数
//     * @param smsContent
//     * @return
//     * @see [类、类#方法、类#成员]
//     */
//    public int findPayCount(String smsContent){
//        SmsTextMessage sms = null;
//        if(StringUtils.haswidthChar(smsContent)) {
//            sms = new SmsTextMessage(smsContent,SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
//        } else {
//            sms = new SmsTextMessage(smsContent,SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
//        }
//        return sms.getPdus().length;
//    }
//}
