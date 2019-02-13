
package com.siloyou.jmsg.common.utils;

/**
 * 
 * 生成msgid
 * @author zhukc
 *
 */
public class MsgIdUtits{
	
	
	public static String msgId(String prefix){
		return prefix+new MsgId().toString();
	}
	
	public static String msgId(){
		return new MsgId().toString();
	}
}
