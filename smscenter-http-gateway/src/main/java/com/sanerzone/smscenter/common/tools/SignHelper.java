package com.sanerzone.smscenter.common.tools;


//签名工具类
public class SignHelper {
	
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
	    if (StringHelper.isNotBlank(content)){
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
		
		return content.substring(startIdx, endIdx);
	}
	
	/**
	 * 判断是否验证码短信
	 * @param content
	 * @return
	 */
	public static boolean isSecurityCode(String content)
	{
		boolean result = false;
		if (StringHelper.isBlank(content))
		{
			return result;
		}
		
		//验证码 and (分钟 or 小时 or 秒）
		String strRegex = "^.*验证码{1}.*$";
//		String str1Regex = "^.*((分钟|小时|秒)+).*$";
		//内容中不能加网址
		String urlRegex = "^((https|http|ftp|rtsp|mms)?:\\/\\/)[^\\s]+";
		//内容中不能加13、14、15、17、18开头的手机号码
		String phoneRegex = "0?(13|14|15|17|18)[0-9]{9}";
		//内容中不能加电话（7位及以上连续数字）
		String numRegex = "^.*\\d{7}.*$";
		
		if (content.matches(strRegex) 
				&& !content.matches(urlRegex)
				&& !content.matches(phoneRegex)
				&& !content.matches(numRegex))
		{
			result = true;
		}
		
		return result;
	}
    
}
