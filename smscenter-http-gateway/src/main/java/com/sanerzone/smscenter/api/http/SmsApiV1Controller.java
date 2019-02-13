package com.sanerzone.smscenter.api.http;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.sanerzone.common.support.sequence.MsgId;
import com.sanerzone.common.support.utils.SystemClock;
import com.sanerzone.smscenter.common.tools.AccountCacheHelper;
import com.sanerzone.smscenter.common.tools.Base64Helper;
import com.sanerzone.smscenter.common.tools.HttpRequest;
import com.sanerzone.smscenter.common.tools.IPHelper;
import com.sanerzone.smscenter.common.tools.JedisClusterHelper;
import com.sanerzone.smscenter.common.tools.MQHelper;
import com.sanerzone.smscenter.common.tools.SignHelper;
import com.sanerzone.smscenter.common.tools.StreamHelper;
import com.sanerzone.smscenter.common.tools.StringHelper;
import com.sanerzone.smscenter.common.web.BaseController;
import com.sanerzone.smscenter.config.SmsBatchSendInterface;
import com.siloyou.jmsg.common.message.SmsMtMessage;


@Controller
@RequestMapping(value = "")
public class SmsApiV1Controller extends BaseController {
	
	public static Logger logger = LoggerFactory.getLogger(SmsApiV1Controller.class);
	
	@Autowired
	private MQHelper mQUtils;
	
    @Autowired
    private SmsBatchSendInterface smsBatchSendInterface;
	
	
	@SuppressWarnings("rawtypes")
	public Map getPostDataMap(HttpServletRequest request,HttpServletResponse response){
		try{
			String charEncoding = request.getCharacterEncoding();
			if (charEncoding == null) {
				charEncoding = "UTF-8";
			}
			String respText = StreamHelper.InputStreamTOString(request.getInputStream(), charEncoding);
			if( StringHelper.isNotBlank(respText)) {
				String jsonString = new String(Base64Helper.decode(respText));
				return (Map)JSON.parseObject(jsonString, Map.class);
			}
		}catch(IOException e) {
			
		}
		return null;
	}
	
