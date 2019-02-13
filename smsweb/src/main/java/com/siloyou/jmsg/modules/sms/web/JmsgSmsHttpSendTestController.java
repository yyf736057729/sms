/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.web.BaseController;

/**
 * 端口发送测试Controller
 * @author zhukc
 * @version 2017-04-01
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsHttpSendTest")
public class JmsgSmsHttpSendTestController extends BaseController {

	@RequestMapping(value = "init")
	public String init(){
		return "modules/sms/jmsgSmsHttpSendTest";
	}
	
	
	/**
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "send")
	public String send(String url, String userid, String apikey, String phone, String content, HttpServletResponse response) {
        String sendtime = "";
        String ts = String.valueOf(System.currentTimeMillis());
        String md5 = HttpRequest.md5(userid + ts + apikey);
        
//        try {
//			content = URLDecoder.decode(content, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
        
        StringBuilder params = new  StringBuilder();
        params.append("userid=").append(userid);
        params.append("&ts=").append(ts);
        params.append("&sign=").append(md5.toLowerCase());
        params.append("&mobile=").append( phone);
        params.append("&msgcontent=").append( content);
        params.append("&time=").append( sendtime); 
        
        String reStr =  HttpRequest.sendGet(url, params.toString(), null);
        
		
		return renderString(response, reStr);
	}


}