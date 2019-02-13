package com.siloyou.jmsg.gateway.smgp.protocol.smgp.util;

//import com.tingfv.sms.common.util.DefaultSequenceNumberUtil;

import com.zx.sms.common.util.DefaultSequenceNumberUtil;

public class SequenceGenerator {

	//取值范围为 0x00000001-0x7FFFFFFF
	public static synchronized long nextSequence() {
		return DefaultSequenceNumberUtil.getSequenceNo();
	}
}
