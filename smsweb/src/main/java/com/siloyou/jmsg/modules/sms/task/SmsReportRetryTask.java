package com.siloyou.jmsg.modules.sms.task;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.support.utils.StringUtils;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.MsgId;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReportRetry;

//@Service
//@Lazy(false)
public class SmsReportRetryTask {
	
	/**
	 * 现实逻辑 每次找到获取1000条，每次获取事ID>MAX(ID)
	 */
	private static Logger logger = LoggerFactory.getLogger(SmsReportRetryTask.class);
	
	private static JmsgSmsReportRetryDao jmsgSmsReportRetryDao = SpringContextHolder.getBean(JmsgSmsReportRetryDao.class);
	private static JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);
	private static MQUtils mqUtils = SpringContextHolder.getBean(MQUtils.class);
	
	//执行后，休息10分钟继续执行
	@Scheduled(fixedDelay=600000)
	@Transactional(readOnly = false)
	public void exec(){
		logger.info("重试reprot开始执行任务,时间："+DateUtils.getDateTime());
		
		//ExecutorType.BATCH, false
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession();
		try {
			int maxId = 0;
			Map<String,Object> map = Maps.newHashMap();
			int index=0;
			SmsRtMessage smsRtMessage;
			SmsMtMessage smsMtMessage;
			while(true){
				map.put("id", maxId);
				
				List<JmsgSmsReportRetry> list = jmsgSmsReportRetryDao.findRTRetryList(map);
                if (list != null && list.size() > 0)
                {
                    for (JmsgSmsReportRetry jmsgSmsReportRetry : list)
                    {
                        //来源标示 0：MT为空  1：send未入库
                        if (jmsgSmsReportRetry.getSourceFlag() == 1)
                        {
                            Map<String, String> map1 = Maps.newHashMap();
                            map1.put("id", jmsgSmsReportRetry.getBizid());
                            map1.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(jmsgSmsReportRetry.getBizid()));
                            if ("DELIVRD".equals(jmsgSmsReportRetry.getStat()))
                            {
                                map1.put("reportStatus", "T100");//成功
                            }
                            else
                            {
                                map1.put("reportStatus", "F2" + jmsgSmsReportRetry.getStat());//失败
                            }
                            int num =
                                sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdateReprotStatus",
                                    map1);
                            
                            if (num == 1)
                            {
                                Map<String, String> param = Maps.newHashMap();
                                param.put("id", jmsgSmsReportRetry.getId());
                                sqlSession.delete("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchDelete",
                                    param);
                                index++;
                            }
                        }
                        else
                        {
                            smsRtMessage = new SmsRtMessage();
                            
                            smsRtMessage.setGateWayID(jmsgSmsReportRetry.getGatewayId());
                            smsRtMessage.setMsgid(jmsgSmsReportRetry.getMsgid());
                            smsRtMessage.setStat(jmsgSmsReportRetry.getStat());
                            smsRtMessage.setSubmitTime(String.valueOf(jmsgSmsReportRetry.getSubmitTime()));
                            smsRtMessage.setDoneTime(String.valueOf(jmsgSmsReportRetry.getDoneTime()));
                            smsRtMessage.setSrcTermID(jmsgSmsReportRetry.getSrcTermId());
                            smsRtMessage.setDestTermID(jmsgSmsReportRetry.getDestTermId());
                            smsRtMessage.setSmscSequence(jmsgSmsReportRetry.getSmscSequence());
                            
                            smsMtMessage = jmsgSmsSendDao.findSmsMtMessage(jmsgSmsReportRetry.getBizid());
                            if (smsMtMessage != null)
                            {
                                smsRtMessage.setSmsMt(smsMtMessage);
                                mqUtils.sendSmsRT(smsRtMessage.getGateWayID(),
                                    smsRtMessage.getMsgid(),
                                    FstObjectSerializeUtil.write(smsRtMessage));
                                
                                Map<String, String> param = Maps.newHashMap();
                                param.put("id", jmsgSmsReportRetry.getId());
                                sqlSession.delete("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchDelete",
                                    param);
                                index++;
                            }
                        }
                        
                        /*if (index % 200 == 0)
                        {//200条提交一次
                            sqlSession.commit();
                        }*/
                    }
                    //sqlSession.commit();
                    if (list.size() < 1000)
                    {
                        break;
                    }
                    else
                    {
                        maxId = new Long(list.get(1000 - 1).getId()).intValue();
                    }
                }
                else
                {
                    break;
                }
				
				/*List<SmsRtMessage> list = jmsgSmsReportRetryDao.findReportRetryList(map);
				if(list != null && list.size() >0){
					for (SmsRtMessage smsRtMessage : list) {
						if(smsRtMessage.getSmsMt() != null && StringUtils.isNotBlank(smsRtMessage.getSmsMt().getId())){
							SmsMtMessage smsMtMessage = jmsgSmsSendDao.findSmsMtMessage(smsRtMessage.getSmsMt().getId());
							if(smsMtMessage != null){
								String id = String.valueOf(smsRtMessage.getSmsMt().getContentSize());
								smsRtMessage.setSmsMt(smsMtMessage);
								mqUtils.sendSmsRT(smsRtMessage.getGateWayID(), smsRtMessage.getMsgid(), FstObjectSerializeUtil.write(smsRtMessage));
								Map<String,String> param = Maps.newHashMap();
								param.put("id", id);
//								sqlSession.delete("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchDelete", param);
								index++;
								if(index%200 == 0){//200条提交一次
									sqlSession.commit();
								}
							}
						}
					}
					sqlSession.commit();
					if(list.size() < 1000){
						break;
					}else{
						maxId = new Long(list.get(1000-1).getSmsMt().getContentSize()).intValue();
					}
				}else{
					break;
				}*/
			}
			//sqlSession.commit();
			logger.info("重试reprot结束执行任务,时间："+DateUtils.getDateTime());
		}catch(Exception e){
			logger.debug("{}", e);
		}finally {
			if(sqlSession != null)sqlSession.close();
		}
	}
	
	public static String getTableIndex(String bizid)
	{
		if (StringUtils.isNotBlank(bizid))
		{
			return String.valueOf(new MsgId(bizid).getDay());
		}
		else
		{
			return "";
		}
	}

}
