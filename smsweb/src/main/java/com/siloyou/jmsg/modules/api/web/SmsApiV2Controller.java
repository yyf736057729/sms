package com.siloyou.jmsg.modules.api.web;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aliyun.openservices.ons.api.Consumer;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.IPUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.mq.MQCustomerFactory;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.modules.api.entity.ApiResultEntity;

@Controller
@RequestMapping(value = "/api")
public class SmsApiV2Controller extends BaseController
{
    
    public static Logger logger = LoggerFactory.getLogger(SmsApiV2Controller.class);


    //	短信上行查询接口（Query）
    @RequestMapping(value = "v2/sms/moquery")
    public String upstreamQuery(HttpServletRequest request, HttpServletResponse response)
    {
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");

        String result = validate(userId, sign, request, response);
        if ("0".equals(result))
        {//验证成功
            User user = UserUtils.get(userId);
            Map<String, String> resultMap = Maps.newHashMap();
            boolean flag = "0".equals(user.getPayMode())?true:false;
            List<Map<String,String>> rList = Lists.newArrayList();

            int idx = 0;
            do {
                idx ++;
                Serializable mqBody = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSMOUERY_"+ userId).poll();
                if(mqBody == null) break;
                try {
                    SmsMoMessage smsMoMessage = (SmsMoMessage) FstObjectSerializeUtil.read((byte[])mqBody);
                    resultMap = new HashMap<String, String>();
                    resultMap.put("srcid",smsMoMessage.getSrcTermID());
                    resultMap.put("mobile",smsMoMessage.getDestTermID());
                    resultMap.put("msgContent",smsMoMessage.getMsgContent());
                    rList.add(resultMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while(idx < 200);

            return resultReport("0", "查询成功", rList, response);
        }
        else
        {
            return result;
        }
    }

    //	短信状态报告查询接口（Query）
    @RequestMapping(value = "v2/sms/query")
    public String smsSendBalance(HttpServletRequest request, HttpServletResponse response)
    {
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");
        
        String result = validate(userId, sign, request, response);
        if ("0".equals(result))
        {//验证成功
        	
        	User user = UserUtils.get(userId);
        	Map<String, String> resultMap = Maps.newHashMap();
        	boolean flag = "0".equals(user.getPayMode())?true:false; 
        	List<Map<String,String>> rList = Lists.newArrayList();
        	
        	int idx = 0;
        	do {
        		idx ++;
        		Serializable mqBody = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSQUERY_"+ userId).poll();
        		if(mqBody == null) break;
        		
        		try {
                    SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read((byte[])mqBody);
                    System.out.println(smsRtMessage);
                    String status = smsRtMessage.getStat();
                    if(flag){//提交扣费
                        if(!status.startsWith("F")){
                            status = "DELIVRD";
                        }
                    }

                    resultMap = new HashMap<String, String>();
                    resultMap.put("taskid", smsRtMessage.getSmsMt().getTaskid());
                    resultMap.put("code", status);
                    resultMap.put("msg", smsRtMessage.getSmsMt().getMsgContent());
                    resultMap.put("mobile", smsRtMessage.getDestTermID());
                    resultMap.put("time", formatDateStr(smsRtMessage.getDoneTime()));

                    rList.add(resultMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	} while(idx < 200);
            return resultReport(rList.size()+"", "查询成功", rList, response);
        }
        else
        {
            return result;
        }
    }
    
    private String validate(String userId, String sign, HttpServletRequest request, HttpServletResponse response)
    {
        // 校验 userId
        if (StringUtils.isBlank(userId))
        {
            return resultReport("2", "userid参数不能为空", null, response);
        }
        // 校验 apikey
        if (StringUtils.isBlank(sign))
        {
            return resultReport("2", "sign参数不能为空", null, response);
        }
        
        String ts = request.getParameter("ts");
        if (StringUtils.isBlank(ts))
        {
            return resultReport("2", "ts参数不能为空", null, response);
        }
        
        //验证用户是否存在
        User user = UserUtils.get(userId);
        
        if (user == null)
        {
            return resultReport("4", "用户不存在", null, response);
        }
        else
        {
            String whiteIP = user.getWhiteIP();
            String ip = IPUtils.getIpAddr(request);
            
            if (StringUtils.isBlank(whiteIP) || ("," + whiteIP + ",").indexOf("," + ip + ",") >= 0)
            {//验证IP
                String apikey = user.getApikey();
                String myMD5 = HttpRequest.md5(userId + ts + apikey);//MD5加密 md5(userid + ts + apikey)
                if (!sign.equals(myMD5))
                {//MD5验证
                    return resultReport("6", "MD5校验失败", null, response);
                }
            }
            else
            {
                return resultReport("7", "IP校验失败", null, response);
            }
        }
        
        return "0";
    }
    
    private String formatDateStr(String dateStr)
	{
	    if (StringUtils.isBlank(dateStr))
	    {
	        return dateStr;
	    }
	    
	    SimpleDateFormat sdf10 = new SimpleDateFormat("yyMMddHHmm");
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");
        //SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try
        {
            if (dateStr.length() == 10)
            {
                date = sdf10.parse(dateStr);
            }
            else
            {
                //date = sdf14.parse(dateStr);
                return dateStr;
            }
            
            return sdf14.format(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        
        return dateStr;
	}
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resultReport(String code, String msg, List<Map<String,String>> report, HttpServletResponse response)
    {
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        if(report != null && report.size() > 0)
        	entity.setData(report);
        return renderString(response, entity);
    }
    
    
    @Autowired
    private SmsRTCacheListener smsRTCacheListener;
    
    @PostConstruct
	private void smsRTCacheConsumer() {
        Consumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("SMSRTCACHEConsumerGroup");
		try {
//			smsStatusConsumer.setInstanceName("SMSRTCACHEConsumer" + UUID.randomUUID().toString());
			smsStatusConsumer.subscribe(CacheKeys.getPushTopic(), "*",smsRTCacheListener);//"SMSRTV1"
//			smsStatusConsumer.setMessageListener(smsRTCacheListener);
			smsStatusConsumer.start();
			logger.info("用户主动查询状态处理程序已启动");
		} catch (Exception e) {
			logger.error("用户主动查询状态处理程序异常", e);
		}
	}
    @Autowired
    private SmsMOCacheListener smsMOCacheListener;

    @PostConstruct
    private void smsRTCacheConsumer1() {
        Consumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("SMSMOCACHEConsumerGroup2");
        try {
//            smsStatusConsumer.setInstanceName("SMSMOCACHEConsumer" + UUID.randomUUID().toString());
            smsStatusConsumer.subscribe(CacheKeys.getMOTopic(), "*",smsMOCacheListener);//上行短信  //SMSMOV1
//            smsStatusConsumer.setMessageListener(smsMOCacheListener);
            smsStatusConsumer.start();
            logger.info("上行短信主动查询程序已启动");
        } catch (Exception e) {
            logger.error("上行短信主动查询理程序异常", e);
        }
    }
}
