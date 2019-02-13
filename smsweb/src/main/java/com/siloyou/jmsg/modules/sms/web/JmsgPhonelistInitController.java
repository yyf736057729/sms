/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.siloyou.core.common.web.BaseController;

/**
 * 黑、白名单初始化Controller
 * @author zhukc
 * @version 2016-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgPhonelist")
public class JmsgPhonelistInitController extends BaseController {
	

	@RequestMapping(value = "init")
	public String init() {
		return "modules/sms/jmsgPhonelistInit";
	}
	
	
	/**
	 * 验证手机号码是否有效
	 * @param oldPhone
	 * @param phone
	 * @return
	 */
	@RequestMapping(value = "loadPhone")
	public String loadPhone(String type,HttpServletResponse response) {
		String msg = "";
//		try{
//			long s = System.currentTimeMillis();
//			if("1".equals(type)){//初始化黑名单
//				BlacklistUtils.initBlacklist();
//				msg = "加载黑名单成功";
//			}else{//初始化白名单
//				WhitelistUtils.initWhitelist();
//				msg = "加载白名单成功";
//			}
//			msg=msg+",耗时:"+(System.currentTimeMillis()-s)/1000+"秒";
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		return renderString(response, msg, "application/json");
	}

}