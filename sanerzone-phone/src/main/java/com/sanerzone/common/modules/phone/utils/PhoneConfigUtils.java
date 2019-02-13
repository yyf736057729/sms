package com.sanerzone.common.modules.phone.utils;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboService;
import com.sanerzone.smscenter.config.PhoneConfigInterface;

@DubboService(interfaceClass = PhoneConfigInterface.class, cluster = "broadcast")
public class PhoneConfigUtils implements PhoneConfigInterface{
	
	/*
	 * 
	 * @param type 1:系统黑名单 新增		2:系统黑名单删除 
	 * 			   3:群发黑名单新增 		4:群发黑名单删除 
	 * 			   5:营销黑名单新增 		6:营销黑名单删除
	 *             7:验证码黑名单新增   	8:验证码黑名单删除
	 * 
	 * @param phone
	 * @param value
	 * @param phones
	 * @return
	 */
	@Override
	public boolean configBlacklist(int type,String phone,int value,String[] phones) {
		switch (type) {
		case 1:
			BlacklistUtils.put(phones, 0, value);//系统
			break;
		case 2:
			BlacklistUtils.del(phone, 0);
			break;
		case 3:
			BlacklistUtils.put(phones, 1, value);//群发
			break;
		case 4:
			BlacklistUtils.del(phone, 1);
			break;
		case 5:
			BlacklistUtils.put(phones, 2, value);//营销
			break;
		case 6:
			BlacklistUtils.del(phone, 2);
			break;
		case 7:
			BlacklistUtils.put(phones, 3, value);//验证码
			break;
		case 8:
			BlacklistUtils.del(phone, 3);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean configPhoneSegment(int type, String phone,String phoneType,String cityCode) {
		switch (type) {
		case 1:
			PhoneUtils.put(phone, phoneType, cityCode);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean configWhitelist(int type, String phone,String[] phones) {
		switch (type) {
		case 1:
			WhitelistUtils.put(phones);
			break;
		case 2:
			WhitelistUtils.del(phone);
			break;
		default:
			break;
		}
		return false;
	}

}
