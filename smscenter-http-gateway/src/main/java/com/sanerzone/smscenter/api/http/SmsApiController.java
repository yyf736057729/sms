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
import com.sanerzone.smscenter.api.entity.ApiResultEntity;
import com.sanerzone.smscenter.common.tools.AccountCacheHelper;
import com.sanerzone.smscenter.common.tools.Base64Helper;
import com.sanerzone.smscenter.common.tools.HttpRequest;
import com.sanerzone.smscenter.common.tools.IPHelper;
import com.sanerzone.smscenter.common.tools.JedisClusterHelper;
import com.sanerzone.smscenter.common.tools.MQHelper;
import com.sanerzone.smscenter.common.tools.SignHelper;
import com.sanerzone.smscenter.common.tools.StreamHelper;
import com.sanerzone.smscenter.common.tools.StringHelper;
import com.sanerzone.smscenter.common.tools.ValidateHelper;
import com.sanerzone.smscenter.common.web.BaseController;
import com.sanerzone.smscenter.config.SmsBatchSendInterface;
import com.siloyou.jmsg.common.message.SmsMtMessage;

@Controller
@RequestMapping(value = "/api")
public class SmsApiController extends BaseController{
    
    public static Logger logger = LoggerFactory.getLogger(SmsApiController.class);
    
    @Autowired
    private MQHelper mQUtils;
    
    @Autowired
    private SmsBatchSendInterface smsBatchSendInterface;
    
    @SuppressWarnings("rawtypes")
    public Map getPostDataMap(HttpServletRequest request, HttpServletResponse response){
        try{
            String charEncoding = request.getCharacterEncoding();
            if (charEncoding == null){
                charEncoding = "UTF-8";
            }
            String respText = StreamHelper.InputStreamTOString(request.getInputStream(), charEncoding);
            if(StringHelper.isNotBlank(respText)){
                String jsonString = new String(Base64Helper.decode(respText));
                return (Map) JSON.parseObject(jsonString, Map.class);
            }
        }catch (IOException e){
            
        }
        return null;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "sms/send")
    public String smsSend(HttpServletRequest request, HttpServletResponse response)
    {
    	logger.info("sms api v2: {}", JSON.toJSONString(request.getParameterMap()));
    	
    	long time = System.currentTimeMillis();
    	
        Map map = Maps.newHashMap();
        try
        {
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
            
            String sendtime = request.getParameter("time");
            map = ValidateHelper.validate(userId, request);
            
            if (!StringHelper.equals(String.valueOf(map.get("code")), "0")){//验证不通过
                return result(response, map);
            }
            
            Date sendDatetime = null;
            if (StringHelper.isNotBlank(sendtime)){
                sendDatetime = sendDatetime(sendtime);
                if (sendDatetime == null){
                    map.put("code", "8");
                    map.put("msg", "时间格式错误");
                    return result(response, map);
                }
            }
            
            //校验手机号码
            String phones = request.getParameter("mobile");
            if (phones.length() > 12000){//单次最多 1000个号码个 TODO
                map.put("code", "14");
                map.put("msg", "号码个数超过限制");
                return result(response, map);
            }
            
            //校验短信内容
            String smsContent = StringEscapeUtils.unescapeHtml4(request.getParameter("msgcontent"));
            smsContent = smsContent.trim();
            smsContent = SignHelper.formatContent(smsContent);
            
            String sign = SignHelper.get(smsContent);//短信签名
            if (StringHelper.isBlank(sign)){
				sign = AccountCacheHelper.getStringValue(userId, "forceSign", "");	//强制签名
            	if(StringHelper.isNotBlank(sign)) {
					smsContent = "【"+sign+"】" + smsContent;
				}
			}
            
            if(smsContent.length() > 600) {
            	map.put("code", "11");
                map.put("msg", "短信内容过长");
                return result(response, map);
            }
            
            List<String> phoneList = StringHelper.queryTextPhone(phones, ",");//获取手机号码
            int payCount = StringHelper.findPayCount(smsContent);
            int count = phoneList.size();
            if (count > 0){
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
            	
                String nocheck = AccountCacheHelper.getStringValue(userId, "noCheck", "");
                int reviewCount = AccountCacheHelper.getIntegerValue(userId, "reviewCount", 0);
                if ("2".equals(nocheck)){//自动审核
                    if (reviewCount >= count){
                        nocheck = "1";//免审
                    }else{
                        nocheck = "0";//必审
                    }
                }
                
                if ("0".equals(nocheck) || phoneList.size() > 50 || sendDatetime != null){
                	
                	if(sendDatetime == null) {
                		sendDatetime = new Date();
                	}
                	
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
                	String sendtermid = StringHelper.stripToEmpty(request.getParameter("extnum"));//扩展号
                	
                	String taskId = new MsgId(1000001).toString();
                	SmsMtMessage message = new SmsMtMessage();
                    message.setSmsType("sms");
                    message.setMsgContent(smsContent);
                    message.setPhone( phones );
                    message.setUserid( userId ); //用户ID
                    message.setTaskid( taskId);
                    message.setSpNumber( sendtermid );
                    message.setUserReportGateWayID("HTTP");
                    message.setUserReportNotify(StringHelper.isNotBlank(AccountCacheHelper.getStringValue(userId, "callbackUrl", ""))?"9":"0");
                    message.setSendTime(SystemClock.now());
                    message.setCstmOrderID(request.getParameter("messageid"));
                    
                    // 提交到UMT
                    String msgid = mQUtils.sendSMSUMT("HTTP", message.getTaskid(), message);
                    logger.info("HttpApi-Submit-UMT = taskid:{}, msgid:{}, time:{}",  message.getTaskid(), msgid, (System.currentTimeMillis()-time));
                    
                    // 提交队列失败
                    if( "-1".equals(msgid)) {
                    	map.put("code", "1");
                        map.put("msg", "提交失败");
                        return result(response, map);
                    }
                    	
                    // 提交队列成功
                    map.put("taskid", message.getTaskid());
                }
            }
            else
            {
                map.put("code", "2");
                map.put("msg", "phones参数不能为空");
                return result(response, map);
            }
            return result(response, map);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            map.put("code", "1");
            map.put("msg", "提交失败");
            return result(response, map);
        }
    }
    
    
    //	查询订单状态
    @RequestMapping(value = "sms/query")
    public String smsSendQuery(HttpServletRequest request, HttpServletResponse response){
        return null;
    }
    
