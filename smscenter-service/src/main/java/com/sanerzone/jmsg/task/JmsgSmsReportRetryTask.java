package com.sanerzone.jmsg.task;

import java.text.SimpleDateFormat;
import java.util.*;

import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.jmsg.dao.JmsgSmsReportRetryDao;
import com.sanerzone.jmsg.dao.JmsgSmsSubmitDao;
import com.sanerzone.jmsg.entity.JmsgSmsReportRetry;
import com.sanerzone.jmsg.entity.JmsgSmsSubmit;
import org.springframework.beans.factory.annotation.Autowired;

public class JmsgSmsReportRetryTask {
	Logger logger = LoggerFactory.getLogger(JmsgSmsReportSyncTask.class);

	private JmsgSmsReportRetryDao jmsgSmsReportRetryDao;

	private JmsgSmsSubmitDao jmsgSmsSubmitDao;

	private SqlSessionFactory sqlSessionFactory;
	@Autowired
	private MQUtils mQUtils;
	public void exec()
	{
		logger.info("重试reprot任务执行中...");
		// 获取retry表近3天的数据
		Map<String,Object> map = Maps.newHashMap();
		List<JmsgSmsReportRetry> list = jmsgSmsReportRetryDao.findRTRetryList(map);
		if (null != list)
		{
			SqlSession sqlSession = sqlSessionFactory.openSession();

			Calendar current = Calendar.getInstance(); // 今天

			Calendar yesterday = Calendar.getInstance();    //昨天
	        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
	        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
	        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
	        yesterday.set( Calendar.HOUR_OF_DAY, 0);
	        yesterday.set( Calendar.MINUTE, 0);
	        yesterday.set(Calendar.SECOND, 0);

	        Calendar byesterday = Calendar.getInstance();    //前天
	        byesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
	        byesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
	        byesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-2);
	        byesterday.set( Calendar.HOUR_OF_DAY, 0);
	        byesterday.set( Calendar.MINUTE, 0);
	        byesterday.set(Calendar.SECOND, 0);

	        JmsgSmsSubmit jmsgSmsSubmit = null;

			for (JmsgSmsReportRetry parm : list)
			{
				// 根据msgid查询submit，获取bizid
				if (StringUtils.isNotBlank(parm.getMsgid()))
				{
					map = new HashMap<String, Object>();
					map.put("tableName", "jmsg_sms_submit_" + current.get(Calendar.DAY_OF_MONTH));
					map.put("tableName1", "jmsg_sms_submit_" + yesterday.get(Calendar.DAY_OF_MONTH));
					map.put("tableName2", "jmsg_sms_submit_" + byesterday.get(Calendar.DAY_OF_MONTH));
					map.put("msgid", parm.getMsgid());
					//map.put("gatewayid", parm.getGatewayId());
					map.put("phone", parm.getDestTermId());
					List<JmsgSmsSubmit> submitList = jmsgSmsSubmitDao.findSubmitByBizId(map);

					// 根据bizid修改send表的reportstatus
					if (null != submitList && submitList.size() > 0)
					{
						jmsgSmsSubmit = submitList.get(0);

						map = new HashMap<String, Object>();

						map.put("id", jmsgSmsSubmit.getBizid());
						if("DELIVRD".equals(parm.getStat())){//成功
							map.put("reportStatus", "T100");
						}else{
							map.put("reportStatus", "F2" + parm.getStat());//失败
						}
//						mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(),
//								smsRtMessage.getSmsMt().getUserReportGateWayID(), smsRtMessage.getSmsMt().getId(), FstObjectSerializeUtil.write(smsRtMessage));
						map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(jmsgSmsSubmit.getMsgid()));
						int num = sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);
						if (num > 0)
	                    {
//							SmsRtMessage smsRtMessage = new SmsRtMessage();
//							smsRtMessage.setMsgid(jmsgSmsSubmit.getMsgid());
//							smsRtMessage.setDestTermID(parm.getDestTermId());
//							smsRtMessage.setDoneTime(stringToDate(jmsgSmsSubmit.getCreatetime()));
//							smsRtMessage.setStat(jmsgSmsSubmit.getResult());
//							SmsMtMessage mtMessage = new SmsMtMessage();
////							mtMessage.setId();
//							mtMessage.setTaskid(jmsgSmsSubmit.getMsgid());
//							mtMessage.setUserid(jmsgSmsSubmit.get);
//							mtMessage.setGateWayID();
//							mtMessage.setPhone();
//							smsRtMessage.setSmsMt(mtMessage);
//
//							if(org.apache.commons.lang3.StringUtils.isBlank(smsRtMessage.getSrcTermID())) {
//								smsRtMessage.setSrcTermID(smsSrMessage.getMessage().getPhone());
//							}
//
//							try {
//								mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(),
//										smsRtMessage.getSmsMt().getUserReportGateWayID(),
//										smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
//							} catch (Exception e) {
//								e.printStackTrace();
//							}

							jmsgSmsReportRetryDao.delete(parm);
	                    }
					}
				}
			}

			if (sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}

	public static String stringToDate(Date date){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sd.format(date);
	}

	public JmsgSmsReportRetryDao getJmsgSmsReportRetryDao() {
		return jmsgSmsReportRetryDao;
	}

	public void setJmsgSmsReportRetryDao(JmsgSmsReportRetryDao jmsgSmsReportRetryDao) {
		this.jmsgSmsReportRetryDao = jmsgSmsReportRetryDao;
	}

	public JmsgSmsSubmitDao getJmsgSmsSubmitDao() {
		return jmsgSmsSubmitDao;
	}

	public void setJmsgSmsSubmitDao(JmsgSmsSubmitDao jmsgSmsSubmitDao) {
		this.jmsgSmsSubmitDao = jmsgSmsSubmitDao;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
}
