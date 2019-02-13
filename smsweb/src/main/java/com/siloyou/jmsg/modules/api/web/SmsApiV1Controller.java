package com.siloyou.jmsg.modules.api.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.sanerzone.common.support.utils.SystemClock;
import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.utils.Base64Util;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.IPUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StreamUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.dao.UserDao;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.MsgId;
import com.siloyou.jmsg.common.utils.MsgIdUtits;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;
import com.siloyou.jmsg.modules.sms.task.impl.SmsSendExecutor;


@Controller
@RequestMapping(value = "")
public class SmsApiV1Controller extends BaseController {
	
	public static Logger logger = LoggerFactory.getLogger(SmsApiV1Controller.class);
	
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	
	@Autowired
	private JmsgSmsTaskService jmsgSmsTaskService;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private MQUtils mQUtils;
	
	
	@SuppressWarnings("rawtypes")
	public Map getPostDataMap(HttpServletRequest request,HttpServletResponse response){
		try{
			String charEncoding = request.getCharacterEncoding();
			if (charEncoding == null) {
				charEncoding = "UTF-8";
			}
			String respText = StreamUtils.InputStreamTOString(request.getInputStream(), charEncoding);
			if( StringUtils.isNotBlank(respText)) {
				String jsonString = new String(Base64Util.decode(respText));
				return (Map) JsonMapper.fromJsonString(jsonString, Map.class);
			}
		}catch(IOException e) {
			
		}
		return null;
	}
	
