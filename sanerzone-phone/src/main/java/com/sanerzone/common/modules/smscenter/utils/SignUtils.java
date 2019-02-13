package com.sanerzone.common.modules.smscenter.utils;

import com.sanerzone.common.modules.smscenter.entity.JmsgGatewaySign;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.StringUtils;


//签名工具类
public class SignUtils {
	
	//格式化内容
	public static String formatContent(String content){
		if(null == content) {
			return "";
		} 
		String sign = "【" + get(content) +"】";
		
		if(sign.length() == 2) {
			return content;
		}
		
		content = content.replace(sign, "").replace("【", "[").replace("】", "]");
		content= sign+content;
		return content;
	}
	
	/**
	 * 获取发送内容，去掉签名
	 * @param content
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String getContent(String content){
	    if (StringUtils.isNotBlank(content)){
	        content = formatContent(content);
	        return content.substring(content.indexOf("】") + 1);
	    }else{
	        return content;
	    }
	}
	
	//获取签名,签名小于2个则返回""，签名必须大于等于2个字符
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
	 * 判断是否验证码短信
	 * @param content
	 * @return
	 */
	public static boolean isSecurityCode(String content)
	{
		boolean result = false;
		if (StringUtils.isBlank(content))
		{
			return result;
		}
		
		//验证码 and (分钟 or 小时 or 秒）
		String strRegex = "^.*验证码{1}.*$";
//		String str1Regex = "^.*((分钟|小时|秒)+).*$";
		//内容中不能加网址
//		String urlRegex = "^((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+";
		//内容中不能加13、14、15、17、18开头的手机号码
//		String phoneRegex = "0?(13|14|15|17|18)[0-9]{9}";
		//内容中不能加电话（7位及以上连续数字）
//		String numRegex = "^.*\\d{7}.*$";
		
		if (content.matches(strRegex) 
//				&& content.matches(str1Regex)
//				&& !content.matches(urlRegex)
//				&& !content.matches(phoneRegex)
//				&& !content.matches(numRegex)
				)
		{
			result = true;
		}
		
		return result;
	}
    
    /**
     * 新增用户通道签名
     * @param gatewaySign
     * @see [类、类#方法、类#成员]
     */
    public static void put(JmsgGatewaySign gatewaySign){
        put(gatewaySign.getUserId(), gatewaySign.getGatewayId(), gatewaySign.getSign(), gatewaySign.getSpNumber());
    }
    
    /**
     * 新增用户通道签名
     * @param userId
     * @param gatewayId
     * @param sign
     * @param spNumber
     * @see [类、类#方法、类#成员]
     */
    public static void put(String userId, String gatewayId, String sign, String spNumber){
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
    public static void put(String key, String spNumber){
        EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, spNumber);
    }
    
    /**
     * 用户通道签名删除
     * @param gatewaySign
     * @see [类、类#方法、类#成员]
     */
    public static void del(JmsgGatewaySign gatewaySign){
        del(gatewaySign.getUserId(), gatewaySign.getGatewayId(), gatewaySign.getSign());
    }
    
    /**
     * 用户通道签名删除
     * @param userId
     * @param gatewayId
     * @param sign
     * @see [类、类#方法、类#成员]
     */
    public static void del(String userId, String gatewayId, String sign){
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
    public static String get(String userId, String gatewayId, String sign){
        Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, CacheKeys.getGatewaySignKey(userId, gatewayId, sign));
        if(obj == null)return null;
        return (String)obj;
    }
}
