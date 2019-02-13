package com.siloyou.jmsg.modules.api.common;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.IPUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.dao.UserDao;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.SignUtils;

public class ValidateUtil
{
    private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, String> validate(String userId, HttpServletRequest request)
    {
        Map map = Maps.newHashMap();
        
        // 校验 userId
        if (StringUtils.isBlank(userId))
        {
            map.put("code", "2");
            map.put("msg", "userid参数不能为空");
            return map;
        }
        
        String sendtermid = request.getParameter("extnum");//扩展号
        if (StringUtils.isNotBlank(sendtermid))
        {
            if (sendtermid.length() > 6)
            {
                map.put("code", "9");
                map.put("msg", "扩展号格式错误");
                return map;
            }
        }
        
        /*String sendtime = request.getParameter("time");//发送时间
        if(StringUtils.isBlank(sendtime)){
            map.put("code", "2");
            map.put("msg", "time参数不能为空");
            return map;
        }*/
        
        String phones = request.getParameter("mobile");//号码
        if (StringUtils.isBlank(phones))
        {
            map.put("code", "2");
            map.put("msg", "mobile参数不能为空");
            return map;
        }
        
        String md5 = request.getParameter("sign");//签名
        if (StringUtils.isBlank(md5))
        {
            map.put("code", "2");
            map.put("msg", "sign参数不能为空");
            return map;
        }
        
        String ip = IPUtils.getIpAddr(request);
        String smsContent = request.getParameter("msgcontent");
        if (StringUtils.isBlank(smsContent))
        {
            map.put("code", "2");
            map.put("msg", "msgcontent参数不能为空");
            return map;
        }
        
        String ts = request.getParameter("ts");
        if (StringUtils.isBlank(ts))
        {
            map.put("code", "2");
            map.put("msg", "ts参数不能为空");
            return map;
        }
        
        //Date tsDate = sendDatetime(ts);
        Date now = new Date();
        //long between = (now.getTime() - tsDate.getTime()) / (1000 * 60);
        long between = (now.getTime() - Long.parseLong(ts)) / (1000 * 60);
        if (between > 5)
        {
            map.put("code", "14");
            map.put("msg", "发送超时");
            return map;
        }
        
        //User user = userDao.get(userId);
        User user = UserUtils.get(userId);
        if (user == null){
			//用户不存在
            map.put("code", "4");
            map.put("msg", "用户不存在");
        }else{
            if("1".equals(user.getFilterWordFlag())){//过滤敏感词
				//全局关键字
	        	String keywords = KeywordsUtils.keywords(smsContent.trim());
	            if (StringUtils.isNotBlank(keywords)){
	                map.put("code", "10");
	                map.put("msg", "发送内容包含敏感词[" + keywords + "]");
	                return map;
	            }
            }

            if (!KeywordsUtils.exits(user.getKeyword(), smsContent)) {
                //TODO 未包含用户关键字
                //map.put("code", "15");
                //map.put("msg", "短信内容未包含用户关键字");
                //return map;
            }else{
                //用户关键字
                map.put("code", "-15");//错误代码暂时用-15
                map.put("msg", "短信内容包含用户关键字");
                return map;
            }
            
            String smsSign = SignUtils.get(smsContent);//短信签名
            
            String whiteIP = user.getWhiteIP();
            if (StringUtils.isBlank(whiteIP) || ("," + whiteIP + ",").indexOf("," + ip + ",") >= 0)
            {//验证IP
                String apikey = user.getApikey();
                String myMD5 = HttpRequest.md5(userId + ts + apikey);//MD5加密 md5(userid + ts + apikey)
                if (md5.equals(myMD5))
                {//MD5验证
                    map.put("code", "0");//验证通过
                    map.put("msg", "提交成功");
                    map.put("nocheck", user.getNoCheck());
                    map.put("reviewCount", user.getReviewCount());
                    map.put("sign", smsSign);
                }
                else
                {
                    map.put("code", "6");
                    map.put("msg", "sign校验失败");
                }
            }
            else
            {
                map.put("code", "7");
                map.put("msg", "IP校验失败");
            }
        }
        
        return map;
    }
    
}
