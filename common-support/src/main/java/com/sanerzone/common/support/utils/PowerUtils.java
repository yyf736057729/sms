package com.sanerzone.common.support.utils;

import org.apache.shiro.SecurityUtils;
/**
 * 权限工具类
 * @author zhukc
 */
public class PowerUtils {
	
	//管理员权限
	public static boolean adminFlag(){
		return SecurityUtils.getSubject().isPermitted("jmsg:admin");
	}
	
	//代理商权限
	public static boolean agencyFlag(){
		return SecurityUtils.getSubject().isPermitted("jmsg:agency");
	}
}
