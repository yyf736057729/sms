/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.swsms.service;

import java.io.File;
import java.util.*;

import com.siloyou.jmsg.modules.sms.dao.JmsgSmsWarnDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsWarn;
import org.apache.ibatis.session.SqlSessionFactory;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.config.Global;
import com.sanerzone.common.support.utils.FileUtils;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.EmailUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.MsgId;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.common.utils.RuleUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.GatewayResult;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;

/**
 * 短信任务发送Service
 * @author zhukc
 * @version 2016-07-20
 */
@Service
@Transactional(readOnly = true)
public class SwSmsTaskService extends CrudService<JmsgSmsTaskDao, JmsgSmsTask> {
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgSmsSendDao jmsgSmsSendDao;

	@Autowired
	private JmsgSmsWarnDao jmsgSmsWarnDao;

	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");

	public JmsgSmsTask get(String id) {
		return super.get(id);
	}
	
	@Transactional(readOnly = false)
    public String createSmsTask(JmsgSmsTask jmsgSmsTask, List<String> phoneList,int errorCount, int payCount) {
		try{
	        
	        StringBuilder tmpSb = new StringBuilder();
	        Iterator<String> it = phoneList.iterator();  
	        while (it.hasNext()) {  
	            tmpSb.append(it.next()).append("\r\n");
	        }  
	        
	        String fileDir = Global.getConfig("swSmsTask.phoneList.dir");
	        String fileName = fileDir + File.separator + jmsgSmsTask.getId() + ".txt";
	        FileUtils.createFile(fileName);
	        FileUtils.writeToFile(fileName, tmpSb.toString(), "UTF-8", false);
	        if(StringUtils.isBlank(jmsgSmsTask.getTaskType())){
	        	jmsgSmsTask.setTaskType("0");//普通短信
	        }
	        dao.insert(jmsgSmsTask);
		}catch(Exception e){
			logger.error("短信接收号码导入失败", e);
			return "短信接收号码导入失败";
		}
		
		String msg = "短信接收号码导入："+jmsgSmsTask.getSendCount()+"条,无效号码"+errorCount+"条";
		if("-1".equals(jmsgSmsTask.getStatus())){
			EmailUtil.CheckSmssendEmail("您有短信内容需要审核,提交信息:"+msg+", 用户名称:" + jmsgSmsTask.getCreateBy().getName() + ", 短信内容:" + jmsgSmsTask.getSmsContent());
			msg =msg+"；短信内容需要审核，审核成功后会自动发送";
		}
        
        return msg;
    }
	
