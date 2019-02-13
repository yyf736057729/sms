package com.siloyou.core.test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.yarn.util.StringHelper;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.common.utils.SignUtils;

public class Test {
	
	
	public static String formatContent(String content){
		if(null == content) {
			return "";
		} 
		String sign = "【" + get(content) +"】";
		
		if(sign.length() < 4) {
			return content;
		}
		
		content = content.replace(sign, "");
		content = content.replace("【", "[");
		content = content.replace("】", "]");
		content= sign+content;
		return content;
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
		
		if ((endIdx - startIdx) < 2 || (endIdx - startIdx) > 6) {
			return "";
		}
		
		return content.substring(startIdx, endIdx);
	}

	public static void main(String[] args) {
		
		
		System.out.println("电子密码器aaa".matches("(?=.*(wap.icbcaku.com))(?=.*(电子密码器)).*$"));
		
		if(true) return;
//		System.out.println(Integer.valueOf("Z", 36) - 10);
//		
//		
//		System.out.println(Integer.toString(25+10, 36).toUpperCase());
		
//		String phone = "123456789";
//		int length = 9;
//		int maxLength = phone.length();
//		if(maxLength > length){
//			phone = phone.substring(length,maxLength);
//			System.out.println(phone);
//		}else{
//			System.out.println("长度错误");
//		}
//		phone = phone.replaceFirst("345","");
//		System.out.println(phone);
		
//		String phones = "12121212121,";
//		String[] array = phones.split(",");
//		for (String phone : array) {
//			System.out.println("phone:"+phone);
//		}
//		
//		System.out.println(array.length);
		
		
//		long st = System.currentTimeMillis();
//		String content = "课表今天从第一节开始上课，0节串到第四节，周知。(关注微信公众号:校园网)【校园2网】原发课表今天从第一节开始上课，0节串到第四节，周知。(关注微信公众号:校园网)若有若无AD发顺丰【答名2】渝中区；小雁塔【校园网w】";
//		Pattern p1 = Pattern.compile("^【(.*?)】");
//        Matcher m = p1.matcher(content);
//        
//        while(m.find()) {
//        	System.out.println(m.group(1));
//        }
	        
//		System.out.println(get(content));
//		
//		System.out.println(System.currentTimeMillis() - st);
	        
//	        System.out.println("util:"+SignUtils.get(s));
//		String userid="3847";
//		String phones="13175130158,13666672546";
//		String pwd="123456";
//		String md5 = HttpRequest.md5(userid+"||"+phones+"||"+pwd);
//		String content="彩秀发送【彩秀普通】";
//		String url="http://114.55.137.104:8081";
//		int smstype=1;
//		String sendtermid="1234";
//		String sendtime="1";
//		
//		String result = send(url, userid, smstype, sendtermid, sendtime, phones, content, md5);
//		System.out.println("result:"+result);
//		List<String> list =Lists.newArrayList();
//		list.add("123");
//		list.add("456");
//		list.add("789");
//		String smsContent = "";
//		int maxParam = list.size();
//		String template = "{3}验证码{1},确实是{2},还是不太确定";
//		if(template.indexOf(("{"+maxParam+"}")) >= 0){
//    		for (int i=1;i<=maxParam;i++) {
//    			template = template.replace("{"+i+"}", list.get(i-1));
//			}
//    	}
//		
//		System.out.println(template);
		//test();
//		BigDecimal bd = null;
//		System.out.println(bd.compareTo(new BigDecimal(1)));
//		System.out.println(bd.negate());
//		double dM = 3000000;
//		double dP = 4.5;
//		int i = (int)(dM%dP == 0 ? dM/dP : 1+dM/dP);
//		System.out.println(i);
		String s = "F2111F23434";
		System.out.println(s.replaceFirst("F2", ""));
		
		String smsContent = "【桐庐停车服务】车主朋友们：1月1日起桐庐实施道路停车无人收费模式，1、下载“好停车”http://t.cn/RNUhzc6，自助停车8折起；2、打开支付宝“车主服务”开通免密支付自动扣费； 3、前两种方法未启动，可找巡检员缴费。详询：0571-69960663 退订回T";
		System.out.println(smsContent.length());
		SmsTextMessage sms = new SmsTextMessage(smsContent, SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
		System.out.println(sms.getPdus().length);
		
		SmsTextMessage sms2 = new SmsTextMessage(smsContent);
		System.out.println(sms2.getPdus().length);
		
		try {
			System.out.println(smsContent.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	private static String send(String url,String userid,int smstype,String sendtermid,String sendtime,String phones,String content,String md5){
//    	return HttpRequest.sendPost(url+"/api/sms/send?userid="+userid+"&smstype="+smstype+"&sendtermid="+sendtermid+
//    			"&sendtime="+sendtime+"&phones="+phones+"&content="+content+"&md5="+md5, null, null);
//
//	}
	
	private static void test(){
		int taskType = 1;
		int index = 0;
		while(true){
			index++;
			System.out.println(index);
			int amount = index;
			if(taskType == 1){//点对点
				if (amount > 1000){
					System.out.println("amount:"+amount);
					taskType = 0;
					if(taskType == 1){
						
					}else{
						return;
					}
	            }
			}
			//System.out.println("amount:"+amount);
			
			String j = "数据：";
			System.out.println(j+index);

		}
		
		

	}
}
