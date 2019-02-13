package com.sanerzone.smscenter.api.http;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.smscenter.api.entity.ApiResultEntity;
import com.sanerzone.smscenter.common.tools.AccountCacheHelper;
import com.sanerzone.smscenter.common.tools.HttpRequest;
import com.sanerzone.smscenter.common.tools.IPHelper;
import com.sanerzone.smscenter.common.tools.StringHelper;
import com.sanerzone.smscenter.common.web.BaseController;
import com.siloyou.jmsg.common.message.SmsRtMessage;

@Controller
@RequestMapping(value = "/api")
public class SmsApiV2Controller extends BaseController{
    
    public static Logger logger = LoggerFactory.getLogger(SmsApiV2Controller.class);
    
    //查询用户状态报告
    @RequestMapping(value = "v2/sms/query")
    public String smsSendBalance(HttpServletRequest request, HttpServletResponse response){
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");
        
        String result = validate(userId, sign, request, response);
        if ("0".equals(result)){//验证成功
        	Map<String, String> resultMap = Maps.newHashMap();
        	boolean flag = "0".equals(AccountCacheHelper.getStringValue(userId, "payMode", ""))?true:false; 
        	List<Map<String,String>> rList = Lists.newArrayList();
        	
        	int idx = 0;
        	do {
        		idx ++;
        		Serializable mqBody = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSQUERY_"+ userId).poll();
        		if(mqBody == null) break;
        		
        		try {
					SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read((byte[])mqBody);
					
	                String status = smsRtMessage.getStat();
	                if(flag){//提交扣费
	                    if(!status.startsWith("F")){
	                        status = "DELIVRD";
	                    }
	                }
	                
	                resultMap = new HashMap<String, String>();
	                resultMap.put("taskid", smsRtMessage.getSmsMt().getTaskid());
	                resultMap.put("code", status);
	                resultMap.put("msg", "");
	                resultMap.put("mobile", smsRtMessage.getDestTermID());
	                resultMap.put("time", formatDateStr(smsRtMessage.getDoneTime()));
	                
	                rList.add(resultMap);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
        		
        	} while(idx <= 200);
        	
            return resultReport("0", "查询成功", rList, response);
        }else{
            return result;
        }
    }
    
    private String validate(String userId, String sign, HttpServletRequest request, HttpServletResponse response){
        // 校验 userId
        if (StringHelper.isBlank(userId)){
            return resultReport("2", "userid参数不能为空", null, response);
        }
        // 校验 apikey
        if (StringHelper.isBlank(sign)){
            return resultReport("2", "sign参数不能为空", null, response);
        }
        
        String ts = request.getParameter("ts");
        if (StringHelper.isBlank(ts)){
            return resultReport("2", "ts参数不能为空", null, response);
        }
        
        //验证用户是否存在
        String curUserId = AccountCacheHelper.getStringValue(userId, "id", "");
        
        if (StringHelper.isBlank(curUserId)){
            return resultReport("4", "用户不存在", null, response);
        }else{
        	int usedFlag = AccountCacheHelper.getIntegerValue(userId, "usedFlag", 1);
			if(usedFlag == 0) {  //账户禁用发送功能
				return resultReport("4", "用户不存在", null, response);
			}
        	
            String whiteIP = AccountCacheHelper.getStringValue(userId, "whiteIP", "");
            String ip = IPHelper.getIpAddr(request);
            
            if (StringHelper.isBlank(whiteIP) || ("," + whiteIP + ",").indexOf("," + ip + ",") >= 0){//验证IP
                String apikey = AccountCacheHelper.getStringValue(userId, "apikey", "");
                String myMD5 = HttpRequest.md5(userId + ts + apikey);//MD5加密 md5(userid + ts + apikey)
                if(!sign.equals(myMD5)){//MD5验证
                    return resultReport("6", "MD5校验失败", null, response);
                }
            }else{
                return resultReport("7", "IP校验失败", null, response);
            }
        }
        
        return "0";
    }
    
    private String formatDateStr(String dateStr){
	    if (StringHelper.isBlank(dateStr)){
	        return dateStr;
	    }
	    
	    SimpleDateFormat sdf10 = new SimpleDateFormat("yyMMddHHmm");
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try{
            if (dateStr.length() == 10){
               date = sdf10.parse(dateStr);
            }else{
               return dateStr;
            }
            return sdf14.format(date);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return dateStr;
	}
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resultReport(String code, String msg, List<Map<String,String>> report, HttpServletResponse response){
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        if(report != null && report.size() > 0)
        	entity.setData(report);
        return renderString(response, entity);
    }
    
    
    /**
    @Autowired
    private SmsRTCacheListener smsRTCacheListener;
    
    @PostConstruct
	private void smsRTCacheConsumer() {
		DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("SMSRTCACHEConsumerGroup");
		try {
			smsStatusConsumer.setInstanceName("SMSRTCACHEConsumer");
			smsStatusConsumer.subscribe(CacheKeys.getReportTopic(), "*");
			smsStatusConsumer.setMessageListener(smsRTCacheListener);
			smsStatusConsumer.start();
			logger.info("用户主动查询状态处理程序已启动");
		} catch (MQClientException e) {
			logger.error("用户主动查询状态处理程序异常", e);
		}
	}
	**/
}
