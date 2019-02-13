package com.sanerzone.common.modules.smscenter.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.smscenter.entity.GatewayResult;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.StringUtils;


//通道工具类
public class TestGatewayUtils {
	
	//匹配通道信息 校验签名
	public static GatewayResult getGateway(String userId,String groupId,String phoneType,String provinceId,String sign){
		return getGateway(userId, groupId, phoneType, provinceId, sign, true);
	}
	
	//匹配通道信息  不校验签名
	public static GatewayResult getGateway(String groupId,String phoneType,String provinceId){
		return getGateway("", groupId, phoneType, provinceId, "", false);
	}
	
	public static GatewayResult getGateway(String userId,String groupId,String phoneType,String provinceId,String sign,boolean signFlag){
		GatewayResult result = new GatewayResult();
		result.setErrorCode("F007");//匹配通道失败
		result.setMsg("");
		
		//验证分组是否存在
		if(!com.sanerzone.common.modules.smscenter.utils.GroupUtils.isExists(groupId)){
			result.setErrorCode("F0071");//分组不存在
			result.setMsg("分组"+groupId+"不存在,错误码：F0071");
			return result;
		}
		
		List<String> list = com.sanerzone.common.modules.smscenter.utils.GatewayGroupUtils.get(groupId, phoneType, provinceId);
		if(list != null && list.size() >0){
			String gateMsg = "<br/>通道个数:"+list.size()+"<br/>";
			String ids ="";
			for (String string : list) {
				ids = ids+string+",";
			}	
			gateMsg = gateMsg+"通道列表:"+ids+"<br/>";
			
			result.setMsg(gateMsg);
			for (String gatewayId : list) {
				String idMsg = "通道:"+gatewayId;
				Map<String,String> spMap = getSpNumber(userId, gatewayId, sign, signFlag);//接入号
				String spNumber = spMap.get("spNumber");
				String msg = spMap.get("msg");
				result.setMsg(result.getMsg()+idMsg+msg+"<br/>");
				if(StringUtils.isNotBlank(spNumber)){//配到到通道
					result.setErrorCode("T000");
					result.setGatewayId(gatewayId);
					result.setSpNumber(spNumber);
					return result;
				}
			}
		}else{
			result.setErrorCode("F0072");//通道分组不存在
			result.setMsg("分组:"+groupId+",运营商:"+phoneType+",省份:"+provinceId+",匹配结果:没有获取到通道分组<br/>");
			return result;
		}
		result.setMsg(result.getMsg());
		return result;
		
	}
	
	//是否到匹配通道
	private static Map<String,String> getSpNumber(String userId,String gatewayId,String sign,boolean signFlag){
		Map<String,String> map = Maps.newHashMap();
		String spNumber = "";
		if(signFlag){//验证签名
			spNumber = com.sanerzone.common.modules.smscenter.utils.SignUtils.get(userId, gatewayId, sign);
			if(StringUtils.isBlank(spNumber) || !StringUtils.isNumeric(spNumber)) {
				map.put("spNumber", "");
				map.put("msg", ",用户ID:"+userId+",签名:"+sign+",匹配结果：没有获取到接入号<br/>");
				return map;
			}
		}
		
		JmsgGatewayInfo info = TestGatewayUtils.getGatewayInfo(gatewayId);
		if(info != null && StringUtils.isNotBlank(info.getId())){
			if(!"1".equals(info.getStatus())) {
				map.put("spNumber", "");
				map.put("msg", ",匹配结果:通道停用<br/>");
				return map;
			}
			if(signFlag){
				map.put("spNumber", info.getSpNumber() + spNumber);
				map.put("msg", ",匹配结果:匹配通道成功<br/>");
				return map;
			}else{
				if("1".equals(info.getCustomFlag())){
					map.put("spNumber", info.getSpNumber());
					map.put("msg", ",匹配结果:匹配通道成功<br/>");
					return map;
				}else{
					map.put("spNumber", "");
					map.put("msg", ",匹配结果:匹配通道失败，不支持自定义签名<br/>");
					return map;
				}
			}
		}else{
			map.put("spNumber", "");
			map.put("msg", ",匹配结果:通道不存在<br/>");
			return map;
		}
	}
	
	//获取通道信息
    public static JmsgGatewayInfo getGatewayInfo(String gatewayId){
    	Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, CacheKeys.getCacheGatewayInfoKey(gatewayId));
    	if(obj == null)return null;
    	return (JmsgGatewayInfo)obj;
    }
}
