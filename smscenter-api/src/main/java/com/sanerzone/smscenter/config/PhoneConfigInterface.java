package com.sanerzone.smscenter.config;
/**
 * @param type 1:新增 2:删除
 * @author zhukc
 */
public interface PhoneConfigInterface {
	public boolean configBlacklist(int type,String phone,int value,String[] phones);
	public boolean configPhoneSegment(int type,String phone,String phoneType,String cityCode);
	public boolean configWhitelist(int type,String phone,String[] phones);
}
