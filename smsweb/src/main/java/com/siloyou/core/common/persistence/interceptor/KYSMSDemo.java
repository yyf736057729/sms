package com.siloyou.core.common.persistence.interceptor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class KYSMSDemo {
	public static final String DEF_CHATSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;
	public static final String USERAGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
	
	//public static final String URL = "http://118.178.35.191:8808";
	//public static final String USERID = "用户ID,联系商务获取";
	//public static final String APIKEY = "加密key,联系商务获取";
	
	public static final String URL = "http://127.0.0.1:8083";
	public static final String USERID = "1";
	public static final String APIKEY = "9488e59cd3954224a0ba3c9d49bb1927";

	public static void main(String[] args) {
		//String phone = "手机号码";  //手机号码，多手机号用逗号分隔如 13300000000,13200000000
		String phone = "13588124881";  //手机号码，多手机号用逗号分隔如 13300000000,13200000000
		String content = "【泛圣科技】您的验证码为：731848，有效期为30分钟，非本人发送敬请忽略"; //短信内容(内容前面必须要带上签名如:【泛圣科技】)


		// 查询剩余条数
		//balance();
		
		// 发送短信
		//smsSend(phone, content, "");
		
		// 查询状态报告
		query();
		
	}
	
	/**
	 * 发送短信
	 * @param phones	不能为空，手机号码，多手机号用逗号分隔如 13300000000,13200000000
	 * @param content	不能为空，短信内容(内容前面必须要带上签名如:【宽信科技】)
	 * @param extnum	可为空，发送扩展号，一般用于上行区分业务，需要联系商务配置
	 */
	public static void smsSend(String phones, String content, String extnum) {
		String url = URL + "/api/sms/send";
		String ts = String.valueOf(System.currentTimeMillis());
		String md5 = MD5(USERID + ts + APIKEY);
		
		Map<String, String> params = new  HashMap<String, String>();
        params.put("userid", USERID);
        params.put("ts",ts);
        params.put("sign",md5.toLowerCase());
        params.put("mobile", phones);
        params.put("msgcontent", content);
        params.put("extnum", extnum);
		try {
			String result = net(url, params, "POST");
			
			//TODO 业务逻辑
			System.out.println("响应结果："+result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 剩余条数查询
	 */
    public static void balance(){
    	String url = URL + "/api/sms/balance";
        String ts = String.valueOf(System.currentTimeMillis());
        
        String md5 = MD5(USERID + ts + APIKEY);
        
        Map<String, String> params = new  HashMap<String, String>();
        params.put("userid", USERID);
        params.put("ts",ts);
        params.put("sign",md5.toLowerCase());
		try {
			String result = net(url, params, "POST");
			
			//TODO 业务逻辑
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * 状态报告主动查询接口，需要联系商务配置。
     * 为了实时获取状态报告，建议用户选择推送模式，需提供接收状态报告的URL
     */
    public static void query(){
    	String url = URL + "/api/v2/sms/query";
        String ts = String.valueOf(System.currentTimeMillis());
        
        String md5 = MD5(USERID + ts + APIKEY);
        
        Map<String, String> params = new  HashMap<String, String>();
        params.put("userid", USERID);
        params.put("ts",ts);
        params.put("sign",md5.toLowerCase());
		try {
			String result = net(url, params, "POST");
			
			//TODO 业务逻辑
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	/**
	 *
	 * @param strUrl
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param method
	 *            请求方法
	 * @return 网络请求字符串
	 * @throws Exception
	 */
	public static String net(String strUrl, Map params, String method) throws Exception {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			if (method == null || method.equals("GET")) {
				strUrl = strUrl + "?" + urlencode(params);
			}
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method == null || method.equals("GET")) {
				conn.setRequestMethod("GET");
			} else {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", USERAGENT);
			conn.setUseCaches(false);
			conn.setConnectTimeout(DEF_CONN_TIMEOUT);
			conn.setReadTimeout(DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params != null && method.equals("POST")) {
				try {
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(urlencode(params));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			InputStream is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}

	// 将map型转为请求参数型
	public static String urlencode(Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry i : data.entrySet()) {
			try {
				sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String MD5(String s) {
		if (s == null && "".equals(s)) {
			return null;
		}

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
}