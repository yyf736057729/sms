package com.siloyou.jmsg.common.util;

import org.apache.commons.lang3.StringUtils;

import com.zx.sms.common.util.MsgId;

public class TableNameUtil {

	/**
	 * 根据业务ID获取表名下标
	 * @param bizid
	 * @return
	 */
	public static String getTableIndex(String bizid)
	{
		if (StringUtils.isNotBlank(bizid))
		{
			return String.valueOf(new MsgId(bizid).getDay());
		}
		else
		{
			return "";
		}
	}
}
