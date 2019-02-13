package com.siloyou.jmsg.modules.swsms.api;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.modules.smscenter.utils.SignUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.sanerzone.common.support.utils.SystemClock;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.utils.Base64Util;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.IPUtils;
import com.siloyou.core.common.utils.StreamUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.MsgId;
import com.siloyou.jmsg.common.utils.RuleUtils;
import com.siloyou.jmsg.modules.api.common.ValidateUtil;
import com.siloyou.jmsg.modules.api.entity.ApiResultEntity;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.swsms.service.SwSmsTaskService;

@Controller
@RequestMapping(value = "${apiPath}")
public class SwSmsApiController extends BaseController
{
    
    public static Logger logger = LoggerFactory.getLogger(SwSmsApiController.class);
    
    @Autowired
    private SwSmsTaskService SwSmsTaskService;
    
    @Autowired
    private MQUtils mQUtils;
    
    @SuppressWarnings("rawtypes")
    public Map getPostDataMap(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            String charEncoding = request.getCharacterEncoding();
            if (charEncoding == null)
            {
                charEncoding = "UTF-8";
            }
            String respText = StreamUtils.InputStreamTOString(request.getInputStream(), charEncoding);
            if (StringUtils.isNotBlank(respText))
            {
                String jsonString = new String(Base64Util.decode(respText));
                return (Map)JsonMapper.fromJsonString(jsonString, Map.class);
            }
        }
        catch (IOException e)
        {
            
        }
        return null;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value = "swsms/send")
    public String smsSend(HttpServletRequest request, HttpServletResponse response)
    {
    	logger.info("sms api v2: {}", JsonMapper.toJsonString(request.getParameterMap()));
    	
    	long time = System.currentTimeMillis();
    	
        Map map = Maps.newHashMap();
        try
        {
            String userId = request.getParameter("userid");
            String sendtime = request.getParameter("time");
            map = ValidateUtil.validate(userId, request);
            
            if (!StringUtils.equals(String.valueOf(map.get("code")), "0"))
            {//验证不通过
                return result(response, map);
            }
            
            Date sendDatetime = null;
            if (StringUtils.isNotBlank(sendtime))
            {
                sendDatetime = sendDatetime(sendtime);
                if (sendDatetime == null)
                {
                    map.put("code", "8");
                    map.put("msg", "时间格式错误");
                    return result(response, map);
                }
            }
            
            String nocheck = (String)map.get("nocheck");
            String sign = (String)map.get("sign");
            int reviewCount = (Integer)map.get("reviewCount");
            map.remove("nocheck");
            map.remove("sign");
            map.remove("reviewCount");
            
            String phones = request.getParameter("mobile");
            String smsContent = StringEscapeUtils.unescapeHtml4(request.getParameter("msgcontent"));
            
            if (phones.length() > 12000) 	//单次最多 1000个号码个
            {
                map.put("code", "14");
                map.put("msg", "号码个数超过限制");
                return result(response, map);
            }
            smsContent = smsContent.trim();
            
            smsContent = SignUtils.formatContent(smsContent);
            if (StringUtils.isBlank(sign))
			{
//				sign = AccountCacheUtils.getStringValue(userId, "forceSign", "");	//强制签名
            	sign = UserUtils.get(userId).getForceSign();
            	if(StringUtils.isNotBlank(sign)) {
            		smsContent = "【"+sign+"】" + smsContent;
            	}
				
			}
            
            JmsgSmsTask phoneEntity = SwSmsTaskService.queryTextPhone(phones, ",");//获取号码
            
            Set<String> phoneList = phoneEntity.getPhoneList();
            
            int payCount = SwSmsTaskService.findPayCount(smsContent);
            int count = phoneList.size();
            if (count > 0)
            {
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
                //String reviewStatus = null;
                
                if ("2".equals(nocheck))
                {//自动审核
                    if (reviewCount >= count)
                    {
                        nocheck = "1";//免审
                    }
                    else
                    {
                        nocheck = "0";//必审
                    }
                }
                
                //if ("9".equals(reviewStatus) || phoneList.size() > 50 || sendDatetime != null)
                if ("0".equals(nocheck) || phoneList.size() > 50 || sendDatetime != null)
                {
                	
                	String filResult = RuleUtils.filtrate(userId, smsContent);
            		if (!StringUtils.equals(filResult, "T0000"))
            		{
            			logger.info(filResult);
            			map.put("code", "2");
                        map.put("msg", "短信内容匹配规则失败");
                        return result(response, map);
            		}
                	
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
            		
                    String res = SwSmsTaskService.createSmsTask(jmsgSmsTask, Arrays.asList(phones.split(",")), 0, payCount);
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
                }
                else
                {
                	String taskId = new MsgId(1000001).toString();
                	
                	SmsMtMessage message = new SmsMtMessage();
                    message.setSmsType("sms");
                    message.setMsgContent(smsContent);
                    message.setPhone( phones );
                    message.setUserid( userId ); //用户ID
                    message.setTaskid( taskId);
                    message.setSpNumber( sendtermid );
                    message.setUserReportGateWayID("HTTP");
                    message.setUserReportNotify(StringUtils.isNotBlank(UserUtils.get(userId).getCallbackUrl())?"9":"0");
                    message.setSendTime(SystemClock.now());
                    message.setCstmOrderID(request.getParameter("messageid"));
                    
                    // 提交到UMT
                    String topic = "SMSMT";
                    topic = topic + Global.getConfig("product.gateway.port") + "S";
                    
                    String msgid = mQUtils.sendSmsMT(topic, "HTTP", message.getTaskid(), FstObjectSerializeUtil.write(message));
                    logger.info("HttpApi-Submit-MT = taskid:{}, msgid:{}, time:{}",  message.getTaskid(), msgid, (System.currentTimeMillis()-time));
                    
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
    @RequestMapping(value = "swsms/query")
    public String smsSendQuery(HttpServletRequest request, HttpServletResponse response)
    {
        return null;
    }
    
    private String validateBalance(String userId, String sign, HttpServletRequest request, HttpServletResponse response)
    {
        // 校验 userId
        if (StringUtils.isBlank(userId))
        {
            return resultBalance("2", "userid参数不能为空", userId, "0", response);
        }
        // 校验 apikey
        if (StringUtils.isBlank(sign))
        {
            return resultBalance("2", "sign参数不能为空", userId, "0", response);
        }
        
        String ts = request.getParameter("ts");
        if (StringUtils.isBlank(ts))
        {
            return resultBalance("2", "ts参数不能为空", userId, "0", response);
        }
        
        //验证用户是否存在
        User user = UserUtils.getByNow(userId);
        
        if (user == null)
        {
            return resultBalance("4", "用户不存在", userId, "0", response);
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
                    return resultBalance("6", "MD5校验失败", userId, "0", response);
                }
            }
            else
            {
                return resultBalance("7", "IP校验失败", userId, "0", response);
            }
        }
        
        return "0";
    }
    
    //	查询用户余额
    @RequestMapping(value = "swsms/balance")
    public String smsSendBalance(HttpServletRequest request, HttpServletResponse response)
    {
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");
        
        String result = validateBalance(userId, sign, request, response);
        if ("0".equals(result))
        {//验证成功
        	//余额
            String amountKey = AccountCacheUtils.getAmountKey("amount", "sms", userId);
            String money = JedisClusterUtils.get(amountKey);
            return resultBalance("0", "查询成功", userId, money, response);
        }
        else
        {
            return result;
        }
    }
    
    // 查询用户余额
    @RequestMapping(value = "swsms/keyword")
    public String keyword(HttpServletRequest request, HttpServletResponse response)
    {
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");
        
        String result = validateKeyword(userId, sign, request, response);
        if ("0".equals(result))
        {//验证成功
        
            String smsContent = request.getParameter("msgcontent");
            if (StringUtils.isBlank(smsContent))
            {
                return resultKeyword("2", "msgcontent参数不能为空", userId, "", response);
            }
            
            String keywords = KeywordsUtils.keywords(smsContent.trim());
            if (StringUtils.isNotBlank(keywords))
            {
                return resultKeyword("10", "发送内容包含敏感词[" + keywords + "]", userId, keywords, response);
            }
            
            return resultKeyword("0", "查询成功", userId, keywords, response);
        }
        else
        {
            return result;
        }
    }
    
    private String validateKeyword(String userId, String sign, HttpServletRequest request, HttpServletResponse response)
    {
        // 校验 userId
        if (StringUtils.isBlank(userId))
        {
            return resultKeyword("2", "userid参数不能为空", userId, "", response);
        }
        // 校验 apikey
        if (StringUtils.isBlank(sign))
        {
            return resultKeyword("2", "sign参数不能为空", userId, "", response);
        }
        
        String ts = request.getParameter("ts");
        if (StringUtils.isBlank(ts))
        {
            return resultKeyword("2", "ts参数不能为空", userId, "", response);
        }
        
        //验证用户是否存在
        User user = UserUtils.getByNow(userId);
        
        if (user == null)
        {
            return resultKeyword("4", "用户不存在", userId, "", response);
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
                    return resultKeyword("6", "MD5校验失败", userId, "", response);
                }
            }
            else
            {
                return resultKeyword("7", "IP校验失败", userId, "", response);
            }
        }
        
        return "0";
    }
    
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String result(HttpServletResponse response, Map map)
    {
        String code = StringUtils.valueof(map.get("code"));
        String msg = StringUtils.valueof(map.get("msg"));
        String taskid = StringUtils.valueof(map.get("taskid"));
        
        /*StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        					.append("<result statustext=\""+msg+"\" status=\""+code+"\">")
        					.append("<taskid>"+taskid+"</taskid>")
        					.append("</result>");*/
        
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        if (StringUtils.equals(code, "0"))
        {
            Map<String, String> tmp = Maps.newHashMap();
            tmp.put("taskid", taskid);
            entity.setData(tmp);
        }
        
        //return renderString(response, sb.toString());
        return renderString(response, entity);
        
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resultBalance(String code, String msg, String userid, String balance, HttpServletResponse response)
    {
        /*StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                            .append("<result statustext=\""+msg+"\" status=\""+code+"\">")
                            .append("<userid>"+userid+"</userid>")
                            .append("<balance>"+balance+"</balance>")
                            .append("</result>");*/
        
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        Map<String, String> map = Maps.newHashMap();
        map.put("balance", balance);
        entity.setData(map);
        
        //return renderString(response, sb.toString(), "text/plain");
        return renderString(response, entity);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resultKeyword(String code, String msg, String userid, String keywords, HttpServletResponse response)
    {
        
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        if (StringUtils.isNotBlank(keywords))
        {
            Map<String, String> map = Maps.newHashMap();
            map.put("keywords", keywords);
            entity.setData(map);
        }
        
        return renderString(response, entity);
    }
    
    public static Date sendDatetime(String sendTime)
    {
        Date result = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            result = sdf.parse(sendTime);
        }
        catch (Exception err)
        {
            logger.debug("时间格式错误: " + err.getMessage());
        }
        return result;
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "swsms/report")
    public String reportTest(HttpServletRequest request, HttpServletResponse response)
    {
    	Map map = getPostDataMap(request, response);
    	System.out.println("=========================>"+JsonMapper.toJsonString(map));
    	return renderString(response, "ok", "application/json");
    }
}
