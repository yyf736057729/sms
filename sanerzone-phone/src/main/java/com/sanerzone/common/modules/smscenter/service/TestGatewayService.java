package com.sanerzone.common.modules.smscenter.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrlPattern;

import java.util.Map;

import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.modules.phone.utils.BlacklistUtils;
import com.sanerzone.common.modules.phone.utils.PhoneUtils;
import com.sanerzone.common.modules.smscenter.entity.GatewayResult;
import com.sanerzone.common.modules.smscenter.utils.SignUtils;
import com.sanerzone.common.modules.smscenter.utils.TestGatewayUtils;
import com.sanerzone.common.support.config.Global;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.sanerzone.common.support.utils.StringUtils;

public class TestGatewayService {
	
	public void testGateway(String userId, String phone,String smsContent){
		String appCode = Global.getConfig("appcode");//应用类型
		String msg = createSendDetail(userId, phone, smsContent);
		JedisClusterUtils.hset("testGateway", appCode, msg);
	}

	public String createSendDetail(String userId, String phone,String smsContent){
		
		StringBuffer sb = new StringBuffer();
		
		//String userName = AccountCacheUtils.getStringValue(userId, "name", "");
		int signFlag = AccountCacheUtils.getIntegerValue(userId, "signFlag", -1);
		String groupId = AccountCacheUtils.getStringValue(userId, "groupId", "");
		int sysBlackListFlag = AccountCacheUtils.getIntegerValue(userId, "sysBlackListFlag", -1);
		int userBlacklistFlag = AccountCacheUtils.getIntegerValue(userId, "userBlacklistFlag", -1);
		String phoneType = "";
		String cityCode = "";
		String sign = SignUtils.get(smsContent);//签名
		
		sb.append("匹配条件:<br/>用户ID:"+userId+"<br/>手机号码:"+phone+"<br/>校验签名:"+(signFlag == 1 ? "是" : "否")+"<br/>分组ID:"+groupId)
		  .append("<br/>签名:"+sign)
		  .append("<br/>校验系统黑名单:"+(sysBlackListFlag == 1 ? "是" : "否"))
		  .append("<br/>校验营销黑名单:"+(userBlacklistFlag == 1 ? "是" : "否"));
		
		
		Map<String,String> phoneMap = PhoneUtils.get(phone);
		
		if(phoneMap == null||phoneMap.size() <2){
			sb.append("<br/>").append("号段匹配异常,错误码:F0170");
		}else{
			phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
			cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
			sb.append("<br/>运营商:"+phoneType+"<br/>省市代码："+cityCode);
			
			if(StringUtils.isBlank(cityCode) || StringUtils.isBlank(phoneType)){
				sb.append("<br/>").append("号段匹配异常，错误码:F0170");
			}else{
				//1：校验 0： 不校验
	            if (sysBlackListFlag == 1 && BlacklistUtils.isExistSysBlackList(phone))
	            {
	                // 判断是否系统黑名单
	                sb.append("<br/>").append("系统黑名单,错误码:F002");
	            }
	            else if (userBlacklistFlag == 1 && BlacklistUtils.isExistBlackList(phone))
	            {
	                // 判断是否营销黑名单
	                sb.append("<br/>").append("营销黑名单,错误码:F008");
	            }
	            else
	            {
	            	GatewayResult gatewayResult = gatewayMap(signFlag,groupId, phoneType, cityCode.substring(0, 2), sign, userId);
					if(gatewayResult.isExists()){
						String spNumber = gatewayResult.getSpNumber();
						if( 0 == signFlag ){
							spNumber = spNumber + userId;
						}
						spNumber = spNumber + "188";
						if(spNumber.length() > 20) {
							spNumber = spNumber.substring(0, 20);
						}
						sb.append("<br/>").append("匹配结果:匹配通道成功,通道代码："+gatewayResult.getGatewayId()).append(",接入号："+spNumber);
					}else{
						sb.append("<br/>").append("匹配结果:匹配通道失败,错误码："+gatewayResult.getErrorCode());
					}
					
					sb.append("<br/>匹配详情:"+gatewayResult.getMsg());
	            }
			}
		}
		
		return sb.toString();
		
	}
	
	//获取通道代码  signFlag 1:验证签名 0:自定义签名 
	private GatewayResult gatewayMap(int signFlag,String groupId,String phoneType,String provinceId,String sign,String userId){
		
		GatewayResult entity = new GatewayResult();
		if(1 == signFlag){
			entity = TestGatewayUtils.getGateway(userId, groupId, phoneType, provinceId, sign);
		}else{
			entity = TestGatewayUtils.getGateway(groupId, phoneType, provinceId);
		}
		
		return entity;
		
	}
}
