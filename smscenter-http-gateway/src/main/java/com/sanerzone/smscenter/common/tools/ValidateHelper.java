package com.sanerzone.smscenter.common.tools;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.google.common.collect.Maps;

public class ValidateHelper{
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, String> validate(String userId, HttpServletRequest request){
        Map map = Maps.newHashMap();
        
        // 校验 userId
        if (StringHelper.isBlank(userId)){
            map.put("code", "2");
            map.put("msg", "userid参数不能为空");
            return map;
        }
        
        String sendtermid = request.getParameter("extnum");//扩展号
        if (StringHelper.isNotBlank(sendtermid)){
            if (sendtermid.length() > 6){
                map.put("code", "9");
                map.put("msg", "扩展号格式错误");
                return map;
            }
        }
        
        String phones = request.getParameter("mobile");//号码
        if (StringHelper.isBlank(phones)){
            map.put("code", "2");
            map.put("msg", "mobile参数不能为空");
            return map;
        }
        
        String md5 = request.getParameter("sign");//签名
        if (StringHelper.isBlank(md5)){
            map.put("code", "2");
            map.put("msg", "sign参数不能为空");
            return map;
        }
        
        String ip = IPHelper.getIpAddr(request);
        String smsContent = request.getParameter("msgcontent");
        if (StringHelper.isBlank(smsContent)){
            map.put("code", "2");
            map.put("msg", "msgcontent参数不能为空");
            return map;
        }
        
        String ts = request.getParameter("ts");
        if (StringHelper.isBlank(ts)){
            map.put("code", "2");
            map.put("msg", "ts参数不能为空");
            return map;
        }
        
        Date now = new Date();
        long between = (now.getTime() - Long.parseLong(ts)) / (1000 * 60);
        if (between > 5){
            map.put("code", "14");
            map.put("msg", "发送超时");
            return map;
        }
        
        String curUserId = AccountCacheHelper.getStringValue(userId, "id", "");
        if (StringHelper.isBlank(curUserId)){//用户不存在
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
        	
        	
            String whiteIP = AccountCacheHelper.getStringValue(userId, "whiteIP", "");
            if (StringHelper.isBlank(whiteIP) || ("," + whiteIP + ",").indexOf("," + ip + ",") >= 0)
            {//验证IP
                String apikey = AccountCacheHelper.getStringValue(userId, "apikey", "");
                String myMD5 = HttpRequest.md5(userId + ts + apikey);//MD5加密 md5(userid + ts + apikey)
                if (md5.equals(myMD5)){//MD5验证
                    map.put("code", "0");//验证通过
                    map.put("msg", "提交成功");
                }else{
                    map.put("code", "6");
                    map.put("msg", "sign校验失败");
                }
            }else{
                map.put("code", "7");
                map.put("msg", "IP校验失败");
            }
        }
        
        return map;
    }
    
}