    private String validateBalance(String userId, String sign, HttpServletRequest request, HttpServletResponse response){
        // 校验 userId
        if (StringHelper.isBlank(userId)){
            return resultBalance("2", "userid参数不能为空", userId, "0", response);
        }
        // 校验 apikey
        if (StringHelper.isBlank(sign)){
            return resultBalance("2", "sign参数不能为空", userId, "0", response);
        }
        
        String ts = request.getParameter("ts");
        if (StringHelper.isBlank(ts)){
            return resultBalance("2", "ts参数不能为空", userId, "0", response);
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
            
            if (StringHelper.isBlank(whiteIP) || ("," + whiteIP + ",").indexOf("," + ip + ",") >= 0){//验证IP
            	String apikey = AccountCacheHelper.getStringValue(userId, "apikey", "");
                String myMD5 = HttpRequest.md5(userId + ts + apikey);//MD5加密 md5(userid + ts + apikey)
                if (!sign.equals(myMD5)){//MD5验证
                    return resultBalance("6", "MD5校验失败", userId, "0", response);
                }
            }else{
                return resultBalance("7", "IP校验失败", userId, "0", response);
            }
        }
        
        return "0";
    }
    
    //	查询用户余额
    @RequestMapping(value = "sms/balance")
    public String smsSendBalance(HttpServletRequest request, HttpServletResponse response){
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");
        
        String result = validateBalance(userId, sign, request, response);
        if ("0".equals(result)){//验证成功
        	//余额
            String amountKey = AccountCacheHelper.getAmountKey("amount", "sms", userId);
            String money = JedisClusterHelper.get(amountKey);
            return resultBalance("0", "查询成功", userId, money, response);
        }else{
            return result;
        }
    }
    
