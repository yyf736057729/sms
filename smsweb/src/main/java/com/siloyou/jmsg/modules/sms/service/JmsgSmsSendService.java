/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsPush;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSendReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSendUserReport;

/**
 * 短信发送Service
 * @author zhukc
 * @version 2016-07-16
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsSendService extends CrudService<JmsgSmsSendDao, JmsgSmsSend> {
	
	@Autowired
    private MQUtils mQUtils;
	
	@DubboReference
	private SmsConfigInterface smsConfigInterface;

	public JmsgSmsSend get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsSend> findList(JmsgSmsSend jmsgSmsSend) {
		return super.findList(jmsgSmsSend);
	}
	
	public Page<JmsgSmsSend> findPage(Page<JmsgSmsSend> page, JmsgSmsSend jmsgSmsSend) {
		return super.findPage(page, jmsgSmsSend);
	}
	
	public Page<JmsgSmsSend> findSendDetailPage(Page<JmsgSmsSend> page, JmsgSmsSend jmsgSmsSend) {
		jmsgSmsSend.setPage(page);
		logger.info(xmlName + "===>>>queryJmsgSmsSend");
		page.setList(dao.queryJmsgSmsSend(jmsgSmsSend));
		return page;
	}
	
	public List<JmsgSmsSendReport> findSendDetailReportV2(JmsgSmsSend jmsgSmsSend) {
		logger.info(xmlName + "===>>>queryJmsgSmsSendReportV2");
		return dao.queryJmsgSmsSendReportV2(jmsgSmsSend);
	}
	
	public List<JmsgSmsSendUserReport> queryJmsgSmsSendUserReportV2(JmsgSmsSend param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.queryJmsgSmsSendUserReportV2(param);
	}
	
	public Page<JmsgSmsSend> findSendDetailPageV2(Page<JmsgSmsSend> page, JmsgSmsSend jmsgSmsSend) {
		jmsgSmsSend.setPage(page);
		logger.info(xmlName + "===>>>queryJmsgSmsSendV2");
		page.setList(dao.queryJmsgSmsSendV2(jmsgSmsSend));
		return page;
	}
	
	public Page<JmsgSmsSend> findSendReportPage(Page<JmsgSmsSend> page, JmsgSmsSend jmsgSmsSend) {
		jmsgSmsSend.setPage(page);
		logger.info(xmlName + "===>>>queryJmsgSmsReport");
		page.setList(dao.queryJmsgSmsReport(jmsgSmsSend));
		return page;
	}	
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsSend jmsgSmsSend) {
		super.save(jmsgSmsSend);
	}
	
	@Transactional(readOnly = false)
    public void insert(JmsgSmsSend jmsgSmsSend) {
        dao.insert(jmsgSmsSend);
    }
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsSend jmsgSmsSend) {
		super.delete(jmsgSmsSend);
	}
	
	//获取手机号码
	public List<String> findPhone(String taskId){
		logger.info(xmlName + "===>>>findPhoneByTaskId");
		return dao.findPhoneByTaskId(taskId);
	}
	
	public JmsgSmsSend queryJmsgSmsSend(JmsgSmsSend param){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		List<JmsgSmsSend> list = dao.queryJmsgSmsSend(param);
		if(list != null && list.size() > 0 )return list.get(0);
		return null;
	}
	
	public List<JmsgSmsPush> queryJmsgSmsPush(String bizid){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.queryJmsgSmsPush(bizid);
	}
	
	public List<JmsgSmsSend> queryJmsgSmsSendV2(JmsgSmsSend param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.queryJmsgSmsSendV2(param);
	}
	
	public JmsgSmsSend getV2(JmsgSmsSend param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.getV2(param);
	}
	
	public List<JmsgSmsPush> queryJmsgSmsPushV2(JmsgSmsPush param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.queryJmsgSmsPushV2(param);
	}
	
	/**
	 * 获取手机号码
	 * @param param
	 * @return
	 */
	public List<String> findPhoneV2(JmsgSmsSend param) {
		logger.info(xmlName + "===>>>findPhoneByTaskIdV2");
		return dao.findPhoneByTaskIdV2(param);
	}
	
	/**
	 * 根据条件获取推送列表
	 * @param map
	 * @return
	 */
	public List<JmsgSmsSend> findPushList(Map<String,Object> map) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findPushList(map);
	}
	
	public List<JmsgSmsSend> findSucReportPushList(Map<String,Object> map){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findSucReportPushList(map);
	}
	
	/**
	 * 推送
	 * @param param
	 * @return
	 */
	public boolean push(JmsgSmsSend param)
	{
		boolean result = true;
        //推送短信状态报告
        try {
        	SmsRtMessage smsRtMessage = new SmsRtMessage();
    		SmsMtMessage message = new SmsMtMessage();
    		message.setId(param.getId());
    		message.setSendStatus(param.getSendStatus());
    		message.setTaskid(param.getTaskId());
    		message.setUserid(param.getUser().getId());
    		message.setPayType(param.getPayType());
    		message.setCstmOrderID(param.getCustomerOrderId());
    		message.setUserReportNotify(param.getPushFlag());
    		message.setUserReportGateWayID(param.getReportGatewayId());
    		message.setMsgContent(param.getSmsContent());
    		message.setPhone(param.getPhone());
    		message.setSmsType(param.getSmsType());
    		message.setContentSize(param.getPayCount());
    		message.setPhoneType(param.getPhoneType());
    		message.setCityCode(param.getAreaCode());
    		message.setGateWayID(param.getChannelCode());
    		message.setSpNumber(param.getSpNumber());
    		
    		smsRtMessage.setSmsMt(message);
    		smsRtMessage.setMsgid(param.getMsgid());
            smsRtMessage.setDestTermID(message.getPhone());
            smsRtMessage.setDoneTime(formatDate(param.getSendDatetime().getTime()));
            
            
            if(StringUtils.startsWith(param.getSendStatus(),"P")) {//待发送
				return true;
			}
            
            if(StringUtils.startsWith(param.getSendStatus(), "F")){//发送失败
            	smsRtMessage.setStat(param.getSendStatus());
            }else{
            	if("0".equals(param.getPayType())){//提交成功
            		smsRtMessage.setStat("DELIVRD");
            	}else if("2".equals(param.getPayType())){//状态成功
            		if(StringUtils.startsWith(param.getReportStatus(), "T")){
            			smsRtMessage.setStat("DELIVRD");
            		}else if(StringUtils.startsWith(param.getReportStatus(), "F")){
            			smsRtMessage.setStat(param.getReportStatus().replace("F2", ""));
            		}else {
            			return true;
            		}
            	}
            }
            
			mQUtils.pushSmsMQ(message.getId(),message.getUserReportGateWayID() ,FstObjectSerializeUtil.write(smsRtMessage));
		} catch (Exception e) {
			result = false;
			logger.error("{}", e);
		}
        
        return result;
	}
	
	private String formatDate(Long longTime)
    {
        String time = DateFormatUtils.format(new Date(), "yyMMddHHmm");
        
        if (null != longTime)
        {
            time = DateFormatUtils.format(new Date(longTime), "yyMMddHHmm");
        }
        
        return time;
    }
	
	//测试匹配通道
	public void testGateway(String userId,String phone,String smsContent){
		smsConfigInterface.testGateway(userId, phone, smsContent);
	}
	
	public List<JmsgSmsSend> findJmsgSmsSendListByProvinceReport(Map<String,Object> map){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findJmsgSmsSendListByProvinceReport(map);
	}

	/**
	* @Description: 短信下行明细查询
	* @param:
	* @return:
	* @author: yuyunfeng
	* @Date: 2019/1/23
	*/
	public Page<JmsgSmsSend> getSmsList(Page<JmsgSmsSend> page,JmsgSmsSend jmsgSmsSend){
		jmsgSmsSend.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		page.setList(dao.getSmsList(jmsgSmsSend));
		return page;
	}

	/**
	 * @Description:通过id查询单条
	 * @param jmsgSmsSend
	 * @return jmsgSmsSend
	 */
	public JmsgSmsSend getSmsById(JmsgSmsSend jmsgSmsSend){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.getSmsById(jmsgSmsSend);
	}

	/**
	* @Description: 短信下行记录查询（时间）
	* @return:
	* @author: yuyunfeng
	* @Date: 2019/1/28
	*/
	public Page<JmsgSmsSend> getSmsListByTime(Page<JmsgSmsSend> page,JmsgSmsSend jmsgSmsSend){
		jmsgSmsSend.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		page.setList(dao.getSmsListByTime(jmsgSmsSend));
		return page;
	}



}