	@RequestMapping(value = "sismsapi.go")
	public String select (HttpServletRequest request, HttpServletResponse response) {
		logger.info("sms api v1: {}", JSON.toJSONString(request.getParameterMap()));
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
			int speed = AccountCacheHelper.getIntegerValue(userId, "httpSpeed", -1);
            if(speed == -1){
            	 map.put("code", "4");
                 map.put("msg", "用户不存在");
            	 return result(response, map);
            }
            if(speed > 0){
            	RateLimiter limiter = AccountCacheHelper.getHttpSpeed(userId);
            	limiter.acquire();
            }
			String sendtime = request.getParameter("sendtime");
			map = validate(userId, request);

			if(!StringHelper.equals(String.valueOf(map.get("code")),"0")){//验证不通过
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
			int reviewCount = Integer.valueOf(StringHelper.valueof(map.get("reviewCount")));
			map.remove("nocheck");
			map.remove("sign");
			map.remove("reviewCount");
			
			String phones = request.getParameter("phones");
			String smsContent = StringEscapeUtils.unescapeHtml4(request.getParameter("content"));
			
			if(phones.length() > 120000) {//一次性发送10000个 TODO
				map.put("code", "14");
				map.put("msg","号码个数超过限制");
				return result(response,map);
			}
			smsContent = smsContent.trim();
			
			smsContent = SignHelper.formatContent(smsContent);
            if (StringHelper.isBlank(sign)){
            	sign = AccountCacheHelper.getStringValue(userId, "forceSign", "");	//强制签名
            	if(StringHelper.isNotBlank(sign)) {
            		smsContent = "【"+sign+"】" + smsContent;
            	}
			}
			
			
            List<String> phoneList = StringHelper.queryTextPhone(phones, ",");//获取手机号码
            int payCount = StringHelper.findPayCount(smsContent);//获取扣费条数
			
			int count = phoneList.size();
			if(count > 0){
				//余额
                String amountKey = AccountCacheHelper.getAmountKey("amount", "sms", userId);
                // 扣款
                long amount = JedisClusterHelper.decrBy(amountKey, count * payCount);
                if (amount < 0){
                	JedisClusterHelper.incrBy(amountKey, count * payCount);
                	map.put("code", "3");
                    map.put("msg", "余额不足");
                    return result(response, map);
                }
            	
                String sendtermid = StringHelper.stripToEmpty(request.getParameter("extnum"));//扩展号
                
				if("2".equals(nocheck)){//自动审核
					if(reviewCount >= count){
						nocheck="1";//免审
					}else{
						nocheck="0";//必审
					}
				}
				
				if("0".equals(nocheck) || phoneList.size() > 50 || sendDatetime.compareTo(new Date()) > 0){
					
					String taskId = new MsgId(1000002).toString();
                    String status = "1";	
            		if ("0".equals(nocheck)){
            			status="-1";
            		}
            		
                    String res = smsBatchSendInterface.configSmsBatchSend(userId, taskId, smsContent, count, status, sendDatetime, phoneList);
                    if ("-1".equals(res)){
                    	 map.put("code", "1");
                         map.put("msg", "提交失败");
                    }else if("2".equals(res)){
            			map.put("code", "2");
                        map.put("msg", "短信内容匹配规则失败");
                        return result(response, map);
                    }else{
                    	map.put("code", "0");
                    	map.put("msg", "提交成功");
                        map.put("taskid", taskId);
                    }
				}else{
					String taskId = new MsgId(1000001).toString();
                	
					SmsMtMessage message = new SmsMtMessage();
                    message.setSmsType("sms");
                    message.setMsgContent(smsContent);
                    message.setPhone( phones );
                    message.setUserid( userId ); //用户ID
                    message.setTaskid( taskId);
                    message.setSpNumber(sendtermid);
                    message.setUserReportGateWayID("HTTP");
                    message.setUserReportNotify(StringHelper.isNotBlank(AccountCacheHelper.getStringValue(userId, "callbackUrl", ""))?"9":"0");
                    message.setSendTime(SystemClock.now());
                    
                    // 提交到UMT
                    String msgid = mQUtils.sendSMSUMT("HTTP", message.getTaskid(), message);
                    logger.info("HttpApi-Submit-UMT = taskid:{}, msgid:{}, time:{}",  message.getTaskid(), msgid, (System.currentTimeMillis()-time));
                    
                    // 提交队列失败
                    if( "-1".equals(msgid)) {
                    	map.put("code", "1");
                        map.put("msg", "提交失败");
                        return result(response, map);
                    }else{
                    	map.put("taskid", message.getTaskid());
                    }
				}
			}else{
				map.put("code", "-2");
				map.put("msg","phones参数不能为空");
				return result(response,map);
			}
			return result(response,map);
		}catch(Exception e){
			e.printStackTrace();
			map.put("code", "1");
			map.put("msg", "提交失败");
			return result(response,map);
		}
	}
	
	
//	查询订单状态
	@RequestMapping(value = "sms/query")
	public String smsSendQuery(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	private String validateBalance(String userId,String apikey, HttpServletRequest request,HttpServletResponse response) {
		// 校验 userId
		if(StringHelper.isBlank(userId)){
			return resultBalance("-2", "userid参数不能为空",userId,"0",response);
		}
		// 校验 apikey
		if(StringHelper.isBlank(apikey)){
			return resultBalance("-2", "pwd参数不能为空",userId,"0",response);
		}
		
		
		//验证用户是否存在
        String curUserId = AccountCacheHelper.getStringValue(userId, "id", "");
        
        if (StringHelper.isBlank(curUserId)){
            return resultBalance("4", "用户不存在", userId, "0", response);
		}else{
			int usedFlag = AccountCacheHelper.getIntegerValue(userId, "usedFlag", 1);
			if(usedFlag == 0) {  //账户禁用发送功能
				return resultBalance("4", "用户不存在", userId, "", response);
			}
			String whiteIP = AccountCacheHelper.getStringValue(userId, "whiteIP", "");
			String ip = IPHelper.getIpAddr(request);
			
			if(StringHelper.isBlank(whiteIP) || (","+whiteIP+",").indexOf(","+ip+",") >= 0){//验证IP
				if(!apikey.equals(AccountCacheHelper.getStringValue(userId, "apikey", ""))){
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
            String amountKey = AccountCacheHelper.getAmountKey("amount", "sms", userId);
            String money = JedisClusterHelper.get(amountKey);
			return resultBalance("0", "查询成功", userId, money, response);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,String> validate(String userId,HttpServletRequest request){
		Map map = Maps.newHashMap();
		
		// 校验 userId
		if(StringHelper.isBlank(userId)){
			map.put("code", "-2");
			map.put("msg", "userid参数不能为空");
			return map;
		}
		
		String sendtermid = request.getParameter("sendtermid");//扩展号
		
		if(StringHelper.isNotBlank(sendtermid)){
			if(sendtermid.length() >4){
				map.put("code", "9");
				map.put("msg", "扩展号格式错误");
				return map;
			}
		}
		
		String sendtime = request.getParameter("sendtime");//发送时间
		if(StringHelper.isBlank(sendtime)){
			map.put("code", "-2");
			map.put("msg", "sendtime参数不能为空");
			return map;
		}
		
		String phones = request.getParameter("phones");//号码
		if(StringHelper.isBlank(phones)){
			map.put("code", "-2");
			map.put("msg", "phones参数不能为空");
			return map;
		}
		
		String md5 = request.getParameter("md5");//签名
		if(StringHelper.isBlank(md5)){
			map.put("code", "-2");
			map.put("msg", "md5参数不能为空");
			return map;
		}
		
		
		String ip = IPHelper.getIpAddr(request);
		String smsContent = request.getParameter("content");
		if(StringHelper.isBlank(smsContent)){
			map.put("code", "-2");
			map.put("msg","content参数不能为空");
			return map;
		}
		
		

		//验证用户是否存在
        String curUserId = AccountCacheHelper.getStringValue(userId, "id", "");
		if(StringHelper.isBlank(curUserId)){//用户不存在
			map.put("code", "4");
			map.put("msg", "用户不存在");
			return map;
		}else{
			
			int usedFlag = AccountCacheHelper.getIntegerValue(userId, "usedFlag", 1);
			if(usedFlag == 0) {  //账户禁用发送功能
				map.put("code", "4");
				map.put("msg", "用户不存在");
				return map;
			}
			
			//TODO 过滤敏感词
			/**
			if("1".equals(user.getFilterWordFlag())){//过滤敏感词
				String keywords = KeywordsHelper.keywords(smsContent.trim());
				if(StringHelper.isNotBlank(keywords)){
					map.put("code", "10");
					map.put("msg","发送内容包含敏感词["+keywords+"]");
					return map;
				}
			}
			
			if(!KeywordsHelper.exits(user.getKeyword(), smsContent)){//TODO 未包含用户关键字
				map.put("code", "15");
				map.put("msg","短信内容未包含用户关键字");
				return map;
			}
			**/
			
			
			String smsSign = SignHelper.get(smsContent);//短信签名
			
			String whiteIP = AccountCacheHelper.getStringValue(userId, "whiteIP", "");
			if(StringHelper.isBlank(whiteIP) || (","+whiteIP+",").indexOf(","+ip+",") >= 0){//验证IP
				String apikey =  AccountCacheHelper.getStringValue(userId, "apikey", "");
				String myMD5 = HttpRequest.md5(userId+"||"+phones+"||"+apikey);//MD5加密 (userid ||phones||password)
				if(md5.equals(myMD5)){//MD5验证
					map.put("code", "0");//验证通过
					map.put("msg", "提交成功");
					map.put("nocheck", AccountCacheHelper.getStringValue(userId, "noCheck", ""));
					map.put("reviewCount",AccountCacheHelper.getStringValue(userId, "reviewCount", "0"));
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
		String code = StringHelper.valueof(map.get("code"));
		String msg = StringHelper.valueof(map.get("msg"));
		String taskid = StringHelper.valueof(map.get("taskid"));
		
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