    // 获取敏感词
    @RequestMapping(value = "sms/keyword")
    public String keyword(HttpServletRequest request, HttpServletResponse response){
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");
        
        String result = validateKeyword(userId, sign, request, response);
        if ("0".equals(result)){//验证成功
            String smsContent = request.getParameter("msgcontent");
            if (StringHelper.isBlank(smsContent)){
                return resultKeyword("2", "msgcontent参数不能为空", userId, "", response);
            }
            
//            String keywords = KeywordsUtils.keywords(smsContent.trim());
//            if (StringHelper.isNotBlank(keywords))
//            {
//                return resultKeyword("10", "发送内容包含敏感词[" + keywords + "]", userId, keywords, response);
//            }
            
            return resultKeyword("0", "查询成功", userId, "", response);//TODO 匹配敏感词 没有实现
        }else{
            return result;
        }
    }
    
    private String validateKeyword(String userId, String sign, HttpServletRequest request, HttpServletResponse response){
        // 校验 userId
        if (StringHelper.isBlank(userId)){
            return resultKeyword("2", "userid参数不能为空", userId, "", response);
        }
        // 校验 apikey
        if (StringHelper.isBlank(sign)){
            return resultKeyword("2", "sign参数不能为空", userId, "", response);
        }
        
        String ts = request.getParameter("ts");
        if (StringHelper.isBlank(ts)){
            return resultKeyword("2", "ts参数不能为空", userId, "", response);
        }
        
        //验证用户是否存在
        String curUserId = AccountCacheHelper.getStringValue(userId, "id", "");
        
        if (StringHelper.isBlank(curUserId)){
            return resultKeyword("4", "用户不存在", userId, "", response);
        }else{
        	int usedFlag = AccountCacheHelper.getIntegerValue(userId, "usedFlag", 1);
			if(usedFlag == 0) {  //账户禁用发送功能
				return resultKeyword("4", "用户不存在", userId, "", response);
			}
        	
            String whiteIP = AccountCacheHelper.getStringValue(userId, "whiteIP", "");
            String ip = IPHelper.getIpAddr(request);
            
            if(StringHelper.isBlank(whiteIP) || ("," + whiteIP + ",").indexOf("," + ip + ",") >= 0){//验证IP
                String apikey = AccountCacheHelper.getStringValue(userId, "apikey", "");
                String myMD5 = HttpRequest.md5(userId + ts + apikey);//MD5加密 md5(userid + ts + apikey)
                if (!sign.equals(myMD5)){//MD5验证
                    return resultKeyword("6", "MD5校验失败", userId, "", response);
                }
            }else{
                return resultKeyword("7", "IP校验失败", userId, "", response);
            }
        }
        
        return "0";
    }
    
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String result(HttpServletResponse response, Map map){
        String code = StringHelper.valueof(map.get("code"));
        String msg = StringHelper.valueof(map.get("msg"));
        String taskid = StringHelper.valueof(map.get("taskid"));
        
        
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        if (StringHelper.equals(code, "0")){
            Map<String, String> tmp = Maps.newHashMap();
            tmp.put("taskid", taskid);
            entity.setData(tmp);
        }
        
        return renderString(response, entity);
        
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resultBalance(String code, String msg, String userid, String balance, HttpServletResponse response){
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        Map<String, String> map = Maps.newHashMap();
        map.put("balance", balance);
        entity.setData(map);
        return renderString(response, entity);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resultKeyword(String code, String msg, String userid, String keywords, HttpServletResponse response){
        
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        if (StringHelper.isNotBlank(keywords)){
            Map<String, String> map = Maps.newHashMap();
            map.put("keywords", keywords);
            entity.setData(map);
        }
        
        return renderString(response, entity);
    }
    
    public static Date sendDatetime(String sendTime){
        Date result = null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            result = sdf.parse(sendTime);
        }catch (Exception err){
            logger.debug("时间格式错误: " + err.getMessage());
        }
        return result;
    }
    
    @RequestMapping(value = "sms/report")
    public String reportTest(HttpServletRequest request, HttpServletResponse response){
    	@SuppressWarnings("rawtypes")
		Map map = getPostDataMap(request, response);
    	System.out.println("=========================>"+JSON.toJSONString(map));
    	return renderString(response, "ok", "application/json");
    }
    
    
}
