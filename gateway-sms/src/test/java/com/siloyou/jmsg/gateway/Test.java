package com.siloyou.jmsg.gateway;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmsTextMessage;

import com.alibaba.fastjson.JSON;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;

import ch.qos.logback.core.net.SyslogOutputStream;

public class Test {
	public static void main(String[] args) {
		String sms = "NomancanmakeagoodcoatwithbadclothNomancanmakeagoodcoatwithbadclothNomancanmakeagoodcoatwithbadclothNomancanmakeagoodcoatwithbadclothNomancaB";
		SmsTextMessage message = new SmsTextMessage(sms);
		System.out.println(sms.length());
		System.out.println(message.getPdus().length);
		
//		System.out.println(String.valueOf(""));
		
//		short i = (short) 1;
//		System.out.println( i == 1);
//		
//		String msgContent = "【百世快递】您的快递50457608301332正由樊金路派送中，请保持手机畅通，如不方便接收快件请联系15041724236";
//			
//			int startIdx = 0;
//			int endIdx = 0;
//			if(msgContent.startsWith("【")) {
//				startIdx = 1;
//				endIdx = msgContent.indexOf("】");
//			}
//			
//			// 签名要大于等于2且小于等于10 
//			if ( !((endIdx - startIdx) < 2 || (endIdx - startIdx) > 10)) {
//				String sign = msgContent.substring(startIdx - 1, endIdx + 1);
//				try {
//					int signLen = sign.getBytes("ISO-10646-UCS-2").length;
//					
//					System.out.println(sign + ":" +signLen);
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
		
//		Map<String, Serializable> storedMap = BDBStoredMapFactoryImpl.INS.buildMap("YD8006", "Report_YD8006");
//		
//		System.out.println(storedMap.size());
//		int idx = 0;
//		Iterator<String> it = storedMap.keySet().iterator();
//		while(it.hasNext()) {
//			idx ++;
//			System.out.println(idx);
//			try {
//				String key = it.next();
////				FileUtils.writeStringToFile(new File("/Users/mac/Desktop/YD8006.txt"), key+"="+JSON.toJSONString(storedMap.get(key)) + "\n", true);
//				
//				FileUtils.writeStringToFile(new File("/Users/mac/Desktop/YD8006_1.txt"), key + "\n", true);
//				if(idx > 100) break;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
////			if(idx > 10)break;
//		}
		
//		/Users/mac/Desktop

//		try {
//			int i = 0;
//			String encoding = "UTF-8";
//			File file = new File("");
//			if (file.isFile() && file.exists()) { // 判断文件是否存在
//				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
//				BufferedReader bufferedReader = new BufferedReader(read);
//				String lineTxt = null;
//				while ((lineTxt = bufferedReader.readLine()) != null) {
//					if (i % 10000 == 0) {
//						System.out.println(i);
//					}
//				}
//				read.close();
//			} else {
//				System.out.println("找不到指定的文件");
//			}
//		} catch (Exception e) {
//			System.out.println("读取文件内容出错");
//			e.printStackTrace();
//		}

	}
}