	@RequestMapping(value = "sismsapi.go")
	public String select (HttpServletRequest request, HttpServletResponse response) {
		logger.info("sms api v1: {}", JsonMapper.toJsonString(request.getParameterMap()));
		String method = request.getParameter("method");
		if("smssend".equals(method)){
			return smsSend(request, response);
		}else if("balanceQuery".equals(method)){
			return smsSendBalance(request, response);
		}
		return "";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "sms/send")
	public String smsSend(HttpServletRequest request, HttpServletResponse response) {
		Map map = Maps.newHashMap();
		long time = System.currentTimeMillis();
		try{
			String userId = request.getParameter("userid");
			String sendtime = request.getParameter("sendtime");
			map = validate(userId, request);

			if(!StringUtils.equals(String.valueOf(map.get("code")),"0")){//验证不通过
				return result(response,map);
			}
			
			Date sendDatetime = null;
			if("1".equals(sendtime)){
				sendDatetime = new Date();
			} else {
				sendDatetime = sendDatetime(sendtime);
				if(sendDatetime == null){
					map.put("code", "8");
					map.put("msg","时间格式错误");
					return result(response,map);
				}
			}
			
			String nocheck = (String)map.get("nocheck");
			String sign = (String)map.get("sign");
			int reviewCount = (Integer)map.get("reviewCount");
			map.remove("nocheck");
			map.remove("sign");
			map.remove("reviewCount");
			
			//Map content = getPostDataMap(request);
			//if( content != null && !content.isEmpty()) {
				
//				String phones = (String)content.get("phones");//发送号码
//				String smsContent = (String)content.get("content");//发送内容
				String phones = request.getParameter("phones");
				String smsContent = StringEscapeUtils.unescapeHtml4(request.getParameter("content"));
//				StringEscapeUtils.unescapeHtml4(request.getParameter("msgcontent"));
				
				if(phones.length() > 120000) {//一次性发送10000个 TODO
					map.put("code", "14");
					map.put("msg","号码个数超过限制");
					return result(response,map);
				}
				smsContent = smsContent.trim();
				
				smsContent = SignUtils.formatContent(smsContent);
	            if (StringUtils.isBlank(sign))
				{
	            	sign = UserUtils.get(userId).getForceSign();
	            	if(StringUtils.isNotBlank(sign)) {
	            		smsContent = "【"+sign+"】" + smsContent;
	            	}
				}
				
//				Map<String,String> dataMap = jmsgSmsTaskService.queryDataId(userId, smsContent,nocheck);//获取短信ID
//				String dataId = dataMap.get("dataId");
//				if(StringUtils.isBlank(dataId)){
//					map.put("code", "13");
//					map.put("msg","短信内容正在审核中");
//					return result(response,map);//TODO 短信内容正在审核中
//				}
				
				JmsgSmsTask phoneEntity = jmsgSmsTaskService.queryTextPhone(phones,",");//获取号码
				
				Set<String> phoneList = phoneEntity.getPhoneList();
				int payCount = jmsgSmsTaskService.findPayCount(smsContent);//单个短信 扣费条数 70字一条
				
				int count = phoneList.size();
				if(count > 0){
					//余额
	                String amountKey = AccountCacheUtils.getAmountKey("amount", "sms", userId);
	                // 扣款
	                long amount = JedisClusterUtils.decrBy(amountKey, count * payCount);
	                if (amount < 0)
	                {
	                	JedisClusterUtils.incrBy(amountKey, count * payCount);
	                	
	                	 map.put("code", "3");
	                     map.put("msg", "余额不足");
	                     return result(response, map);
	                }
	            	
	                String sendtermid = StringUtils.stripToEmpty(request.getParameter("extnum"));//扩展号
	                
					if("2".equals(nocheck)){//自动审核
						if(reviewCount >= count){
							nocheck="1";//免审
						}else{
							nocheck="0";//必审
						}
					}
					/*Map<String,String> dataMap = jmsgSmsTaskService.queryDataId(userId, smsContent,nocheck, "sms");//获取短信ID
					String dataId = dataMap.get("dataId");
					if(StringUtils.isBlank(dataId)){
						map.put("code", "13");
						map.put("msg","短信内容正在审核中");
						return result(response,map);//TODO 短信内容正在审核中
					}*/
					
					
					String mq=CacheKeys.getSmsSingleTopic();//任务通道
					//String reviewStatus = dataMap.get("reviewStatus");//审核标识 8:免审 9:待审
					//if("9".equals(reviewStatus) || phoneList.size() > 50 || sendDatetime.compareTo(new Date()) > 0){
					if("0".equals(nocheck) || phoneList.size() > 50 || sendDatetime.compareTo(new Date()) > 0){
//						mq = CacheKeys.getSmsBatchTopic();
//						Map<String,String> insertMap = jmsgSmsTaskService.insertTask(sendtermid, reviewStatus, phoneList, smsContent, sendDatetime, userId, phoneList.size(),"API",mq,true,sign,payCount);
//						if("1".equals(insertMap.get("code"))){
//							map.put("taskid", insertMap.get("taskId"));
//							map.put("msg", insertMap.get("msg"));
//						}else{
//							map.put("code", "1");
//							map.put("msg", "提交失败");
//						}
						
						JmsgSmsTask jmsgSmsTask= new JmsgSmsTask();
	                    String taskId = new MsgId(1000002).toString();
	                    jmsgSmsTask.setSendDatetime(new Date());
	            		jmsgSmsTask.setId(taskId);
	            		jmsgSmsTask.setSmsContent(smsContent);
	            		jmsgSmsTask.setSendCount(count);
	            		jmsgSmsTask.setCreateBy(UserUtils.get(userId));
	            		
	            		if ("0".equals(nocheck))
	            		{
	            			jmsgSmsTask.setStatus("-1");
	            		}
	            		else
	            		{
	            			jmsgSmsTask.setStatus("1");
	            		}
	            		
	                    String res = jmsgSmsTaskService.createSmsTask(jmsgSmsTask, Arrays.asList(phones.split(",")), 0, payCount);
	                    if ("短信接收号码导入失败".equals(res))
	                    {
	                    	 map.put("code", "1");
		                     map.put("msg", "提交失败");
	                    }
	                    else
	                    {
	                    	map.put("code", "0");
	                    	map.put("msg", "提交成功");
	                        map.put("taskid", taskId);
	                    }
						
					}else{
						//map.putAll(MQSendSms(response, userId, smsContent, dataId, phoneList, sign, map,payCount,mq,sendDatetime)); //队列发送短信
						String taskId = new MsgId(1000001).toString();
	                	
	                	SmsMtMessage message = new SmsMtMessage();
	                    message.setSmsType("sms");
	                    message.setMsgContent(smsContent);
	                    message.setPhone( phones );
	                    message.setUserid( userId ); //用户ID
	                    message.setTaskid( taskId);
	                    message.setSpNumber(sendtermid);
	                    message.setUserReportGateWayID("HTTP");
	                    message.setUserReportNotify(StringUtils.isNotBlank(UserUtils.get(userId).getCallbackUrl())?"9":"0");
	                    message.setSendTime(SystemClock.now());
	                    
	                    // 提交到UMT
	                    String msgid = mQUtils.sendSmsMT("SMSUMTV1", "HTTP", message.getTaskid(), FstObjectSerializeUtil.write(message));
	                    logger.info("HttpApi-Submit-UMT = taskid:{}, msgid:{}, time:{}",  message.getTaskid(), msgid, (System.currentTimeMillis()-time));
	                    
	                    // 提交队列失败
	                    if( "-1".equals(msgid)) {
	                    	map.put("code", "1");
	                        map.put("msg", "提交失败");
	                        return result(response, map);
	                    }
					}
				}else{
					map.put("code", "-2");
					map.put("msg","phones参数不能为空");
					return result(response,map);
				}
				return result(response,map);
			//}
//			return result(map);
		}catch(Exception e){
			e.printStackTrace();
			map.put("code", "1");
			map.put("msg", "提交失败");
			return result(response,map);
		}
	}
	
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
	private Map<String,String> MQSendSms(HttpServletResponse response,String userId, String smsContent, String dataId, Set<String> phoneList,String sign,Map<String,String> map,int payCount,String mq,Date sendTime)
			throws Exception {
		String taskId = MsgIdUtits.msgId("T");//生成任务ID
		Map<String,String> sendMap = jmsgSmsTaskService.createSendDetail(dataId, taskId, userId, phoneList, smsContent, "API", mq,sign,payCount,sendTime);
		if("1".equals(sendMap.get("errorCode"))){
//			mQUtils.sendSmsMQ(taskId, "SMS_SINGLE_TASK_TOPIC", "single", taskId.getBytes());
		    logger.info("发送任务==>{}", taskId);
		    SmsSendExecutor send = new SmsSendExecutor(taskId);
            poolTaskExecutor.execute(send);
		}else{
			map.put("code", "1");
			map.put("msg", "提交失败,系统错误");
			return map;
		}
		map.put("taskid", taskId);
		return map;
	}
	
//	查询订单状态
	@RequestMapping(value = "sms/query")
	public String smsSendQuery(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	private String validateBalance(String userId,String apikey, HttpServletRequest request,HttpServletResponse response) {
		// 校验 userId
		if(StringUtils.isBlank(userId)){
			return resultBalance("-2", "userid参数不能为空",userId,"0",response);
		}
		// 校验 apikey
		if(StringUtils.isBlank(apikey)){
			return resultBalance("-2", "pwd参数不能为空",userId,"0",response);
		}
		
		//验证用户是否存在
		User user = UserUtils.get(userId);
		
		if(user == null){
			return resultBalance("4", "用户不存在",userId,"0",response);
		}else{
			String whiteIP = user.getWhiteIP();
			String ip = IPUtils.getIpAddr(request);
			
			if(StringUtils.isBlank(whiteIP) || (","+whiteIP+",").indexOf(","+ip+",") >= 0){//验证IP
				if(!apikey.equals(user.getApikey())){
					return resultBalance("-1", "用户密码错误",userId,"0",response);
				}
			}else{
				return resultBalance("7", "IP校验失败",userId,"0",response);
			}
		}
		
		return "0";
	}
	
//	查询用户余额
	@RequestMapping(value = "sms/balance")
	public String smsSendBalance(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter("userid");
		String apikey = request.getParameter("pwd");
		
		String result = validateBalance(userId,apikey,request,response);
		if("0".equals(result)){//验证成功
			
			//余额
            String amountKey = AccountCacheUtils.getAmountKey("amount", "sms", userId);
            String money = JedisClusterUtils.get(amountKey);
			return resultBalance("0", "查询成功", userId, money, response);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,String> validate(String userId,HttpServletRequest request){
		Map map = Maps.newHashMap();
		
		// 校验 userId
		if(StringUtils.isBlank(userId)){
			map.put("code", "-2");
			map.put("msg", "userid参数不能为空");
			return map;
		}
		
		String sendtermid = request.getParameter("sendtermid");//扩展号
//		if(StringUtils.isBlank(sendtermid)){
//			map.put("code", "-2");
//			map.put("msg", "sendtermid参数不能为空");
//			return map;
//		}else{
//			if(sendtermid.length() >4){
//				map.put("code", "9");
//				map.put("msg", "扩展号格式错误");
//				return map;
//			}
//		}
		
		if(StringUtils.isNotBlank(sendtermid)){
			if(sendtermid.length() >4){
				map.put("code", "9");
				map.put("msg", "扩展号格式错误");
				return map;
			}
		}
		
		String sendtime = request.getParameter("sendtime");//发送时间
		if(StringUtils.isBlank(sendtime)){
			map.put("code", "-2");
			map.put("msg", "sendtime参数不能为空");
			return map;
		}
		
		String phones = request.getParameter("phones");//号码
		if(StringUtils.isBlank(phones)){
			map.put("code", "-2");
			map.put("msg", "phones参数不能为空");
			return map;
		}
		
		String md5 = request.getParameter("md5");//签名
		if(StringUtils.isBlank(md5)){
			map.put("code", "-2");
			map.put("msg", "md5参数不能为空");
			return map;
		}
		
		
		String ip = IPUtils.getIpAddr(request);
		String smsContent = request.getParameter("content");
		if(StringUtils.isBlank(smsContent)){
			map.put("code", "-2");
			map.put("msg","content参数不能为空");
			return map;
		}
		
		

		User user = userDao.get(userId);
		if(user == null){//用户不存在
			map.put("code", "4");
			map.put("msg", "用户不存在");
		}else{
			
			if("1".equals(user.getFilterWordFlag())){//过滤敏感词
				String keywords = KeywordsUtils.keywords(smsContent.trim());
				if(StringUtils.isNotBlank(keywords)){
					map.put("code", "10");
					map.put("msg","发送内容包含敏感词["+keywords+"]");
					return map;
				}
			}
			
			if(!KeywordsUtils.exits(user.getKeyword(), smsContent)){//TODO 未包含用户关键字
				map.put("code", "15");
				map.put("msg","短信内容未包含用户关键字");
				return map;
			}
			
			
			String smsSign = SignUtils.get(smsContent);//短信签名
			
			String whiteIP = user.getWhiteIP();
			if(StringUtils.isBlank(whiteIP) || (","+whiteIP+",").indexOf(","+ip+",") >= 0){//验证IP
				String apikey = user.getApikey();
				String myMD5 = HttpRequest.md5(userId+"||"+phones+"||"+apikey);//MD5加密 (userid ||phones||password)
				if(md5.equals(myMD5)){//MD5验证
					map.put("code", "0");//验证通过
					map.put("msg", "提交成功");
					map.put("nocheck", user.getNoCheck());
					map.put("reviewCount",user.getReviewCount());
					map.put("sign", smsSign);
				}else{
					map.put("code", "6");
					map.put("msg", "MD5校验失败");
				}
			}else{
				map.put("code", "7");
				map.put("msg", "IP校验失败");
			}
		}
		
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	private String result(HttpServletResponse response,Map map){
		String code = StringUtils.valueof(map.get("code"));
		String msg = StringUtils.valueof(map.get("msg"));
		String taskid = StringUtils.valueof(map.get("taskid"));
		
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
							.append("<result statustext=\""+msg+"\" status=\""+code+"\">")
							.append("<taskid>"+taskid+"</taskid>")
							.append("</result>");
		return renderString(response, sb.toString(), "text/plain");

	}
	
	private String resultBalance(String code,String msg,String userid,String balance,HttpServletResponse response){
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
							.append("<result statustext=\""+msg+"\" status=\""+code+"\">")
							.append("<userid>"+userid+"</userid>")
							.append("<balance>"+balance+"</balance>")
							.append("</result>");
		return renderString(response, sb.toString(), "text/plain");
	}
	
	public static Date sendDatetime(String sendTime) {
        Date result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            result = sdf.parse(sendTime);
        } catch (Exception err) {
        	logger.debug("时间格式错误: "+ err.getMessage());
        }
        return result;
    }
	
	
}
