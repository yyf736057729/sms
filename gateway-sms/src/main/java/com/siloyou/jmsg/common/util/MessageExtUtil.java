package com.siloyou.jmsg.common.util;

import com.alibaba.rocketmq.common.message.MessageExt;

public class MessageExtUtil 
{

	@SuppressWarnings("unchecked")
	public static <T> T convertMessageExt(Class<T> clazz, MessageExt msg)
	{
		try {
			return (T) FstObjectSerializeUtil.read(msg.getBody());
		} catch (Exception e) {
			return null;
		}
	}
}