	/**
	 * 组装任务发送明细
	 * @param user 用户
	 * @param tableName 表名
	 * @param taskId 任务ID
	 * @param phone 手机号
	 * @param smsContent 短息内容
	 * @param sign 签名
	 * @param payCount 扣条条数
	 * @param payType 扣费方式
	 * @param pushFlag 推送标识
	 * @param smsType 短信类型 2:长短信 1:普通短信
	 * @param checkContent 校验内容 true:需要 false:无需
	 */
	@Transactional(readOnly = false)
	public JmsgSmsSend createSendDetailNew(User user, String tableName, String taskId, String phone, String smsContent, 
									 String sign, int payCount, String payType, String pushFlag, String smsType,boolean checkContent){
		JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
		jmsgSmsSend.setCustomerOrderId(user.getId());
		jmsgSmsSend.setPayTime(new Date());
		MsgId msgId =  new MsgId();
		jmsgSmsSend.setId(msgId.toString());
		jmsgSmsSend.setDataId("188");
		jmsgSmsSend.setTaskId(taskId);//任务我ID
		jmsgSmsSend.setPhone(phone);//手机号码
		jmsgSmsSend.setSmsContent(smsContent);//短信内容
		jmsgSmsSend.setSmsType(smsType);//短信类型
		jmsgSmsSend.setPayCount(payCount);//扣费条数
		jmsgSmsSend.setUser(user);//用户ID,公司ID
		jmsgSmsSend.setChannelCode("");
		jmsgSmsSend.setSpNumber("");
		jmsgSmsSend.setMsgid(msgId.toString());
		Date date = new Date();
		jmsgSmsSend.setSendDatetimeQ(date);
		jmsgSmsSend.setSendDatetimeZ(date);
		jmsgSmsSend.setCreateDatetimeQ(date);
		jmsgSmsSend.setCreateDatetimeZ(date);
		String sendStatus = "T000";//待发
		String phoneType = "";//运营商
		String cityCode = "";//省市代码
		
		boolean runFlag = true;
		
		if(checkContent){
			String filResult = RuleUtils.filtrate(user.getId(), smsContent);
			if (!StringUtils.equals(filResult, "T0000")){
				logger.info(filResult);
				sendStatus = "F009"+filResult;//短信内容匹配规则失败
				runFlag = false;
			}
			
			/**
			if(!KeywordsUtils.exits(user.getKeyword(), smsContent)){
				sendStatus = "F";//短信内容未包含用户关键字
				runFlag = false;
			}**/
			
			if("1".equals(user.getFilterWordFlag())){//过滤敏感字
				String keywords = KeywordsUtils.keywords(smsContent);
				if(StringUtils.isNotBlank(keywords)){
					sendStatus = "F020";//发送内容包含敏感词["+keywords+"]";
					runFlag = false;
				}
			}

		}
		
		if(runFlag){
			Map<String,String> phoneMap = PhoneUtils.get(phone);
			if(phoneMap == null||phoneMap.size() <2){
				sendStatus = "F0170";//号段匹配异常
			}else{
				phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
				cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
				
				if(StringUtils.isBlank(cityCode) || StringUtils.isBlank(phoneType)){
					sendStatus = "F0170";//号段匹配异常
				}else{
					//1：校验 0： 不校验
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
		            else
		            {
		            	GatewayResult gatewayResult = gatewayMap(user.getSignFlag(),user.getGroupId(), phoneType, cityCode.substring(0, 2), sign, user.getId());
						if(gatewayResult.isExists()){
							jmsgSmsSend.setChannelCode(gatewayResult.getGatewayId());//通道代码
							String spNumber = gatewayResult.getSpNumber();
							if( 0 == user.getSignFlag() ){
								spNumber = spNumber + user.getId();
							}
							spNumber = spNumber + "188";
							if(spNumber.length() > 20) {
								spNumber = spNumber.substring(0, 20);
							}
							jmsgSmsSend.setSpNumber(spNumber);//接入号
						}else{
							sendStatus = gatewayResult.getErrorCode();//匹配通道失败
							sendStatus = "F";
							JmsgSmsWarn jmsgSmsWarn = new JmsgSmsWarn();
							jmsgSmsWarn.setWarnType("1");
							jmsgSmsWarn.setWarnContent("匹配通道失败");
							jmsgSmsWarn.setWarnStatus("0");
							jmsgSmsWarnDao.insert(jmsgSmsWarn);
						}
		            }
				}
			}
		}
		
		jmsgSmsSend.setPhoneType(phoneType);//运营商
		jmsgSmsSend.setAreaCode(cityCode);//省市代码
		jmsgSmsSend.setPayType(payType);//扣费方式
		jmsgSmsSend.setPayStatus("1");//扣费状态
		jmsgSmsSend.setPushFlag(pushFlag);//推送标识
		
		jmsgSmsSend.setSendStatus(sendStatus);//发送状态
		jmsgSmsSend.setSubmitMode("1");//提交方式 WEB :1 , API : 2
		jmsgSmsSend.setTopic(CacheKeys.getSmsBatchTopic());//发送队列
		jmsgSmsSend.setReportGatewayId("HTTP");
		jmsgSmsSend.setTableName(tableName);
		
		return jmsgSmsSend;
	}
	
	public JmsgSmsTask queryTextPhone(String txtPhone,String regex){
		JmsgSmsTask entity = new JmsgSmsTask();
		String[] phones = txtPhone.split(regex);
		int count = 0;
		Set<String> phoneList = new HashSet<String>();
		for (String string : phones) {
			count++;
			phoneList.add(string);
		}
		
		entity.setSendCount(count);
		entity.setPhoneList(phoneList);
		return entity;
	}
	
	//获取扣费条数
	public int findPayCount(String smsContent){
		SmsTextMessage sms = null;
		if(StringUtils.haswidthChar(smsContent)) {
			sms = new SmsTextMessage(smsContent,SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
		} else {
			sms = new SmsTextMessage(smsContent,SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
		}
		return sms.getPdus().length;
	}
	
	//获取通道代码  signFlag 1:验证签名 0:自定义签名 
	private GatewayResult gatewayMap(int signFlag,String groupId,String phoneType,String provinceId,String sign,String userId){
		
		GatewayResult entity = new GatewayResult();
		if(1 == signFlag){
			entity = GatewayUtils.getGateway(userId, groupId, phoneType, provinceId, sign);
		}else{
			entity = GatewayUtils.getGateway(groupId, phoneType, provinceId);
		}
		
		return entity;
		
	}
}