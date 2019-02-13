package com.siloyou.jmsg.modules.sms.task.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.mapper.JsonMapper;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgReportStatusTask;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.task.IPublicCustomTask;

public class JmsgModifyReportStatusTask implements IPublicCustomTask{
	
	private static Logger logger = LoggerFactory.getLogger(JmsgModifyReportStatusTask.class);
	
	private JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);
	
	private MQUtils mQUtils = SpringContextHolder.getBean(MQUtils.class);
	
	private SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
	
	@Override
	public String taskRun(String param) {
		String msg = "";
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try{
			JmsgReportStatusTask paramEntity = (JmsgReportStatusTask) JsonMapper.fromJsonString(param, JmsgReportStatusTask.class);
			String tableIndex = TableNameUtil.getTableIndex(paramEntity.getDayQ());
			String tableName = "jmsg_sms_send_"+tableIndex;
			paramEntity.setTableName(tableName);
			String status = "1";
			if("1".equals(paramEntity.getNullType()) && "1".equals(paramEntity.getErrorType())){
				status = "0";
			}else if("1".equals(paramEntity.getErrorType())){
				status = "2";
			}
			paramEntity.setStatus(status);
			Integer countObj = jmsgSmsSendDao.findReportStatusCount(paramEntity);
			int relCount = (countObj == null ? 0 : countObj);
			if(relCount >= paramEntity.getCount()){
				int payCount = updateReportStatus(sqlSession, paramEntity, tableName);//修改状态报告
				sqlSession.commit();
				msg = "执行修改状态报告任务成功,修改状态报告数量"+payCount+"条";
			}else{
				msg = "修改状态报告数量0条,修改数量("+paramEntity.getCount()+")和实际查询数量("+relCount+")不符。";
			}
		}catch(Exception e){
			 logger.error("{}", e);
			 msg = "执行修改状态报告任务失败，系统错误";
		}finally{
			if (sqlSession != null){
                sqlSession.close();
            }
		}
		
		
		return msg;
	}

	private int updateReportStatus(SqlSession sqlSession, JmsgReportStatusTask paramEntity, String tableName)throws Exception {
		boolean pushFlag = pushFlag(paramEntity.getUserId());//推送标识 true:推送
		int count = paramEntity.getCount();//修改总量
		int submitCount = 0;
		int pageSize = 200;
		int payCount = 0;
		String id = "0";
		paramEntity.setPageSize(pageSize);
		while(true){
			paramEntity.setId(id);
			List<JmsgSmsSend> list = jmsgSmsSendDao.findReportStatusCountList(paramEntity);
			if(list !=null && list.size() > 0){
				for (JmsgSmsSend jmsgSmsSend : list) {
					id = jmsgSmsSend.getId();
					submitCount++;
					payCount += jmsgSmsSend.getPayCount();
					if(count < payCount){
						return payCount-jmsgSmsSend.getPayCount();
					}
					jmsgSmsSend.setTableName(tableName);
					sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.updateReportStatusSuccess", jmsgSmsSend);
					if(submitCount%200 ==0){
						sqlSession.commit();
					}
					if(pushFlag){
						sendSmsMT(jmsgSmsSend);
					}
				}
			}else{
				return payCount;
			}
			if(list == null || list.size() < pageSize){
				return payCount;
			}
		}
	}
	
	//推送标识
	private boolean pushFlag(String userId){
		User user = UserUtils.get(userId);
		if(user == null)return false;
		if(StringUtils.isBlank(user.getCallbackUrl())){
			return false;
		}
		return true;
		
	}
	
	private void sendSmsMT(JmsgSmsSend jmsgSmsSend) throws Exception{
		SmsMtMessage smsMtMessage = new SmsMtMessage();
		
		smsMtMessage.setId(jmsgSmsSend.getId());
		smsMtMessage.setTaskid(jmsgSmsSend.getTaskId());
		smsMtMessage.setUserid(jmsgSmsSend.getUser().getId());
		smsMtMessage.setGateWayID(jmsgSmsSend.getChannelCode());
		smsMtMessage.setPayType(jmsgSmsSend.getPayType());
		smsMtMessage.setCstmOrderID(jmsgSmsSend.getCustomerOrderId());
		
		smsMtMessage.setUserReportNotify("9");
		smsMtMessage.setUserReportGateWayID(jmsgSmsSend.getSubmitMode());
		smsMtMessage.setMsgContent(jmsgSmsSend.getSmsContent());
		smsMtMessage.setPhone(jmsgSmsSend.getPhone());
		smsMtMessage.setSpNumber(jmsgSmsSend.getSpNumber());
		smsMtMessage.setSmsType(jmsgSmsSend.getSmsType());
		smsMtMessage.setSendStatus(jmsgSmsSend.getSendStatus());
		smsMtMessage.setPhoneType(jmsgSmsSend.getPhoneType());
		smsMtMessage.setCityCode(jmsgSmsSend.getAreaCode());
		
		SmsRtMessage smsRtMessage = new SmsRtMessage();
        smsRtMessage.setMsgid(jmsgSmsSend.getMsgid());
        smsRtMessage.setSrcTermID(smsMtMessage.getPhone());//业务处理失败的情况下没有发送号码
        smsRtMessage.setDestTermID(smsMtMessage.getPhone());
        smsRtMessage.setSubmitTime(DateUtils.getDate("yyMMddHHmm"));
        smsRtMessage.setDoneTime(DateUtils.getDate("yyMMddHHmm"));
        smsRtMessage.setStat("DELIVRD");
        smsRtMessage.setSmsMt(smsMtMessage);
        smsRtMessage.setSmscSequence("0");
        mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsMtMessage.getUserReportGateWayID(), smsMtMessage.getId(),FstObjectSerializeUtil.write(smsRtMessage));
	}

}
