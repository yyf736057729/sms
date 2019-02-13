package com.sanerzone.smscenter.api.webservice;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanerzone.common.support.sequence.MsgId;
import com.sanerzone.common.support.utils.SystemClock;
import com.sanerzone.smscenter.common.tools.AccountCacheHelper;
import com.sanerzone.smscenter.common.tools.JedisClusterHelper;
import com.sanerzone.smscenter.common.tools.MQHelper;
import com.sanerzone.smscenter.common.tools.SignHelper;
import com.sanerzone.smscenter.common.tools.StringHelper;
import com.sanerzone.smscenter.config.SmsBatchSendInterface;
import com.siloyou.jmsg.common.message.SmsMtMessage;

@WebService  
public class SmsApiWebService {  
	
	public static Logger logger = LoggerFactory.getLogger(SmsApiWebService.class);
	
	@Autowired
    private MQHelper mQUtils;
	
    @Autowired
    private SmsBatchSendInterface smsBatchSendInterface;
     
	public String SendSMS(String accountNum,String accountPwd,String content,String phones,String smsLevel,String settime,String lastNum,String ip) {
    	long time = System.currentTimeMillis();
		
		String code = validate(accountNum, accountPwd, ip);
		if(!StringHelper.equals("0",code)){
			return code;
		}
        Date sendDatetime = null;
        if (StringHelper.isNotBlank(settime)){
            try {
				sendDatetime = DateUtils.parseDate(settime, "yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				logger.error("WS api,参数settime：{},时间格式错误：{}", settime, e.getMessage());
				return "-1";
			}
        }

		
		List<String> phoneList = StringHelper.queryTextPhone(phones, ",");//获取手机号码
		
		content = SignHelper.formatContent(content);
        
        String sign = SignHelper.get(content);//短信签名
        if (StringHelper.isBlank(sign)){
			sign = AccountCacheHelper.getStringValue(accountNum, "forceSign", "");	//强制签名
        	if(StringHelper.isNotBlank(sign)) {
        		content = "【"+sign+"】" + content;
			}
		}
        int payCount = StringHelper.findPayCount(content);
        int count = phoneList.size();
        if (count > 0){
        	//余额
            String amountKey = AccountCacheHelper.getAmountKey("amount", "sms", accountNum);
            // 扣款
            long amount = JedisClusterHelper.decrBy(amountKey, count * payCount);
            if (amount < 0){
            	JedisClusterHelper.incrBy(amountKey, count * payCount);
            	return "302";
            }
        	
            String nocheck = AccountCacheHelper.getStringValue(accountNum, "noCheck", "");
            int reviewCount = AccountCacheHelper.getIntegerValue(accountNum, "reviewCount", 0);
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
        		
                String res = smsBatchSendInterface.configSmsBatchSend(accountNum, taskId, content, count, status, sendDatetime, phoneList);
//        		String res = "0";
                if ("-1".equals(res)){
                	logger.error("WS api 提交失败");
                	return res;
                }else if("2".equals(res)){
                	return "301";
                }
            }else{
            	
            	String taskId = new MsgId(1000001).toString();
            	SmsMtMessage message = new SmsMtMessage();
                message.setSmsType("sms");
                message.setMsgContent(content);
                message.setPhone(phones);
                message.setUserid(accountNum); //用户ID
                message.setTaskid(taskId);
                message.setSpNumber(lastNum);
                message.setUserReportGateWayID("HTTP");
                message.setUserReportNotify(StringHelper.isNotBlank(AccountCacheHelper.getStringValue(accountNum, "callbackUrl", ""))?"9":"0");
                message.setSendTime(SystemClock.now());
                message.setCstmOrderID("");
                
                // 提交到UMT
                String msgid = mQUtils.sendSMSUMT("HTTP", message.getTaskid(), message);
                logger.info("HttpApi-Submit-UMT = taskid:{}, msgid:{}, time:{}",  message.getTaskid(), msgid, (System.currentTimeMillis()-time));
                
                // 提交队列失败
                if( "-1".equals(msgid)) {
                	return "-1";
                }
            }
        }
		return "0";
		
	}
	
	
    public static String validate(String accountNum, String accountPwd, String ip){
    	String userid = AccountCacheHelper.getStringValue(accountNum, "id", "");
    	if(StringHelper.isBlank(userid)){
    		return "303";//用户名或密码错误或平台对该应用没有开启
    	}
    	String apikey = AccountCacheHelper.getStringValue(accountNum, "apikey", "");
    	if(StringHelper.isBlank(apikey) || (!StringHelper.equals(apikey, accountPwd))){
    		return "303";
    	}
    	//接入应用所使用的IP地址非法。
    	String whiteIP = AccountCacheHelper.getStringValue(accountNum, "whiteIP", "");
		if (!(StringHelper.isBlank(whiteIP) || ("," + whiteIP + ",").indexOf("," + ip + ",") >= 0)){
    		return "306";//接入应用所使用的IP地址非法。
    	}
    	return "0";
    }

}  