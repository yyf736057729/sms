package com.sanerzone.jmsg.util;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;

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
