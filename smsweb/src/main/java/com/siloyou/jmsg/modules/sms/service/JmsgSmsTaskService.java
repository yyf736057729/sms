/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.support.config.Global;
import com.sanerzone.common.support.utils.FileUtils;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.EmailUtil;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.ValidatorUtils;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.MmsUtils;
import com.siloyou.jmsg.common.utils.MsgId;
import com.siloyou.jmsg.common.utils.MsgIdUtits;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.common.utils.RuleUtils;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.GatewayResult;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsData;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;

/**
 * 短信任务发送Service
 * @author zhukc
 * @version 2016-07-20
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsTaskService extends CrudService<JmsgSmsTaskDao, JmsgSmsTask> {
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgSmsDataService jmsgSmsDataService;
	
	@Autowired
	private JmsgSmsSendDao jmsgSmsSendDao;
	
	private static final int BATCH_COMMIT_MAX_COUNT = 500;//批量提交默认值
	
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");

	public JmsgSmsTask get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsTask> findList(JmsgSmsTask jmsgSmsTask) {
		return super.findList(jmsgSmsTask);
	}
	
	public Page<JmsgSmsTask> findPage(Page<JmsgSmsTask> page, JmsgSmsTask jmsgSmsTask) {
		return super.findPage(page, jmsgSmsTask);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsTask jmsgSmsTask) {
		super.save(jmsgSmsTask);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsTask jmsgSmsTask) {
		super.delete(jmsgSmsTask);
	}
	
	@Transactional(readOnly = false)
	public String insertTask(String dataId,String reviewStatus,Set<String> phoneList,String smsContent,Date sendDatetime,String userId,String sign,int payCount,boolean timeFlag){
		boolean taskFlag = false;
		String mq=CacheKeys.getSmsSingleTopic();//任务通道
		int count = phoneList.size();
		
		if(count >50 || "9".equals(reviewStatus) || timeFlag){//发送条数大于50 或者是待审短信或者定时任务 需要创建任务
			taskFlag = true;
			mq = CacheKeys.getSmsBatchTopic();
		}
		
		Map<String,String> map = insertTask(dataId, reviewStatus, phoneList, smsContent, sendDatetime, userId, count,"WEB",mq,taskFlag,sign,payCount);
//		if(!taskFlag){//队列发送
//			if("1".equals(map.get("code"))){
//				String taskId = map.get("taskId");
//				mQUtils.sendSmsMQ(taskId, "SMS_SINGLE_TASK_TOPIC", "sigle", taskId.getBytes());
//			}
//		}
		
		return map.get("msg");
	}
	
	/**
	 * ===========
	 * @param dataId
	 * @param reviewStatus
	 * @param phoneList
	 * @param smsContent
	 * @param sendDatetime
	 * @param userId
	 * @param count
	 * @param submitMode
	 * @param topic
	 * @param taskFlag
	 * @param sign
	 * @param payCount
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String,String> insertTask(String dataId,String reviewStatus,Set<String> phoneList,String smsContent,Date sendDatetime,
			String userId,int count,String submitMode,String topic,boolean taskFlag,
			String sign,int payCount){
		Map<String,String> result = Maps.newHashMap();
		if(sendDatetime == null){
			sendDatetime = new Date();
		}
		String code = "1";
		String msg = "";
		String status = "-1";//待审
		String taskId = MsgIdUtits.msgId("T");//生成任务ID
//		if(taskFlag){//待审
			createTask(taskId,smsContent,phoneList.size() * payCount,sendDatetime,status,userId);//创建任务
//		}
		int submitCount=0,errorCount=0;
		JmsgSmsTask param = new JmsgSmsTask();
		param.setId(taskId);
		try {
			Map<String,String> map = createSendDetail(dataId,taskId, userId, phoneList, smsContent,
					submitMode, topic,sign,payCount,sendDatetime);//任务发送明细
			if("1".equals(map.get("errorCode"))){
				submitCount = Integer.valueOf(map.get("submitCount"));
				errorCount = Integer.valueOf(map.get("errorCount"));
				
				//修改任务发送数量、发送状态
//				if(taskFlag){
					param.setSendCount(submitCount * payCount);
					if(!"9".equals(reviewStatus))status = "1";
					param.setStatus(status);
					logger.info(xmlName + "===>>>updateJmsgSmsTask");
					dao.updateJmsgSmsTask(param);
//				}
				msg = "短信接收号码导入："+count+"条，成功："+submitCount+"条，重复："+(count-submitCount-errorCount)+"条，异常号码："+errorCount+"条";
				if(count == 0)taskId="";
			}else{
				dao.delete(param);//删除任务
				code ="0";
				msg = "短信发送失败,系统错误";
			}
			
		} catch (Exception e) {
			code ="0";
			msg = "短信发送失败,系统错误";
			e.printStackTrace();
		}
		result.put("code", code);
		result.put("msg",msg);
		result.put("taskId", taskId);
		return  result;
	}
	
	
	@Transactional(readOnly = false)
    public Map<String,String> insertMmsTask(String dataId,String reviewStatus,Set<String> phoneList,String smsContent,Date sendDatetime,String userId,int count,String submitMode,String topic,boolean taskFlag,String sign,int payCount){
        Map<String,String> result = Maps.newHashMap();
        String code = "1";
        String msg = "";
        String status = "-1";//待审
        String taskId = MsgIdUtits.msgId("T");//生成任务ID
        if(taskFlag){//待审
            createTask(taskId,dataId,phoneList.size(),sendDatetime,status,userId);//创建任务
        }
        int submitCount=0,errorCount=0;
        JmsgSmsTask param = new JmsgSmsTask();
        param.setId(taskId);
        try {
            //String mms = Integer.toHexString(Integer.parseInt(taskId));
            String mmsFrom  = "106";
            List<Dict> dictList = DictUtils.getDictList("mmsfrom");
            if (null != dictList && dictList.size() > 0)
            {
                Collections.shuffle(dictList);
                mmsFrom = dictList.get(0).getValue();
            }
            
            byte[] content = MmsUtils.mmsInstance.makeMmsPdu(taskId, SignUtils.get(smsContent), mmsFrom, "<div>"+SignUtils.getContent(smsContent)+"</div>");//制作彩信
            if(content != null){
                EhCacheUtils.put("mmsFileCache", taskId, content);
            }
            
            Map<String,String> map = createMmsSendDetail(dataId,taskId, userId, phoneList, smsContent,submitMode, topic,sign,payCount);//任务发送明细
            if("1".equals(map.get("errorCode"))){
                submitCount = Integer.valueOf(map.get("submitCount"));
                errorCount = Integer.valueOf(map.get("errorCount"));
                
                //修改任务发送数量、发送状态
                if(taskFlag){
                    param.setSendCount(submitCount);
                    if(!"9".equals(reviewStatus))status = "1";
                    param.setStatus(status);
					logger.info(xmlName + "===>>>updateJmsgSmsTask");
                    dao.updateJmsgSmsTask(param);
                }
                msg = "短信接收号码导入："+count+"条，成功："+submitCount+"条，重复："+(count-submitCount-errorCount)+"条，异常号码："+errorCount+"条";
                if(count == 0)taskId="";
            }else{
                dao.delete(param);//删除任务
                code ="0";
                msg = "短信发送失败,系统错误";
            }
            
        } catch (Exception e) {
            code ="0";
            msg = "短信发送失败,系统错误";
            e.printStackTrace();
        }
        result.put("code", code);
        result.put("msg",msg);
        result.put("taskId", taskId);
        return  result;
    }
	
	//创建任务
	@Transactional(readOnly = false)
	private String createTask(String taskId,String smsContent,int sendCount,Date sendDatetime,String status,String userId){
		JmsgSmsTask jmsgSmsTask = new JmsgSmsTask();
		jmsgSmsTask.setId(taskId);
		jmsgSmsTask.setSmsContent(smsContent);
		jmsgSmsTask.setSendCount(sendCount);
		jmsgSmsTask.setSendDatetime(sendDatetime);
		jmsgSmsTask.setStatus(status);
		jmsgSmsTask.setCreateBy(new User(userId));
		dao.insert(jmsgSmsTask);
		return jmsgSmsTask.getId();
	}
	
	
	
	
	//任务发送明细
	@Transactional(readOnly = false)
	public Map<String,String> createSendDetail(String sendtermid,String taskId,String userId,Set<String> phoneList,String smsContent,
			String submitMode,String topic,String sign,int payCount,Date sendDatetime) throws Exception{
		Map<String,String> map = Maps.newHashMap();
		if(StringUtils.isBlank(taskId)){
			map.put("errorCode", "0");
			return map;
		}
		boolean timeFlag = false;//不是跨天任务
		int tableIndex = DateUtils.getDayOfMonth(sendDatetime);//获取天
		String tableName = "jmsg_sms_send_"+tableIndex;
		
		int todayIndex = DateUtils.getDayOfMonth(0);
		if(tableIndex != todayIndex){
			timeFlag = true;
		}
		
		User user = UserUtils.get(userId);
		Integer submitCount=0,errorCount=0;
		String errorCode = "1";
		String smsType = payCount >1 ?"2":"1";//1:短短信 2:长短信
		String payType = findPayType(userId);
		String payStatus = "1";//TODO 扣费状态
		String pushFlag = "9";//待推送
		if(StringUtils.isBlank(user.getCallbackUrl())){
			pushFlag = "0";//无需推送
		}
		if(!StringUtils.isNumeric(sendtermid)) {
        	sendtermid = "";
        }
		SqlSession sqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			JmsgSmsSend jmsgSmsSend;
			MsgId msgid;
			for (String phone : phoneList) {
				jmsgSmsSend = new JmsgSmsSend();
				if(timeFlag){//跨天
					msgid= new MsgId(1000000);
					msgid.setDay(DateUtils.getDayOfMonth(tableIndex));
				}else{
					msgid = new MsgId();
				}
				jmsgSmsSend.setId(msgid.toString());
				jmsgSmsSend.setDataId("188");
				jmsgSmsSend.setTaskId(taskId);//任务我ID
				jmsgSmsSend.setPhone(phone);//手机号码
				jmsgSmsSend.setSmsContent(smsContent);//短信内容
				jmsgSmsSend.setSmsType(smsType);//短信类型
				jmsgSmsSend.setPayCount(payCount);//扣费条数
				jmsgSmsSend.setUser(user);//用户ID,公司ID
				if(!ValidatorUtils.isMobile(phone)){//验证是不是手机号
					errorCount++;
					continue;
				}
				String sendStatus = "T000";//待发
				String phoneType = "";//运营商
				String cityCode = "";//省市代码
				
				Map<String,String> phoneMap = PhoneUtils.get(phone);
				if(phoneMap == null||phoneMap.size() <2){
					sendStatus = "F0170";//号段匹配异常
					jmsgSmsSend.setSendDatetime(sendDatetime);
				}else{
					phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
					cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
					
					if(StringUtils.isBlank(cityCode) || StringUtils.isBlank(phoneType)){
						sendStatus = "F0170";//号段匹配异常
						jmsgSmsSend.setSendDatetime(sendDatetime);
					}else{
						//1：校验 0： 不校验
			            if (user.getSysBlacklistFlag() == 1 && BlacklistUtils.isExistSysBlackList(phone))
			            {
			                // 判断是否系统黑名单
			                sendStatus = "F002";
			                jmsgSmsSend.setSendDatetime(sendDatetime);
			            }
			            else if (user.getUserBlacklistFlag() == 1 && BlacklistUtils.isExistUserBlackList(phone))
			            {
			                // 判断是否营销黑名单
			                sendStatus = "F008";
			                jmsgSmsSend.setSendDatetime(sendDatetime);
			            }
			            else
			            {
			            	GatewayResult gatewayResult = gatewayMap(user.getSignFlag(),user.getGroupId(), phoneType, cityCode.substring(0, 2), sign, userId);
							if(gatewayResult.isExists()){
								jmsgSmsSend.setChannelCode(gatewayResult.getGatewayId());//通道代码
								String spNumber = gatewayResult.getSpNumber();
								if( 0 == user.getSignFlag() ){
									spNumber = spNumber + user.getId();
								}
								spNumber = spNumber + sendtermid;
								if(spNumber.length() > 20) {
									spNumber = spNumber.substring(0, 20);
								}
								jmsgSmsSend.setSpNumber(spNumber);//接入号
							}else{
								sendStatus = gatewayResult.getErrorCode();//匹配通道失败
								jmsgSmsSend.setSendDatetime(sendDatetime);
							}
			            }
					}
				}
				
				jmsgSmsSend.setPhoneType(phoneType);//运营商
				jmsgSmsSend.setAreaCode(cityCode);//省市代码
				jmsgSmsSend.setPayType(payType);//扣费方式
				jmsgSmsSend.setPayStatus(payStatus);//扣费状态
				jmsgSmsSend.setPushFlag(pushFlag);//推送标识
				
				jmsgSmsSend.setSendStatus(sendStatus);//发送状态
				jmsgSmsSend.setSubmitMode(submitMode);//提交方式 WEB,API
				jmsgSmsSend.setTopic(topic);//发送队列
				jmsgSmsSend.setReportGatewayId("HTTP");
				jmsgSmsSend.setTableName(tableName);
				sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
				submitCount++;
				if(submitCount % BATCH_COMMIT_MAX_COUNT == 0) {
					sqlSession.commit();
				}
			}
			sqlSession.commit();
			
//			if(money >0){
//				jmsgAccountService.consumeMoneyV2(userId, "02", money, "sms", "自消费操作(流水批次ID："+taskId+")", userId, taskId);//自消费
//			}
			
		}catch(Exception e){
			errorCode = "0";
			throw e;
		}finally{
			if(sqlSession != null){
				sqlSession.close();
			}
		}
		map.put("errorCode", errorCode);
		map.put("submitCount", String.valueOf(submitCount));
		map.put("errorCount", String.valueOf(errorCount));
		return map;
	}
	
	//任务发送明细
    @Transactional(readOnly = false)
    public Map<String,String> createMmsSendDetail(String dataId,String taskId,String userId,Set<String> phoneList,String smsContent,String submitMode,String topic,String sign,int payCount) throws Exception{
        Map<String,String> map = Maps.newHashMap();
        if(StringUtils.isBlank(taskId)){
            map.put("errorCode", "0");
            return map;
        }
        User user = UserUtils.get(userId);
        Integer submitCount=0,errorCount=0;
        int money=0;
        String errorCode = "1";
        //String smsType = payCount >1 ?"2":"1";//1:短短信 2:长短信
        String payType = findPayType(userId);
        String payStatus = "1";//TODO 扣费状态
        String pushFlag = "9";//待推送
        if(StringUtils.isBlank(user.getCallbackUrl())){
            pushFlag = "0";//无需推送
        }
        
        SqlSession sqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH,false);
        try{
            JmsgSmsSend jmsgSmsSend;
            for (String phone : phoneList) {
                jmsgSmsSend = new JmsgSmsSend();
                jmsgSmsSend.setId(new MsgId().toString());
                jmsgSmsSend.setDataId(dataId);
                jmsgSmsSend.setTaskId(taskId);//任务我ID
                jmsgSmsSend.setPhone(phone);//手机号码
                jmsgSmsSend.setSmsContent(smsContent);//短信内容
                jmsgSmsSend.setSmsType("3");//短信类型 1:短短信 2:长短信 3:彩信
                jmsgSmsSend.setPayCount(payCount);//扣费条数
                jmsgSmsSend.setUser(user);//用户ID,公司ID
                if(!ValidatorUtils.isMobile(phone)){//验证是不是手机号
                    errorCount++;
                    continue;
                }
                String sendStatus = "P000";//待发
                String phoneType = "";//运营商
                String cityCode = "";//省市代码
                Map<String,String> phoneMap = PhoneUtils.get(phone);
                if(phoneMap == null||phoneMap.size() <2){
                    sendStatus = "F0170";//号段匹配异常
                    jmsgSmsSend.setSendDatetime(new Date());
                }else{
                    phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
                    cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
                    
                    if(StringUtils.isBlank(cityCode) || StringUtils.isBlank(phoneType)){
                        sendStatus = "F0170";//号段匹配异常
                        jmsgSmsSend.setSendDatetime(new Date());
                    }else{
                        GatewayResult gatewayResult = gatewayMap(user.getSignFlag(),user.getGroupId(), phoneType, cityCode.substring(0, 2), sign, userId);
                        if(gatewayResult.isExists()){
                            jmsgSmsSend.setChannelCode(gatewayResult.getGatewayId());//通道代码
                            jmsgSmsSend.setSpNumber(gatewayResult.getSpNumber());//接入号
                        }else{
                            sendStatus = gatewayResult.getErrorCode();//匹配通道失败
                            jmsgSmsSend.setSendDatetime(new Date());
                        }
                    }
                }
                
                jmsgSmsSend.setPhoneType(phoneType);//运营商
                jmsgSmsSend.setAreaCode(cityCode);//省市代码
                jmsgSmsSend.setPayType(payType);//扣费方式
                jmsgSmsSend.setPayStatus(payStatus);//扣费状态
                jmsgSmsSend.setPushFlag(pushFlag);//推送标识
                
                jmsgSmsSend.setSendStatus(sendStatus);//发送状态
                jmsgSmsSend.setSubmitMode(submitMode);//提交方式 WEB,API
                jmsgSmsSend.setTopic(topic);//发送队列
                jmsgSmsSend.setReportGatewayId("HTTP");
                money+=payCount;//扣费条数
                sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
                submitCount++;
                if(submitCount % BATCH_COMMIT_MAX_COUNT == 0) {
                    sqlSession.commit();
                }
            }
            sqlSession.commit();
            
            if(money >0){
                jmsgAccountService.consumeMoney(userId, "02", money, "sms", "自消费操作(流水批次ID："+taskId+")", userId, taskId);//自消费
            }
            
        }catch(Exception e){
            errorCode = "0";
            throw e;
        }finally{
            if(sqlSession != null){
                sqlSession.close();
            }
        }
        map.put("errorCode", errorCode);
        map.put("submitCount", String.valueOf(submitCount));
        map.put("errorCount", String.valueOf(errorCount));
        return map;
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
	
	//扣费方式
	private String findPayType(String userId){
		return UserUtils.getPayMode(userId, "sms");
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
	
	@Transactional(readOnly = false)
	public String save(Set<String> phoneList,String userId,String dataId,String reviewStatus,String content,Date sendDatetime,String sign,int payCount,boolean timeFlag){
		String msg = "";
		
		msg = this.insertTask(dataId, reviewStatus, phoneList, content, sendDatetime, userId, sign, payCount, timeFlag);
		if("9".equals(reviewStatus)){
			msg = msg+"<br/>"+"短信内容需要审核，审核通过后会自动发送。";
		}
		
		return msg;
	}
	
	public JmsgSmsTask queryFilePhone(MultipartFile phoneFile){
		JmsgSmsTask entity = new JmsgSmsTask();
		
		Set<String> phoneList = new HashSet<String>();
		if (StringUtils.isNotBlank(phoneFile.getName())) {
			try {
				InputStreamReader reader = new InputStreamReader(phoneFile.getInputStream());
				BufferedReader br = new BufferedReader(reader);
				int count = 0;
				for(String line = br.readLine();line !=null;line=br.readLine()){
					if(StringUtils.isNotBlank(line)){
						count++;
						phoneList.add(StringUtils.trim(line));
					}
				}
				entity.setSendCount(count);
				entity.setPhoneList(phoneList);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return entity;
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
	
	@Transactional(readOnly = false)
	public void updateStatus(JmsgSmsTask jmsgSmsTask){
		Map<String,String> pMap = Maps.newHashMap();
		pMap.put("status", jmsgSmsTask.getStatus());
		pMap.put("updateBy",UserUtils.getUser().getId());
		pMap.put("taskId",jmsgSmsTask.getId());
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.updateStatus(pMap);
	}
	
	/**
	 * 获取短信ID
	 * 
	 * @param userId 用户ID
	 * @param content 用户内容
	 * @param noCheck 是否审核 1:免审 0:必审
	 * @param contentType 信息类型 sms:短信 mms:彩信
	 * @return 短信id   id="" 则短信审核中
	 */
	@Transactional(readOnly = false)
	public Map<String,String> queryDataId(String userId,String content,String noCheck, String contentType){
		Map<String,String> map = Maps.newHashMap();
		
		String reviewStatus = "9";//待审
		String id = "188";
		Date reviewDatetime = null;
		if("1".equals(noCheck)){
			reviewStatus = "8";//免审
			reviewDatetime = new Date();
		} else {
			String contentKey = HttpRequest.md5(content);
			JmsgSmsData result = jmsgSmsDataService.findJmsgSmsDataByContentKey(userId, contentKey);//根据内容和用户获取短信素材
			if(result != null){//已经存在素材
				if("1".equals(result.getReviewStatus())||"8".equals(result.getReviewStatus())){//审核通过
					id = result.getId();
					reviewStatus = "8";//免审
				}
			}else{
				id = jmsgSmsDataService.save(content, contentType, userId, contentKey, reviewStatus, reviewDatetime, "1", "0");
			}
		}
		map.put("dataId", id);
		map.put("reviewStatus", reviewStatus);
		return map;
	}
	
	/**
	 * 发送任务报表汇总
	 * 
	 * @param
	 * @param
	 */
	@Transactional(readOnly = false)
	public void smsTaskReport(int day){
		String tableName = "jmsg_sms_send_"+TableNameUtil.getTableIndex(day);
		JmsgSmsTask param = new JmsgSmsTask();
		Date sendDatetimeQ = DateUtils.getDay(DateUtils.getDay(day),0,0,0);
		Date sendDatetimeZ = DateUtils.getDay(DateUtils.getDay(day),23,59,59);
		param.setSendDatetimeQ(sendDatetimeQ);
		param.setSendDatetimeZ(sendDatetimeZ);

		logger.info(xmlName + "===>>>findSmsTaskReport");
		List<JmsgSmsTask> list = dao.findSmsTaskReport(param);
		
		if(list != null && list.size() > 0){
			JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
			jmsgSmsSend.setTableName(tableName);
			for (JmsgSmsTask jmsgSmsTask : list) {
				//if(jmsgSmsTask.getSendCount() - jmsgSmsTask.getSuccessCount() == jmsgSmsTask.getFailCount())continue;
				jmsgSmsSend.setTaskId(jmsgSmsTask.getId());
				String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
				logger.info(xmlName + "===>>>findSmsSendByTaskId");
				JmsgSmsTask report = jmsgSmsSendDao.findSmsSendByTaskId(jmsgSmsSend);
				if(report != null){
					jmsgSmsTask.setSuccessCount(report.getSuccessCount());
					jmsgSmsTask.setFailCount(report.getFailCount());
					logger.info(xmlName + "===>>>updateReport");
					dao.updateReport(jmsgSmsTask);
				}
			}
		}
	}
	
	@Transactional(readOnly = false)
    public String createSmsTask(JmsgSmsTask jmsgSmsTask, List<String> phoneList,int errorCount, int payCount) {
		try{
	        
	        StringBuilder tmpSb = new StringBuilder();
	        Iterator<String> it = phoneList.iterator();  
	        while (it.hasNext()) {  
	            tmpSb.append(it.next()).append("\r\n");
	        }  
	        
	        String fileDir = Global.getConfig("smsTask.phoneList.dir");
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
	
	@Transactional(readOnly = false)
    public String createSmsTask(JmsgSmsTask jmsgSmsTask, Set<String> phoneList, int payCount) {
		try{
	        StringBuilder tmpSb = new StringBuilder();
	        for (String phone : phoneList) {
	        	tmpSb.append(phone).append("\r\n");
			}
	        
	        String date = DateUtils.formatDate(jmsgSmsTask.getSendDatetime(), "yyyy-MM-dd");
	        
	        String fileDir = Global.getConfig("smsTask.phoneList.dir")+File.separator+date;
	        String fileName = fileDir + File.separator + jmsgSmsTask.getId() + ".txt";
	        FileUtils.createFile(fileName);
	        FileUtils.writeToFile(fileName, tmpSb.toString(), "UTF-8", false);
	        if(StringUtils.isBlank(jmsgSmsTask.getTaskType())){
	        	jmsgSmsTask.setTaskType("0");//普通短信
	        }
	        dao.insert(jmsgSmsTask);
		}catch(Exception e){
			logger.error("短信接收号码导入失败", e);
			return "1";
		}
        
        return "0";//成功
    }
	
	@Transactional(readOnly = false)
	public String httpSmsTask(String userId,String taskId,String smsContent,int count,String status,Date sendDatetime,List<String> phoneList){
		User createBy = null;
		try{
        	String filResult = RuleUtils.filtrate(userId, smsContent);
    		if (!StringUtils.equals(filResult, "T0000")){
    			logger.info("http批量短信发送号码,短信内容匹配规则失败："+filResult);
    			return "2";
    		}
    		createBy = UserUtils.get(userId);
			JmsgSmsTask jmsgSmsTask= new JmsgSmsTask();
    		jmsgSmsTask.setId(taskId);
            jmsgSmsTask.setSendDatetime(sendDatetime);
    		jmsgSmsTask.setSmsContent(smsContent);
    		jmsgSmsTask.setSendCount(count);
    		jmsgSmsTask.setCreateBy(createBy);
	        jmsgSmsTask.setTaskType("0");//普通短信
	        jmsgSmsTask.setStatus(status);
	        
	        StringBuilder tmpSb = new StringBuilder();
	        Iterator<String> it = phoneList.iterator();  
	        while (it.hasNext()) {  
	            tmpSb.append(it.next()).append("\r\n");
	        }
	        
	        String fileDir = Global.getConfig("smsTask.phoneList.dir");
	        String fileName = fileDir + File.separator + jmsgSmsTask.getId() + ".txt";
	        FileUtils.createFile(fileName);
	        FileUtils.writeToFile(fileName, tmpSb.toString(), "UTF-8", false);
	        dao.insert(jmsgSmsTask);
		}catch(Exception e){
			logger.error("http批量短信发送号码导入失败", e);
			return "-1";
		}
		
		String msg = "短信接收号码导入："+count+"条";
		if("-1".equals(status)){
			EmailUtil.CheckSmssendEmail("您有短信内容需要审核,提交信息:"+msg+", 用户名称:" + createBy.getName() + ", 短信内容:" + smsContent);
			msg =msg+"；短信内容需要审核，审核成功后会自动发送";
		}
        
        return "1";
	}
	
	//点对点短息任务
	@Transactional(readOnly = false)
    public String createSmsTaskDot(JmsgSmsTask jmsgSmsTask) {
		try{
	        dao.insert(jmsgSmsTask);
	        String fileDir = Global.getConfig("smsTask.phoneList.dir");
	        String fileName = fileDir + File.separator + jmsgSmsTask.getId() + ".txt";
	        FileUtils.createFile(fileName);
	        FileUtils.writeToFile(fileName, jmsgSmsTask.getPhones(), "UTF-8", false);
	        
		}catch(Exception e){
			logger.error("短信接收号码导入失败", e);
			return "短信接收号码导入失败";
		}
		String msg = "短信接收号码导入："+jmsgSmsTask.getSendCount()+"条";
		if("-1".equals(jmsgSmsTask.getStatus())){
			EmailUtil.CheckSmssendEmail("您有短信内容需要审核, 用户名称:" + jmsgSmsTask.getCreateBy().getName() + ", 短信内容:定制短信普通点对点");
			msg =msg+"；短信内容需要审核，审核成功后会自动发送";
		}
        
        return msg;
    }
	
	public void inputstreamToFile(InputStream ins,File file){
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(os !=null){
					os.close();
				}
				if(ins != null){
					ins.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	public JmsgSmsSend createSendDetailNew(User user, String tableName, String taskId, String phone, String smsContent, 
									 String sign, int payCount, String payType, String pushFlag, String smsType,boolean checkContent){
		JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
		jmsgSmsSend.setId(new MsgId().toString());
		jmsgSmsSend.setDataId("188");
		jmsgSmsSend.setTaskId(taskId);//任务我ID
		jmsgSmsSend.setPhone(phone);//手机号码
		jmsgSmsSend.setSmsContent(smsContent);//短信内容
		jmsgSmsSend.setSmsType(smsType);//短信类型
		jmsgSmsSend.setPayCount(payCount);//扣费条数
		jmsgSmsSend.setUser(user);//用户ID,公司ID
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
		jmsgSmsSend.setSubmitMode("WEB");//提交方式 WEB,API
		jmsgSmsSend.setTopic(CacheKeys.getSmsBatchTopic());//发送队列
		jmsgSmsSend.setReportGatewayId("HTTP");
		jmsgSmsSend.setTableName(tableName);
		
		return jmsgSmsSend;
	}
	
	//审核短信内容
	@Transactional(readOnly = false)
	public void reviewSmsContent(JmsgSmsTask jmsgSmsTask){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.reviewSmsContent(jmsgSmsTask);
	}
	
	//修改短息内容
	@Transactional(readOnly = false)
	public void updateSmsContent(JmsgSmsTask jmsgSmsTask){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.updateSmsContent(jmsgSmsTask);
	}
	
	//待审核条数
	public int reviewCount(){
		logger.info(xmlName + "===>>>findReviewCount");
		return dao.findReviewCount();
	}

	/**
	 * @Description: 一键审核
	 * @param: jmsgSmsTask
	 * @return: void
	 * @author: zhanghui
	 * @Date: 2019-01-10
	 */
	@Transactional(readOnly = false)
	public void onekeyReview(JmsgSmsTask jmsgSmsTask){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.onekeyReview(jmsgSmsTask);
	}


}