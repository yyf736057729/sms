package com.siloyou.jmsg.common.utils;

import java.util.List;

import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewaySignDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewaySign;


//签名工具类
public class SignUtils {
	
	private static JmsgGatewaySignDao gatewaySignDao = SpringContextHolder.getBean(JmsgGatewaySignDao.class);
	
	public static String formatContent(String content){
		if(null == content) {
			return "";
		} 
		String sign = "【" + get(content) +"】";
		
//		if(sign.length() < 4) {
//			return content;
//		}
		
		content = content.replace(sign, "");
		content = content.replace("【", "[");
		content = content.replace("】", "]");
		content= sign+content;
		return content;
	}
	
	/**
	 * 获取发送内容，去掉签名
	 * @param content
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String getContent(String content)
	{
	    if (StringUtils.isNotBlank(content))
	    {
	        content = formatContent(content);
	        return content.substring(content.indexOf("】") + 1);
	    }
	    else
	    {
	        return content;
	    }
	}
	
	//获取签名,签名大于2个则返回""
	public static String get(String content){
		
		if(null == content){
			return "";
		}
		
		int startIdx = 0;
		int endIdx = 0;
		
		if(content.startsWith("【")) {
			startIdx = 1;
			endIdx = content.indexOf("】");
		} else if(content.endsWith("】")) {
			startIdx = content.lastIndexOf("【") + 1;
			endIdx = content.length() - 1;
		} else {
			return "";
		}
		
		if ((endIdx - startIdx) < 2) {
			return "";
		}
		String result = content.substring(startIdx, endIdx);
		if(StringUtils.isNotBlank(result)){
			return result.trim();
		}
		
		return result;
	}
	
	
	/**
     * 初始化用户通道签名
     * @see [类、类#方法、类#成员]
     */
    public static void initGatewaySign()
    {
        List<JmsgGatewaySign> list = gatewaySignDao.findList(new JmsgGatewaySign());
        
        String userId = null;
        String gatewayId = null;
        String sign = null;
        String key = null;
        
        for (JmsgGatewaySign gatewaySign : list)
        {
            userId = gatewaySign.getUser().getId();
            gatewayId = gatewaySign.getGatewayId();
            sign = gatewaySign.getSign();
            key = CacheKeys.getGatewaySignKey(userId, gatewayId, sign);
            
            EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, gatewaySign.getSpNumber());
        }
    }
    
    /**
     * 刷新用户通道签名
     * @see [类、类#方法、类#成员]
     */
    public static void refreshGatewaySign(String userId)
    {
        JmsgGatewaySign tem = new JmsgGatewaySign();
        tem.setUserId(userId);
        List<JmsgGatewaySign> list = gatewaySignDao.findList(tem);
        
        String uId = null;
        String gatewayId = null;
        String sign = null;
        String key = null;
        
        for (JmsgGatewaySign gatewaySign : list)
        {
            uId = gatewaySign.getUser().getId();
            gatewayId = gatewaySign.getGatewayId();
            sign = gatewaySign.getSign();
            key = CacheKeys.getGatewaySignKey(uId, gatewayId, sign);
            
            EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, gatewaySign.getSpNumber());
        }
    }
    
    /**
     * 新增用户通道签名
     * @param gatewaySign
     * @see [类、类#方法、类#成员]
     */
    public static void put(JmsgGatewaySign gatewaySign)
    {
        put(gatewaySign.getUser().getId(), gatewaySign.getGatewayId(), gatewaySign.getSign(), gatewaySign.getSpNumber());
    }
    
    /**
     * 新增用户通道签名
     * @param userId
     * @param gatewayId
     * @param sign
     * @param spNumber
     * @see [类、类#方法、类#成员]
     */
    public static void put(String userId, String gatewayId, String sign, String spNumber)
    {
        String key = CacheKeys.getGatewaySignKey(userId, gatewayId, sign);
        put(key, spNumber);
    }
    
    /**
     * 新增用户通道签名
     * @param userId
     * @param gatewayId
     * @param sign
     * @param spNumber
     * @see [类、类#方法、类#成员]
     */
    public static void put(String key, String spNumber)
    {
        EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, spNumber);
    }
    
    /**
     * 用户通道签名删除
     * @param gatewaySign
     * @see [类、类#方法、类#成员]
     */
    public static void del(JmsgGatewaySign gatewaySign)
    {
        del(gatewaySign.getUser().getId(), gatewaySign.getGatewayId(), gatewaySign.getSign());
    }
    
    /**
     * 用户通道签名删除
     * @param userId
     * @param gatewayId
     * @param sign
     * @see [类、类#方法、类#成员]
     */
    public static void del(String userId, String gatewayId, String sign)
    {
        String key = CacheKeys.getGatewaySignKey(userId, gatewayId, sign);
        
        EhCacheUtils.remove(CacheKeys.GATEWAY_CACHE, key);
    }
    
    /**
     * 获取用户通道签名的接入号
     * 如果获取不到，则往缓存中放入一个值为：-1的接入号
     * @param userId
     * @param gatewayId
     * @param sign
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String get(String userId, String gatewayId, String sign)
    {
        String key = CacheKeys.getGatewaySignKey(userId, gatewayId, sign);
        
        String spNumber = (String)EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
        
        // 如果缓存中没有，则从数据库获取；如果数据库也没有，则在缓存中加一个值为：-1的接入号
        if (StringUtils.isBlank(spNumber))
        {
            JmsgGatewaySign param = new JmsgGatewaySign();
            param.setUser(new User(userId));
            param.setGatewayId(gatewayId);
            param.setSign(sign);
            
            param = gatewaySignDao.getByParam(param);
            
            if (null != param)
            {
                if (StringUtils.isNotBlank(param.getSpNumber()))
                {
                    spNumber = param.getSpNumber();
                }
                
                put(param);
            }
            else
            {
                spNumber = "-1";
                put(key, spNumber);
            }
        }
        
        return spNumber;
    }
}
