package com.siloyou.jmsg.modules.api.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.utils.Base64Util;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.IPUtils;
import com.siloyou.core.common.utils.StreamUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.MsgIdUtits;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.api.common.ValidateUtil;
import com.siloyou.jmsg.modules.api.entity.ApiResultEntity;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;

@Controller
@RequestMapping(value = "${apiPath}")
public class WavApiController extends BaseController
{
    
    public static Logger logger = LoggerFactory.getLogger(WavApiController.class);
    
    @Autowired
    private JmsgSmsTaskService jmsgSmsTaskService;
    
    @Autowired
    private JmsgAccountService jmsgAccountService;
    
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
    @RequestMapping(value = "wav/send")
    public String smsSend(HttpServletRequest request, HttpServletResponse response)
    {
        Map map = Maps.newHashMap();
        try
        {
            String userId = request.getParameter("userid");
            String sendtime = request.getParameter("time");
            //map = validate(userId, request);
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
            String smsContent = request.getParameter("msgcontent");
            
            if (phones.length() > 12000)
            {//单次最多 1000个号码个 TODO
                map.put("code", "14");
                map.put("msg", "号码个数超过限制");
                return result(response, map);
            }
            smsContent = smsContent.trim();
            
            JmsgSmsTask phoneEntity = jmsgSmsTaskService.queryTextPhone(phones, ",");//获取号码
            
            Set<String> phoneList = phoneEntity.getPhoneList();
            
            int payCount = jmsgSmsTaskService.findPayCount(smsContent);
            int count = phoneList.size();
            if (count > 0)
            {
                Long money = jmsgAccountService.findUserMoeny(userId, "sms");
                if (money >= count * payCount)
                {
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
                    Map<String, String> dataMap = jmsgSmsTaskService.queryDataId(userId, smsContent, nocheck, "sms");//获取短信ID
                    String dataId = dataMap.get("dataId");
                    if (StringUtils.isBlank(dataId))
                    {
                        map.put("code", "13");
                        map.put("msg", "短信内容正在审核中");
                        return result(response, map);//TODO 短信内容正在审核中
                    }
                    
                    Map<String, String> content = new HashMap<String, String>();
                    content.put("dataid", dataId);
                    content.put("msgContent", smsContent);
                    
                    smsContent = JSON.toJSONString(content);
                    
                    String mq = CacheKeys.getSmsSingleTopic();//任务通道
                    
                    String reviewStatus = dataMap.get("reviewStatus");//审核标识 8:免审 9:待审
                    if ("9".equals(reviewStatus) || phoneList.size() > 50 || sendDatetime != null)
                    {
                        mq = CacheKeys.getSmsBatchTopic();//任务通道
                        
                        Map<String, String> insertMap =
                            jmsgSmsTaskService.insertTask(dataId,
                                reviewStatus,
                                phoneList,
                                smsContent,
                                sendDatetime,
                                userId,
                                phoneList.size(),
                                "API",
                                mq,
                                true,
                                sign,
                                payCount);
                        if ("1".equals(insertMap.get("code")))
                        {
                            map.put("taskid", insertMap.get("taskId"));
                        }
                        else
                        {
                            map.put("code", "1");
                            map.put("msg", "提交失败");
                        }
                        
                    }
                    else
                    {
                        map.putAll(MQSendSms(response, userId, smsContent, dataId, phoneList, sign, map, payCount, mq)); //队列发送短信
                    }
                }
                else
                {
                    map.put("code", "3");
                    map.put("msg", "余额不足");
                    return result(response, map);
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
    
    private Map<String, String> MQSendSms(HttpServletResponse response, String userId, String smsContent,
        String dataId, Set<String> phoneList, String sign, Map<String, String> map, int payCount, String mq)
        throws Exception
    {
        String taskId = MsgIdUtits.msgId("T");//生成任务ID
        Map<String,String> sendMap = jmsgSmsTaskService.createSendDetail(dataId, taskId, userId, phoneList, smsContent, "API", mq,sign,payCount,new Date());
        if("1".equals(sendMap.get("errorCode"))){
            mQUtils.sendSmsMQ(taskId, "SMS_SINGLE_TASK_TOPIC", "single", taskId.getBytes());
        }else{
            map.put("code", "1");
            map.put("msg", "提交失败,系统错误");
            return map;
        }
        map.put("taskid", taskId);
        return map;
    }
    
    //	查询订单状态
    @RequestMapping(value = "wav/query")
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
    @RequestMapping(value = "wav/balance")
    public String smsSendBalance(HttpServletRequest request, HttpServletResponse response)
    {
        String userId = request.getParameter("userid");
        String sign = request.getParameter("sign");
        
        String result = validateBalance(userId, sign, request, response);
        if ("0".equals(result))
        {//验证成功
            String money = String.valueOf(jmsgAccountService.findUserMoeny(userId, "sms"));
            return resultBalance("0", "查询成功", userId, money, response);
        }
        else
        {
            return result;
        }
    }
    
    // 查询用户余额
    @RequestMapping(value = "wav/keyword")
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
        
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        if (StringUtils.equals(code, "0"))
        {
            Map<String, String> tmp = Maps.newHashMap();
            tmp.put("taskid", taskid);
            entity.setData(tmp);
        }
        
        return renderString(response, entity);
        
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String resultBalance(String code, String msg, String userid, String balance, HttpServletResponse response)
    {
        ApiResultEntity entity = new ApiResultEntity();
        entity.setCode(code);
        entity.setMsg(msg);
        Map<String, String> map = Maps.newHashMap();
        map.put("balance", balance);
        entity.setData(map);
        
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
    
}
